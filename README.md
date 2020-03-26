# Mikilin 介绍
该框架是对象的属性核查框架。直接对标hibernate.validate，但是却比起功能更多，使用和扩展更简单。秉承大道至简的思想，引入核查器和匹配器机制，可以将各种复杂的匹配变得特别简单。核查器为内置的两种：黑名单和白名单。而匹配器针对各种类型的设定不同匹配策略。大部分的匹配都是基于基本的类型，而复杂类型（集合、map或者自定义类型）又都是由基本类型组成的。框架支持对复杂类型会进行拆解并核查内部的匹配类型进而对复杂类型进行拦截。该框架具有以下特性：

## 功能性：
- 全类型：可以核查所有类型，基本类型，复杂类型，集合和Map等各种有固定属性（泛型暂时不支持）的类型
- 匹配器：对类型的匹配机制：分组、值列表、属性class、指定模型类型、正则表达式、系统回调（扩展）、枚举类型、范围判决（支持时间范围）和表达式语言判决
- 黑白机制：匹配完之后，数据是拒绝还是接收。接收表示只接收匹配的值，为白名单概念。拒绝表示只拒绝匹配的值，为黑名单概念

## 非功能性：
- 零侵入：对代码零侵入，仅作为一个工具类存在
- 易使用：使用超级简单，一个类，两类核查器，三个注解，多种匹配器
- 高性能：所有的核查均是内存直接调用，第一次构建匹配树后，后面就无须重建
- 可扩展：针对一些不好核查的属性，可以通过自定义匹配器属性，也可以使用spring的Bean作为系统匹配器类

## 使用文档
[Mikilin文档](https://persimon.gitbook.io/mikilin/)

# 目录：

* 一、[快速入门](#快速入门)
    * 1.[maven引入](#maven引入)
    * 2.[使用](#使用)
* ​二、[详细介绍](#详细介绍)
    * 1.[核查函数](#核查函数)
    * 2.[注解](#注解)
    * 3.[匹配器](#匹配器)
        * 1.[value: 指定的值匹配](#指定的值匹配)
        * 2.[type: 属性类型匹配](#属性类型匹配)
        * 3.[enumType: 枚举值匹配](#枚举值匹配)
        * 4.[model: 内置类型匹配](#内置类型匹配)
        * 5.[range: 范围匹配](#范围匹配)
            * 1.[数值范围](#数值范围)
            * 2.[时间范围](#时间范围)
            * 3.[集合大小范围](#集合大小范围)
        * 6.[condition: 表达式匹配](#表达式匹配)
        * 7.[regex: 正则表达式匹配](#正则表达式匹配)
        * 8.[judge: 自定义扩展匹配](#自定义扩展匹配)
            * 1.[自定义函数路径匹配](#自定义函数路径匹配)
            * 2.[spring的Bean自定义匹配器](#spring的Bean自定义匹配器)
        * 9.[group: 分组匹配](#分组匹配)
        * 10.[errMsg: 自定义拦截文案](#自定义拦截文案)
        * 11.[accept: 拦截还是拒绝](#拦截还是拒绝)
    * 4.[核查某个属性](#核查某个属性)

# 一、快速入门 <a name="快速入门"></a>
本工具用法极其简单，可以说，只要会用一个注解`Matcher`和一个方法`MkValidators.check(Object obj)`即可。`Matcher`表示匹配器，内部根据accept区分白名单和黑名单，就是只要匹配到注解中的属性，则表示当前的值是可以通过的，否则函数`MkValidators.check(Object obj)`返回失败，并通过`MkValidators.getErrMsgChain`获取所有错误信息或者通过`MkValidators.getErrMsg`获取某一项错误信息。

## maven引入 <a name="maven引入"></a>
已发布到中央仓库，可自行获取
```xml
<dependency>
    <groupId>com.github.simonalong</groupId>
    <artifactId>mikilin</artifactId>
    <!--请替换为最新版本-->
    <version>${latest.release.version}</version>
</dependency>
```

## 使用 <a name="使用"></a>
该框架使用极其简单，如下：给需要拦截的属性添加注解即可
```java
@Data
@Accessors(chain = true)
public class WhiteAEntity {
    
    // 修饰属性name，只允许对应的值为a，b,c和null
    @Matcher(value = {"a","b","c","null"}, errMsg = "输入的值不符合需求")
    private String name;
    private String address;
}
```

在拦截的位置添加核查，这里是做一层核查，在业务代码中建议封装到aop中对业务使用方不可见即可实现拦截
```java
import lombok.SneakyThrows;

@Test
@SneakyThrows
public void test1(){
    WhiteAEntity whiteAEntity = new WhiteAEntity();
    whiteAEntity.setName("d");

    // 可以使用带有返回值的核查
    if (!MkValidators.check(whiteAEntity)) {
        // 输出：数据校验失败-->属性 name 的值 d 不在只可用列表 [null, a, b, c] 中-->类型 WhiteAEntity 核查失败
        System.out.println(MkValidators.getErrMsgChain());
        // 输出：输入的值不符合需求
        System.out.println(MkValidators.getErrMsg());
    }

    // 或者 可以采用抛异常的核查，该api为 MkValidators.check 的带有异常的检测方式
    MkValidators.validate(whiteAEntity);
}
```

从上面用例可以看到该框架使用非常简单。但是框架的功能性却很强大，那么强大在哪，在于注解属性的多样性，后面一一介绍。对于属性这里的核查函数其实就是只有一个，不同的重载

# 详细介绍 <a name="详细介绍"></a>

## 核查函数 <a name="核查函数"></a>

```java
/**
* 核查对象
*/
public boolean check(Object object){}
/**
* 核查对象的某些属性
*/
public boolean check(Object object, String... fieldSet){}
/**
* 根据分组核查属性
*/
public boolean check(String group, Object object) {}
/**
* 核查分组中的对象的某些属性
*/
public boolean check(String group, Object object, String... fieldSet){}
/**
* 返回错误信息链
*/
public String getErrMsgChain() {}
/**
* 获取最后设置错误信息
*/
public String getErrMsg() {}

/**
 * 核查对象失败抛异常
 */
public void validate(Object object) throws MkException

/**
 * 核查对象指定属性失败抛异常
 */
public void validate(Object object, String ...fieldSet) throws MkException

/**
 * 根据组核查对象失败抛异常
 */
public void validate(String group, Object object) throws MkException

/**
 * 根据组核查对象指定属性失败抛异常
 */
public void validate(String group, Object object, String ...fieldSet) throws MkException
```

## 注解 <a name="注解"></a>
```java
/**
* 匹配器
*/
@Matcher
/**
* 多个匹配器，不同的分组
*/
@Matchers
/**
* 复杂对象解析器，修饰属性，只有添加该注解，则复杂的属性，才会进行解析
*/
@Check
```

## 匹配器 <a name="匹配器"></a>
匹配器就是该框架最强大和功能最丰富的的地方，这里根据不同的场景将各种不同的配置都作为属性，每个属性定位是能够匹配该领域的所有类型

```java
@Repeatable(Matchers.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Matcher {

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
     * 如果要匹配值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null，修饰的对象可以为Number类型也可以为String类型，也可以为Boolean类型
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
     * 如果是数值类型，则比较的是数值的范围，使用比如：[a,b]，[a,b)，(a,b]，(a,b)，(null,b]，(null,b)，[a, null), (a, null)
     * 如果是集合类型，则比较的是集合的size大小，使用和上面一样，比如：[a,b]等等
     * 如果是时间类型，可以使用这种，比如["2019-08-03 12:00:32.222", "2019-08-03 15:00:32.222")，也可以用单独的一个函数关键字
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
     * 其中#current表示当前的属性的值，#root表示当前的属性所在的对象，ratioB为该对象的另外一个属性，如上只有在属性ratioA是大于ratioB的时候核查才会拦截
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
     * @return 调用的核查的类和函数对应的表达式，比如："com.xxx.AEntity#isValid"，其中#后面是方法，方法返回boolean或者包装类，其中参数根据个数支持的类型也是不同，参考测试类{@link com.simonalong.mikilin.judge.JudgeCheck}
     */
    String judge() default "";
    
    /**
     * 核查失败后的返回语句
     *
     * @return 核查失败后返回的语句
     */
    String errMsg() default "";

    /**
     * 过滤器模式
     * <p>
     *     其他的属性都是匹配，而该属性表示匹配之后对应的数据的处理，是接受放进来，还是只拒绝这样的数据
     * @return true：accept（放进来），false：deny（拒绝）
     */
    boolean accept() default true;
    
    /**
     * 是否启用核查
     * @return true：禁用核查，false：启用核查
     */
    boolean disable() default false;
}
```

### 指定的值匹配 <a name="指定的值匹配"></a>
```java
@Data
@Accessors(chain = true)
public class WhiteAEntity {
    @Matcher({"a","b","c","null"})
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
    boolean actResult = MkValidators.check(entity)
    if (!actResult) {
        println MkValidators.getErrMsgChain()
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

### 属性类型匹配 <a name="属性类型匹配"></a>
该属性表示修饰的属性的类型只可为指定的类型
```java
@Data
@Accessors(chain = true)
public class TypeEntity {

    /**
     * 没有必要设置type
     */
    @Matcher(type = Integer.class)
    private Integer data;

    @Matcher(type = CharSequence.class)
    private String name;

    @Matcher(type = {Integer.class, Float.class})
    private Object obj;

    @Matcher(type = Number.class)
    private Object num;
}
```
```groovy
def "测试不明写类继承关系1"() {
    given:
    TypeEntity entity = new TypeEntity().setObj(obj)

    expect:
    boolean actResult = MkValidators.check(entity, "obj")
    if (!result) {
        println MkValidators.getErrMsgChain()
    }
    Assert.assertEquals(result, actResult)

    where:
    obj    | result
    'c'    | false
    "abad" | false
    1232   | true
    1232l  | false
    1232f  | true
    12.0f  | true
    -12    | true
}
```

注意：<br/>
1. 如果设置的类型不是属性的类型或者父类则会报错
2. 如果为具体的类型，则再设置与其相同的类型，则没有必要，就像上面的data属性

### 枚举值匹配 <a name="枚举值匹配"></a>
```java
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class JudgeEntity {

    @Matcher(enumType = AEnum.class)
    private String name;

    @Matcher(enumType = {AEnum.class, BEnum.class})
    private String tag;

    @Matcher(enumType = {CEnum.class}, accept = false)
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
    def act = MkValidators.check(judgeEntity)
    Assert.assertEquals(result, act)
    if (!act) {
        println MkValidators.errMsg
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

### 内置类型匹配 <a name="内置类型匹配"></a>
目前内置了常见的几种类型：身份证号、手机号、固定电话、邮箱、IP地址
> ID_CARD ：身份证号 <br/>
> PHONE_NUM ：手机号<br/>
> FIXED_PHONE ：固定电话<br/>
> MAIL ：邮箱<br/>
> IP_ADDRESS： IP地址<br/>
```java
@Data
@Accessors(chain = true)
public class IpEntity {

    @Matcher(model = FieldModel.IP_ADDRESS)
    private String ipValid;
    @Matcher(model = FieldModel.IP_ADDRESS, accept =false)
    private String ipInvalid;
}
```
```groovy
def "IP测试"() {
    given:
    IpEntity entity = new IpEntity().setIpValid(valid).setIpInvalid(invalid)

    expect:
    boolean actResult = MkValidators.check(entity)
    if (!result) {
        println MkValidators.getErrMsgChain()
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

### 范围匹配 <a name="范围匹配"></a>
目前该属性不只是数值类型（Integer, Long, Float, Short, Double等一切数值类型），也支持时间类型，也支持集合类型（集合比较的是集合的大小），范围是用的是数学的开闭写法
#### 数值范围 <a name="数值范围"></a>
```java
@Data
@Accessors(chain = true)
public class RangeEntity4 {

    /**
     * 属性为大于100
     */
    @Matcher(range = "(100, null)")
    private Integer num1;

    /**
     * 属性为大于等于100
     */
    @Matcher(range = "[100, null)")
    private Integer num2;

    /**
     * 属性为大于20且小于50
     */
    @Matcher(range = "(20, 50)")
    private Integer num3;

    /**
     * 属性为小于等于50
     */
    @Matcher(range = "(null, 50]")
    private Integer num4;
    
    /**
     * 属性为大于等于20且小于等于50
     */
    @Matcher(range = "[20, 50]")
    private Integer num5;

    /**
     * 属性为大于等于100，同属性num2一样
     */
    @Matcher(range = "[100, )")
    private Integer num6;
    
    /**
     * 属性为大于等于100，同属性num2一样
     */
    @Matcher(range = "[100,)")
    private Integer num7;
    
    /**
     * 属性为小于等于5，同属性num4一样
     */
    @Matcher(range = "(, 50]")
    private Integer num8;
}
```

#### 时间范围 <a name="时间范围"></a>
修饰的类型可以为Date类型，也可以为Long类型
```java
@Data
@Accessors(chain = true)
public class RangeTimeEntity {

    /**
     * 属性为：2019-07-13 12:00:23.321 到 2019-07-23 12:00:23.321的时间
     */
    @Matcher(range = "['2019-07-13 12:00:23.321', '2019-07-23 12:00:23.321']")
    private Date date1;

    /**
     * 属性为：2019-07-13 12:00:23.000 到 2019-07-23 12:00:00.000 的时间
     */
    @Matcher(range = "['2019-07-13 12:00:23', '2019-07-23 12:00']")
    private Date date2;
    
    /**
     * 属性为：2019-07-13 00:00:00.000 到 2019-07-01 00:00:00.000 的时间
     */
    @Matcher(range = "['2019-07-13', '2019-07']")
    private Long dateLong3;
        
    /**
     * 属性为：现在时间 到 2019-07-23 12:00:23.321 的时间
     */
    @Matcher(range = "(now, '2019-07-23 12:00:23.321']")
    private Date date4;
    
    /**
     * 属性为：2019-07-13 00:00:00.000 到现在的时间
     */
    @Matcher(range = "['2019-07-13', now)")
    private Date date5;
    
    /**
     * 属性为：过去的时间，同下面的past
     */
    @Matcher(range = "(null, now)")
    private Date date6;
    
    /**
     * 属性为：过去的时间，同下面的past
     */
    @Matcher(range = "('null', 'now')")
    private Date date7;
    
    /**
     * 属性为：过去的时间，同上
     */
    @Matcher(range = "past")
    private Date date8;
    
    /**
     * 属性为：未来的时间，同下面的future
     */
    @Matcher(range = "(now, null)")
    private Date date9;
    
    /**
     * 属性为：未来的时间，同下面的future
     */
    @Matcher(range = "future")
    private Date date10;
}
```

#### 集合大小范围 <a name="集合大小范围"></a>
集合这里只核查集合的数据大小
```java
@Data
@Accessors(chain = true)
public class CollectionSizeEntityA {

    private String name;

    /**
    * 对应集合的个数不为空，且个数小于等于2 
    */
    @Matcher(range = "(0, 2]")
    private List<CollectionSizeEntityB> bList;
}
```

### 表达式匹配 <a name="表达式匹配"></a>
这里的表达式只要是任何返回Boolean的表达式即可，框架提供两个占位符，#current和#root，其中#current表示当前属性的值，#root表示的是当前属性所在的对象的值，可以通过#root.xxx访问其他的属性。该表达式支持java中的任何符号操作，此外还支持java.lang.math中的所有静态函数，比如：min、max和abs等等
```java
@Data
@Accessors(chain = true)
public class ConditionEntity {

    /**
    * 当前属性和属性num3的值大于100 
    */
    @Matcher(condition = "#current + #root.num2 > 100")
    private Integer num1;

    /**
    * 当前属性的值小于 20 
    */
    @Matcher(condition = "#current < 20")
    private Integer num2;

    /**
    * 当前属性的值大于31并自增 
    */
    @Matcher(condition = "(++#current) >31")
    private Integer num3;
    
    /**
    * 当前属性的值大于31并自增 
    */
    @Matcher(condition = "(++#current) >31")
    private Integer num4;
    
    /**
    * 其中某个属性为true 
    */
    @Matcher(condition = "#root.judge")
    private Integer age;

    private Boolean judge;
    
    /**
    * 当前值和另外值的最小值大于第三个值 
    */
    @Matcher(condition = "min(#current, #root.num6) > #root.num7")
    private Integer num5;
    private Integer num6;
    private Integer num7;
}
```

### 正则表达式匹配 <a name="正则表达式匹配"></a>
```java
@Data
@Accessors(chain = true)
public class RegexEntity {

    @Matcher(regex = "^\\d+$")
    private String regexValid;

    @Matcher(regex = "^\\d+$", accept = false)
    private String regexInValid;
}
```

### 自定义扩展匹配 <a name="自定义扩展匹配"></a>
上面都是系统内置的一些匹配，如果用户想自定义匹配，可以自行扩展，需要通过该函数指定一个全限定名的类和函数指定即可，目前支持的参数类型有如下几种，比如
#### 自定义函数路径匹配 <a name="自定义函数路径匹配"></a>
```java
@Data
@Accessors(chain = true)
public class JudgeEntity {

    /**
    * 外部定义的匹配器，只传入属性的参数
    */
    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ageValid")
    private Integer age;

    /**
    *  外部定义的匹配器，传入属性所在对象本身，也可传入属性的参数类型
    */
    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;

    /**
    * 这里自定义的第一个参数是属性本身，第二个参数是框架的上下文（用户填充匹配成功或者失败的信息） 
    */
    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#twoParam")
    private String twoPa;

    /**
    * 这里自定义的第一个参数是属性所在对象，第二个是属性本身，第三个参数是框架的上下文（用户填充匹配成功或者失败的信息） 
    */
    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#threeParam")
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

#### spring的Bean自定义匹配器 <a name="spring的Bean自定义匹配器"></a>
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

### 分组匹配 <a name="分组匹配"></a>
上面看到，每个属性只有一种核查规则，但是如果我们要在不同的场景中使用不同的规则，那么这个时候应该怎么办呢，分组就来了，新增一个注解`Matchers`
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Matchers {

    Matcher[] value();
}
```
使用时候，通过属性中的group进行定义不同的规则，核查的时候，采用函数`MkValidators.check(String group, Object obj)`进行核查，如果采用`MkValidators.check(Object obj)`则采用默认的组，即下面的没有设置组的匹配规则
```java
@Data
@Accessors(chain = true)
public class GroupEntity {

    @Matchers({
        @Matcher(range = "[50, 100]", accept = false),
        @Matcher(group = "test1", range = "[12, 23]", accept = false),
        @Matcher(group = "test2", range = "[1, 10]", accept = false)
    })
    private Integer age;

    @Matchers({
        @Matcher(value = {"beijing", "shanghai", "guangzhou"}),
        @Matcher(group = "test1", value = {"beijing", "shanghai"}),
        @Matcher(group = "test2", value = {"shanghai", "hangzhou"})
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
    def act = MkValidators.check("test1", entity);
    Assert.assertEquals(result, act)
    if (!act) {
        println MkValidators.errMsg
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

### 自定义拦截文案 <a name="自定义拦截文案"></a>
errMsg是用于在当前的数据被拦截之后的输出，比如刚开始的介绍案例，如果
```java
@Data
@Accessors(chain = true)
public class WhiteAEntity {
    
    // 修饰属性name，只允许对应的值为a，b,c和null
    @Matcher(value = {"a","b","c","null"}, errMsg = "输入的值不符合需求")
    private String name;
    private String address;
}
```

在拦截的位置添加核查，这里是做一层核查，在业务代码中建议封装到aop中对业务使用方不可见即可实现拦截
```java
import lombok.SneakyThrows;

@Test
@SneakyThrows
public void test1(){
    WhiteAEntity whiteAEntity = new WhiteAEntity();
    whiteAEntity.setName("d");

    // 可以使用带有返回值的核查
    if (!MkValidators.check(whiteAEntity)) {
        // 输出：数据校验失败-->属性 name 的值 d 不在只可用列表 [null, a, b, c] 中-->类型 WhiteAEntity 核查失败
        System.out.println(MkValidators.getErrMsgChain());
        // 输出：输入的值不符合需求
        System.out.println(MkValidators.getErrMsg());
    }

    // 或者 可以采用抛异常的核查，该api为 MkValidators.check 的带有异常的检测方式
    MkValidators.validate(whiteAEntity);
}
```
如果我没不写errMsg，如下这种，那么返回值为系统默认的错误信息，比如
```java
@Data
@Accessors(chain = true)
public class WhiteAEntity {
    
    // 修饰属性name，只允许对应的值为a，b,c和null
    @Matcher(value = {"a","b","c","null"})
    private String name;
    private String address;
}
```
执行结果
```java
import lombok.SneakyThrows;

@Test
@SneakyThrows
public void test1(){
    WhiteAEntity whiteAEntity = new WhiteAEntity();
    whiteAEntity.setName("d");

    // 可以使用带有返回值的核查
    if (!MkValidators.check(whiteAEntity)) {
        // 输出：数据校验失败-->属性 name 的值 d 不在只可用列表 [null, a, b, c] 中-->类型 WhiteAEntity 核查失败
        System.out.println(MkValidators.getErrMsgChain());
        // 输出：属性 name 的值 d 不在只可用列表 [null, a, b, c] 中
        System.out.println(MkValidators.getErrMsg());
    }

    // 或者 可以采用抛异常的核查，该api为 MkValidators.check 的带有异常的检测方式
    MkValidators.validate(whiteAEntity);
}
```

### 拦截还是拒绝 <a name="拦截还是拒绝"></a>
该属性表示匹配后的数据是接收，还是拒绝，如果为true表示接收，则表示只接收按照匹配器匹配的数据，为白名单概念。如果为false，则表示值拒绝对于匹配到的数据，为黑名单概念。白名单就不再介绍，这里介绍下为false情况
```java
@Data
@Accessors(chain = true)
public class DenyEntity {

    @Matcher(value = {"a", "b", "null"}, accept = false)
    private String name;
    @Matcher(range = "[0, 100]", accept = false)
    private Integer age;
}
```
拦截用例
```groovy
def "测试指定的属性age"() {
    given:
    DenyEntity entity = new DenyEntity().setName(name).setAge(age)

    expect:
    def act = MkValidators.check(entity);
    Assert.assertEquals(result, act)
    if (!act) {
        println MkValidators.errMsgChain
    }

    where:
    name | age | result
    "a"  | 0   | false
    "b"  | 89  | false
    "c"  | 100 | false
    null | 200 | false
    "d"  | 0   | false
    "d"  | 200 | true
}
```

## 核查某个属性 <a name="核查某个属性"></a>
上面我们说到，可以核查整个对象，但是如果我们只想核查对象中的某几个属性，那么应该怎么办呢，这里增加了这么个方法`check(Object object, String... fieldSet)`，后者为要核查的属性名字

```java
@Data
@Accessors(chain = true)
public class TestEntity {

    @Matcher(value = {"nihao", "ok"}, accept = false)
    private String name;
    @Matcher(range = "[12, 32]")
    private Integer age;
    @Matcher({"beijing", "shanghai"})
    private String address;
}
```

```java
def "测试指定的属性age"() {
    given:
    TestEntity entity = new TestEntity().setName(name).setAge(age)

    expect:
    def act = MkValidators.check(entity, "age");
    Assert.assertEquals(result, act)
    if (!act) {
        println MkValidators.errMsg
    }

    where:
    name     | age | result
    "nihao"  | 12  | true
    "ok"     | 32  | true
    "hehe"   | 20  | true
    "haohao" | 40  | false
}
```
