# Mikilin 介绍

该框架是对象的属性核查框架，通过引入黑白名单和匹配器机制，在可用和不可用两个方面对对象的属性进行拦截，黑名单匹配上是不可用，而白名单是只可用。所有的拦截都是基于基本的类型，而复杂类型（集合、map或者自定义类型）又都是由基本类型组成的，框架支持对复杂类型进行拆解并核查内部的基本类型进而对复杂类型进行拦截。该框架具有以下特性：

## 功能性：
- 全类型：可以核查所有类型，基本类型，复杂类型，集合和Map等各种有固定属性（泛型暂时不支持）的类型
- 匹配器：对类型的匹配机制：分组、值列表、属性class、指定模型类型、正则表达式、系统回调、枚举类型、范围判决（支持时间范围）和表达式语言判决
- 黑白机制：核查机制引入正反的黑白名单机制，白名单表示只要的值，黑名单表示禁用的值

## 非功能性：
- 零侵入：对代码无侵入，仅作为一个工具类存在
- 易使用：使用超级简单，一个类，两种函数，三种注解，多种匹配器
- 高性能：所有的核查均是内存直接调用，第一次构建匹配树后，后面就无须重建
- 可扩展：针对一些不好核查的属性，可以设置自定义匹配，可以通过自定义匹配器类，也可以使用spring的Bean作为系统匹配器类

## 快速入门
本工具用法极其简单，可以说，只要会用一个注解`WhiteMatcher`和一个方法`Checks.check(Object obj)`即可。`WhiteMatcher`表示白名单匹配器，就是只要匹配到注解中的属性，则表示当前的值是可以通过的，否则函数`Checks.check(Object obj)`返回失败，并通过`Checks.getErrMsg`获取错误信息。其中匹配方式，采用的是只要任何一个属性没有匹配上，则认为没有通过。而对于黑名单匹配器，采用的是只要有任何一个匹配上了，则认为没有通过。
> @WhiteMatcher
> Checks.check(Object obj)
> Checks.getErrMsg()

```java
@Repeatable(WhiteMatchers.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WhiteMatcher {

    /**
     * 针对不同场景下所需的匹配模式的不同，默认"_default_"，详见{@link com.simonalong.mikilin.MkConstant#DEFAULT_GROUP}
     * @return 分组
     */
    String[] group() default {MkConstant.DEFAULT_GROUP};

    /**
     * 匹配属性为对应的类型，比如Integer.class，Long.class等等
     */
    Class<?>[] type() default {};

    /**
     * 可用的值， 如果允许值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null
     * @return 只允许的值的列表
     */
    String[] value() default {};

    /**
     * 可用的值对应的类型
     * @return 对应的枚举类型
     */
    FieldModel model() default FieldModel.DEFAULT;

    /**
     * 枚举类型的判断
     *
     * 注意：该类型只用于修饰属性的值为String类型或者Integer类型的属性，String为枚举的Names，Integer是枚举的下标
     *
     * @return 该属性为枚举对应的类，否则不生效
     */
    Class<? extends Enum>[] enumType() default {};

    /**
     * 数据范围的判断
     * <p> 该字段修饰的类型可以为数值类型，也可以为时间类型，也可以为集合类型（集合类型用来测试集合的size个数的范围）
     *
     * @return
     * 如果是数值类型，且位于范围之内，则核查成功，当前支持的核查功能：[a,b]，[a,b)，(a,b]，(a,b)，(null,b]，(null,b)，[a, null), (a, null)
     * 如果是时间类型，则也可以进行比较，比如["2019-08-03 12:00:32.222", "2019-08-03 15:00:32.222")，也可以用单独的一个函数
     * past: 表示过去
     * now: 表示现在
     * future: 表示未来
     * 同时也支持范围中包含函数（其中范围内部暂时不支持past和future，因为这两个函数没有具体的时间），比如：
     * past 跟(nul, now)表示的相同
     * future 跟(now, null)表示的相同
     * 支持具体的范围，比如：("2019-08-03 12:00:32", now)，其中对应的时间类型，目前支持这么几种格式
     * yyyy
     * yyyy-MM
     * yyyy-MM-dd
     * yyyy-MM-dd HH
     * yyyy-MM-dd HH:mm
     * yyyy-MM-dd HH:mm:ss
     * yyyy-MM-dd HH:mm:ss.SSS
     */
    String range() default "";

    /**
     * 数据条件的判断
     *
     * 根据Java的运算符构造出来对应的条件表达式来进行判断，而其中的数据不仅可以和相关的数据做条件判断，还可和当前修饰的类的其他数据进行判断，
     * 其中当前类用#root表示，比如举例如下，对象中的一个属性小于另外一个属性，比如：{@code #current + #root.ratioB + #root.ratioC == 100}
     * 其中#current表示当前的属性，#root表示当前的属性所在的对象，ratioB为该对象的另外一个属性，如上只有在属性ratioA是大于ratioB的时候核查才会拦截
     *
     * @return 用于数据字段之间的条件表达式（即条件结果为true还是false），当前条件支持Java的所有运算符，以及java的所有运算结果为boolean的表达式
     * 算术运算符：{@code  "+"、"-"、"*"、"/"、"％"、"++"、"--"}
     * 关系运算符：{@code "=="、"!="、">"、"<"、">="、"<="}
     * 位运算符：{@code "＆"、"|"、"^"、"~"、"<<"、">>"、">>>"}
     * 逻辑运算符：{@code "&&"、"||"、"!"}
     * 赋值运算符：{@code "="、"+="、"-="、"*="、"/="、"(%)="、"<<="、">>="、"&="、"^="、"|="}
     * 其他运算符：{@code 条件运算符（?:）、instanceof运算符}
     * {@code java.lang.math}中的所有函数，比如：{@code min,max,asb,cell}等等
     */
    String condition() default "";

    /**
     * 可用的值对应的正则表达式
     * @return 对应的正则表达式
     */
    String regex() default "";

    /**
     * 系统自己编码判断
     *
     * @return 调用的核查的类和函数对应的表达式，比如："com.xxx.AEntity#isValid"，其中#后面是方法，方法返回boolean或者包装类，第一个入参为当前Field对应的类型或者子类，第二个入参为属性对应的对象
     */
    String judge() default "";

    /**
     * 是否启用核查
     * @return true：禁用核查，false：启用核查
     */
    boolean disable() default false;
}
```

### 只要指定的值
```java
@Data
@Accessors(chain = true)
public class WhiteAEntity {
    @WhiteMatcher({"a","b","c","null"})
    private String name;
    private String address;
}
```

```groovy
def "只有指定的值才能通过"() {
    given:
    WhiteAEntity entity = new WhiteAEntity()
    entity.setName(name as String)

    expect:
    boolean actResult = Checks.check(entity)
    if (!actResult) {
        println Checks.getErrMsg()
    }
    Assert.assertEquals(result, actResult)

    where:
    name | result
    "a"  | true
    "b"  | true
    "c"  | true
    null | true
    "d"  | false
}
```

### 只要固定的一些枚举
```java
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class JudgeEntity {

    @WhiteMatcher(enumType = AEnum.class)
    private String name;

    @WhiteMatcher(enumType = {AEnum.class, BEnum.class})
    private String tag;

    @BlackMatcher(enumType = {CEnum.class})
    private String invalidTag;
}
```
```java
@Getter
public enum AEnum {
    A1("a1"),
    A2("a2"),
    A3("a3");

    private String name;

    AEnum(String name) {
        this.name = name;
    }
}
```
```groovy
def "枚举类型测试"() {
    given:
    JudgeEntity judgeEntity = new JudgeEntity(name, tag, invalidTag)

    expect:
    def act = Checks.check(judgeEntity)
    Assert.assertEquals(result, act)
    if (!act) {
        println Checks.errMsg
    }

    where:
    name | tag  | invalidTag | result
    "A1" | "A1" | "c"        | true
    "A1" | "B1" | "c"        | true
    "A1" | "B2" | "c"        | true
    "A1" | "B3" | "c"        | true
    "A1" | "A1" | "C1"       | false
    "A1" | "A1" | "C2"       | false
}
```

### 只要匹配指定的内置类型通过
目前内置了常见的几种类型：身份证号、手机号、固定电话、邮箱、IP地址
```java
@Data
@Accessors(chain = true)
public class IpEntity {

    @WhiteMatcher(model = FieldModel.IP_ADDRESS)
    private String ipValid;
    @BlackMatcher(model = FieldModel.IP_ADDRESS)
    private String ipInvalid;
}
```
```groovy
def "IP测试"() {
    given:
    IpEntity entity = new IpEntity().setIpValid(valid).setIpInvalid(invalid)

    expect:
    boolean actResult = Checks.check(entity)
    if (!result) {
        println Checks.getErrMsg()
    }
    Assert.assertEquals(result, actResult)

    where:
    valid             | invalid           | result
    "192.231asdf"     | "192.123.231.222" | false
    "192.231asdf"     | "192.231asdf"     | false
    "192.123.231.222" | "192.231asdf"     | true
    "192.123.231.222" | "192.123.231.222" | false
}
```

### 只要匹配对应范围的数据
目前该属性只是数值类型（Integer, Long, Float, Short, Double等一切数值类型），也支持时间类型，也支持集合类型（集合比较的是集合的大小），范围是用的是数学的开闭写法
```java
@Data
@Accessors(chain = true)
public class RangeEntity4 {

    /**
     * 属性为大于100
     */
    @WhiteMatcher(range = "(100, null)")
    private Integer num1;

    /**
     * 属性为大于等于100
     */
    @WhiteMatcher(range = "[100, null)")
    private Integer num2;

    /**
     * 属性为大于20且小于50
     */
    @WhiteMatcher(range = "(20, 50)")
    private Integer num3;

    /**
     * 属性为小于等于50
     */
    @WhiteMatcher(range = "(null, 50]")
    private Integer num4;
    
    /**
     * 属性为大于等于20且小于等于50
     */
    @WhiteMatcher(range = "[20, 50]")
    private Integer num5;
}
```
#### 表示时间类型
修饰的类型可以为Date类型，也可以为Long类型
```java
@Data
@Accessors(chain = true)
public class RangeTimeEntity {

    /**
     * 属性为：2019-07-13 12:00:23.321 到 2019-07-23 12:00:23.321的时间
     */
    @WhiteMatcher(range = "['2019-07-13 12:00:23.321', '2019-07-23 12:00:23.321']")
    private Date date1;

    /**
     * 属性为：2019-07-13 12:00:23.000 到 2019-07-23 12:00:00.000 的时间
     */
    @WhiteMatcher(range = "['2019-07-13 12:00:23', '2019-07-23 12:00']")
    private Date date2;
    
    /**
     * 属性为：2019-07-13 00:00:00.000 到 2019-07-01 00:00:00.000 的时间
     */
    @WhiteMatcher(range = "['2019-07-13', '2019-07']")
    private Long dateLong3;
        
    /**
     * 属性为：现在时间 到 2019-07-23 12:00:23.321 的时间
     */
    @WhiteMatcher(range = "(now, '2019-07-23 12:00:23.321']")
    private Date date4;
    
    /**
     * 属性为：2019-07-13 00:00:00.000 到现在的时间
     */
    @WhiteMatcher(range = "['2019-07-13', now)")
    private Date date5;
    
    /**
     * 属性为：过去的时间，同下面的past
     */
    @WhiteMatcher(range = "(null, now)")
    private Date date6;
    
    /**
     * 属性为：过去的时间，同下面的past
     */
    @WhiteMatcher(range = "('null', 'now')")
    private Date date7;
    
    /**
     * 属性为：过去的时间，同上
     */
    @WhiteMatcher(range = "past")
    private Date date8;
    
    /**
     * 属性为：未来的时间，同下面的future
     */
    @WhiteMatcher(range = "(now, null)")
    private Date date9;
    
    /**
     * 属性为：未来的时间，同下面的future
     */
    @WhiteMatcher(range = "future")
    private Date date10;
}
```
#### 集合的范围
集合这里只核查集合的数据大小
```java
@Data
@Accessors(chain = true)
public class CollectionSizeEntityA {

    private String name;

    /**
    * 对应集合的个数不为空，且个数小于等于2 
    */
    @WhiteMatcher(range = "(0, 2]")
    private List<CollectionSizeEntityB> bList;
}
```

### 只要求属性匹配某个表达式匹配
这里的表达式只要是任何返回true的表达式即可，其中提供两个占位符，#current和#root，其中#current表示修饰的当前属性的值，#root表示的是当前属性所在的对象的值，可以通过#root.xxx访问其他的属性。该表达式支持java中的任何符号操作，此外还支持java.util.math中的所有静态函数，比如：min、max和abs等等
```java
@Data
@Accessors(chain = true)
public class ConditionEntity {

    /**
    * 当前属性和属性num3的值大于100 
    */
    @WhiteMatcher(condition = "#current + #root.num2 > 100")
    private Integer num1;

    /**
    * 当前属性的值小于 20 
    */
    @WhiteMatcher(condition = "#current < 20")
    private Integer num2;

    /**
    * 当前属性的值大于31并自增 
    */
    @WhiteMatcher(condition = "(++#current) >31")
    private Integer num3;
    
    /**
    * 当前属性的值大于31并自增 
    */
    @WhiteMatcher(condition = "(++#current) >31")
    private Integer num4;
    
    /**
    * 其中某个属性为true 
    */
    @WhiteMatcher(condition = "#root.judge")
    private Integer age;

    private Boolean judge;
    
    /**
    * 当前值和另外值的最小值大于第三个值 
    */
    @WhiteMatcher(condition = "min(#current, #root.num6) > #root.num7")
    private Integer num5;
    private Integer num6;
    private Integer num7;
}
```

### 只要符合自定义的表达式匹配
```java
@Data
@Accessors(chain = true)
public class RegexEntity {

    @WhiteMatcher(regex = "^\\d+$")
    private String regexValid;

    @BlackMatcher(regex = "^\\d+$")
    private String regexInValid;
}
```

### 用户自定义匹配
上面都是系统内置的一些匹配，如果用户想自定义匹配，可以自行扩展，需要通过该函数指定一个全限定名的类和函数指定即可，目前支持的参数类型有如下几种，比如
```java
@Data
@Accessors(chain = true)
public class JudgeEntity {

    /**
    * 外部定义的匹配器，只传入属性的参数
    */
    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ageValid")
    private Integer age;

    /**
    *  外部定义的匹配器，传入属性所在对象本身，也可传入属性的参数类型
    */
    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;

    /**
    * 这里自定义的第一个参数是属性本身，第二个参数是框架的上下文（用户填充匹配成功或者失败的信息） 
    */
    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#twoParam")
    private String twoPa;

    /**
    * 这里自定义的第一个参数是属性所在对象，第二个是属性本身，第三个参数是框架的上下文（用户填充匹配成功或者失败的信息） 
    */
    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#threeParam")
    private String threePa;
}
```
对应的匹配逻辑，其中匹配函数的入参是上面注解修饰的属性的类型（或者子类）
```java
public class JudgeCheck {

    /**
     * 年龄是否合法
     */
    public boolean ageValid(Integer age) {
        if(null == age){
            return false;
        }
        if (age >= 0 && age < 200) {
            return true;
        }

        return false;
    }
    
    /**
     * 能够传递核查的对象，对于对象中的一些属性可以进行系统内部的配置
     *
     * mRatio + nRatio < 1.0
     */
    private boolean ratioJudge(JudgeEntity judgeEntity, Float nRatio) {
        if(null == nRatio || null == judgeEntity){
            return false;
        }
        return nRatio + judgeEntity.getMRatio() < 10.0f;
    }
    
    /**
     * 两个函数
     */
    private boolean twoParam(String funName, MkContext context) {
        if (funName.equals("hello")){
            context.append("匹配上字段'hello'");
           return true;
        }
        context.append("没有匹配上字段'hello'");
        return false;
    }
    
    /**
     * 三个函数
     */
    private boolean threeParam(JudgeEntity judgeEntity, String temK, MkContext context) {
        if (temK.equals("hello") || temK.equals("word")){
            context.append("匹配上字段'hello'和'word'");
            return true;
        }
        context.append("没有匹配上字段'hello'和'word'");
        return false;
    }
}
```

#### spring的Bean自定义匹配器
上面看到了，我们指定一个全限定路径即可设置过滤器，其实是反射了一个代理类，在真实的业务场景中，我们的bean是用spring进行管理的，因此这里增加了一个通过spring管理的匹配器，如下
使用时候需要在指定为扫描一下如下路径即可

> @ComponentScan(value = "com.simonalong.mikilin.util")

下面的函数对应的参数跟上面非spring时候一样，可以有三种格式
```java
@Service
public class JudgeCls {
​
    // 该引用只是举例
    @Autowire
    private UserSevice userSevice;
​
    /**
     * 年龄是否合法
     */
    public boolean ageValid(Integer age) {
        if(null == age){
            return false;
        }
        if (age >= 0 && age < 200) {
            return true;
        }
​
        return false;
    }
}
```

### 不同场景校验的规则不同
上面看到，每个属性只有一种核查规则，但是如果我们要在不同的场景中使用不同的规则，那么这个时候应该怎么办呢，分组就来了，新增一个注解`WhiteMatchers`
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WhiteMatchers {

    WhiteMatcher[] value();
}
```
使用时候，通过属性中的group进行定义不同的规则，核查的时候，采用函数`Checks.check(String group, Object obj)`进行核查，如果采用`Checks.check(Object obj)`则采用默认的组，即下面的没有设置组的匹配规则
```java
@Data
@Accessors(chain = true)
public class GroupEntity {

    @BlackMatchers({
        @BlackMatcher(range = "[50, 100]"),
        @BlackMatcher(group = "test1", range = "[12, 23]"),
        @BlackMatcher(group = "test2", range = "[1, 10]")
    })
    private Integer age;

    @WhiteMatchers({
        @WhiteMatcher(value = {"beijing", "shanghai", "guangzhou"}),
        @WhiteMatcher(group = "test1", value = {"beijing", "shanghai"}),
        @WhiteMatcher(group = "test2", value = {"shanghai", "hangzhou"})
    })
    private String name;
}
```
用例
```groovy
def "测试指定分组"() {
    given:
    GroupEntity entity = new GroupEntity().setAge(age).setName(name)

    expect:
    def act = Checks.check("test1", entity);
    Assert.assertEquals(result, act)
    if (!act) {
        println Checks.errMsg
    }

    where:
    age | name        | result
    10  | "shanghai"  | true
    12  | "beijing"   | false
    23  | "beijing"   | false
    50  | "beijing"   | true
    100 | "guangzhou" | false
}
```

更多详细用法请见：[Mikilin文档](https://persimon.gitbook.io/mikilin/)