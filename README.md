# Mikilin 介绍

该框架是对象属性的核查框架，通过引入黑白名单机制，能够通过可用和不可用两个方面对对象的属性进行拦截。所有的拦截都是基于基本的类型，而对于复杂类型（集合、map或者自定义类型）都是由基本类型组成的，框架也是支持对复杂类型进行拆解并核查内部的基本类型进而对复杂类型进行拦截。该框架具有以下特性：

### 功能性：
- 全类型：可以核查所有类型，基本类型，复杂类型，集合和Map等各种有固定属性的类型
- 多方式：对类型的核查支持多种方式：值列表、属性类型、正则表达式、系统回调、枚举类型、范围判决 和表达式语言判决
- 黑白机制：核查机制引入正反的黑白名单机制，白名单表示只要的值，黑名单表示禁用的值

### 非功能性：
- 零侵入：对代码无侵入，仅作为一个工具类存在
- 易使用：使用超级简单，三个注解，一个核查函数，一个核查失败函数，多种核查通道
- 高性能：所有的核查均是内存直接调用
- 可扩展：针对一些不好核查的属性，可以设置外部回调，可设置自己定义的核查逻辑

## 目录：

* [一、介绍](#介绍)
    * [一个类](#一个类)
    * [两种函数](#两种函数)
    * [三种注解](#三种注解)
        * [@Check](#@Check)
        * [@FieldWhiteMatchers](#@FieldWhiteMatchers)
        * [@FieldWhiteMatcher](#@FieldWhiteMatcher)
        * [@FieldBlackMatchers](#@FieldBlackMatchers)
        * [@FieldBlackMatcher](#@FieldBlackMatcher)
* [​二、匹配器](#匹配器)
    * [values](#values)
    * [type](#type)
    * [enumType](#enumType)
    * [range](#range)
    * [condition](#condition)
        * [运算符](#运算符)
        * [math的函数](#math的函数)
        * [自定义占位符](#自定义占位符)
    * [regex](#regex)
    * [judge](#judge)
    * [disable](#disable)
* [三、使用](#使用)
    * [基本黑白名单匹配](#基本黑白名单匹配)
    * [复杂类型黑白名单匹配](#复杂类型黑白名单匹配)
    * [指定参数匹配](#指定参数匹配)
    * [分组匹配](#分组匹配)
* [四、注意点](#注意点)
* [五、代码](#代码)
* [六、版本](#版本)

<h1 id="介绍">一、介绍：</h1>

项目对应的jar包已经发布到maven中央仓库中，可以直接使用，目前最低版本是1.4.0，引入即可

```xml
<dependency>
  <groupId>com.github.simonalong</groupId>
  <artifactId>mikilin</artifactId>
  <version>1.4.0</version>
</dependency>
```

该工具在使用方面，采用一个类，两种函数（核查函数和检测函数），三个注解的方式，使用超级简单，但是功能却很多，所有的功能都提供在注解中，下面先简单介绍下。

<h2 id="一个类">一个类：</h2>

该类为`Checks` ，里面只包含如下四个个函数用于核查，两种维度：分组还有属性列表

```java
// 核查复杂对象的所有属性
public boolean check(Object object){}

// 核查复杂对象的部分属性
public boolean check(Object object, String... fieldSet){}

// 分组核查对象
public boolean check(String group, Object object) {}

// 分组核查对象的具体属性
public boolean check(String group, Object object, String... fieldSet) {}
```

<h2 id="两种函数">两种函数：</h2>
这两种函数是核查函数和核查失败后的异常数据获取函数。核查函数就是上面的，而检测函数则更简单，如下：

```java
public String getErrMsg()
```
​
只有在核查失败的时候才需要调用。一般返回的是失败的调用链，比如对象的属性的属性等这种嵌套链。比如：

```text
数据校验失败-->对象值["c"]不在白名单[null, a, b]中
```
​
更复杂一点的打印：

```text
数据校验失败-->属性[name]的值[d]不在白名单[a, b, c, null]中-->自定义类型[WhiteAEntity]核查失败
```

<h2 id="三种注解">三种注解：</h2>
在该工具中只有三种注解：

- `@Check`：针对修饰的复杂属性，进行拆解
- `@FieldWhiteMatcher`和`@FieldBlackMatcher`：黑白名单核查
- `@FieldWhiteMatchers`和`@FieldBlackMatchers`：黑白名单分组核查，内部包含对应的黑白名单核查数组

<h3 id="@Check">@Check</h3>
该注解没有属性，修饰属性，用于表示该属性里面是有待核查的属性，如果不添加，则该属性里面的核查注解无法生效

<h3 id="@FieldWhiteMatcher">@FieldWhiteMatcher</h3>
该注解是白名单注解，修饰属性，表示修饰的属性只接收能匹配上该注解的值，用于对修饰的属性进行核查和筛选，该注解有如下的属性：

- group：分组，默认为\"\_default\_\"，该字段主要是在使用注解`@FieldWhiteMatchers`和`@FieldBlackMatchers`时候使用
- value：值列表
- type：既定的类型：身份证，手机号，固定电话，IP地址，邮箱
- enumType：枚举类型，可以设置对应的枚举，属性只有为String才识别
- range：范围类型，支持数值类型的范围：[a,b],[a,b),(a,b],(a,b),[a,null),(a,null],
- condition：java条件表达式，可以支持Java的所有运算符和所有返回值为`Boolean`的表达式，是一个小型表达式语言，两个占位符：`#root`（属性所在对象），`#current`（当前属性）。以及`java.lang.Math`中的所有函数，比如：`min(#root.num1, #root.num2) > #current`
- regex：正则表达式
- judge：属性外部配置，可设置自定义的核查逻辑
- disable：是否启动该核查
以上的属性都是用于匹配的规则，只要满足以上任何一项规则，则称之为匹配成功，即通过核查，否则，如果没有命中任何一个规则，且设定了规则，那么认为匹配失败，即该值就是会被拒绝的。

##### 注意：

一旦修饰属性，则该属性的值就不能为null，否则就会命中失败，如果需要允许null，则需要在白名单中添加上允许为null的规则即可。

<h3 id="@FieldBlackMatcher">@FieldBlackMatcher</h3>
该注解是黑名单注解，修饰属性，表示修饰的属性不接受匹配上该注解的值，用于对修饰的属性进行核查和筛选，该注解的属性跟`@FieldWhiteMatcher`是完全一样的，只是逻辑判断不一样：只要满足属性中的任何一项匹配，则称之为匹配成功，即没有通过核查，调用`Checks.getErrMsg()`即可看到错误调用链。

<h3 id="@FieldWhiteMatchers">@FieldWhiteMatchers</h3>
该注解是用于在修饰的bean在不同的场景可能核查规则不同的情况下使用。

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldWhiteMatchers {

    FieldWhiteMatcher[] value();
}
```
其中对应的黑名单匹配器为对应的黑名单数组，使用如下，比如

```java
@Data
@Accessors(chain = true)
public class GroupEntity {

    @FieldBlackMatchers({
        @FieldBlackMatcher(range = "[50, 100]"),
        @FieldBlackMatcher(group = "test1", range = "[12, 23]"),
        @FieldBlackMatcher(group = "test2", range = "[1, 10]")
    })
    private Integer age;

    @FieldWhiteMatchers({
        @FieldWhiteMatcher(value = {"beijing", "shanghai", "guangzhou"}),
        @FieldWhiteMatcher(group = "test1", value = {"beijing", "shanghai"}),
        @FieldWhiteMatcher(group = "test2", value = {"shanghai", "hangzhou"})
    })
    private String name;
}
```
其中分组时候核查可以通过包含分组的函数`check`来进行核查，比如
```java
Checks.check("test1", entity);
```

<h1 id="匹配器">一、匹配器：</h1>

这里是将注解的属性作为匹配器，只要配置了这个属性，那么就相当于启用了这个匹配器，匹配器可以有多个，只要匹配上任何一个，那么就算匹配上。注解 `@FieldWhiteMatcher` 或者`@FieldBlackMatcher`中的属性进行用法介绍。

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldWhiteMatcher {

    /**
     * 针对不同场景下所需的匹配模式的不同，默认"_default_"，详见{@link MikiConstant#DEFAULT_GROUP}
     * @return 分组
     */
    String group() default MikiConstant.DEFAULT_GROUP;
    
    /**
     * 可用的值， 如果允许值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null
     * @return 只允许的值的列表
     */
    String[] value() default {};

    /**
     * 可用的值对应的类型
     * @return 对应的枚举类型
     */
    FieldType type() default FieldType.DEFAULT;

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
     *
     * @return 如果是数值类型，且位于范围之内，则核查成功，当前支持的核查功能：[a,b]，[a,b)，(a,b]，(a,b)，(null,b]，(null,b)，[a, null), (a, null)
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
 
<h2 id="values">values</h2>
用于表示只要的或者不要的值列表，一般用于`String`，`Integer`（会自动转成`Integer`），该属性用于表示修饰的属性对应的值，比如

```java
@FieldWhiteMatcher({"a", "b", "c", "null"})
private String name;

@FieldWhiteMatcher({"12", "32", "29"})
private Integer age;
```

表示一个类的属性名 name 允许的值（或禁止的值）只能为："a", "b", "c" 和null，属性名 age 允许的值（或禁止的值）只能为：12， 32， 29

<h2 id="type">type</h2>
该属性一般用于修饰String类型的数据，表示命中系统内置的几种类型（目前系统内置了简单的几种）：

```text
手机号，身份证号，固定电话，邮箱，IP地址
```

使用方式比如：

```java
@FieldWhiteMatcher(type = FieldType.FIXED_PHONE)
private String fixedPhone;

@FieldBlackMatcher(type = FieldType.FIXED_PHONE)
private String fixedPhoneInValid;
```
<h2 id="enumType">enumType</h2>
表示枚举类型，修饰String类型的属性，用于表示该String类型的属性是多个枚举的名字

```java
@FieldWhiteMatcher(enumType = AEnum.class)
private String name;

@FieldWhiteMatcher(enumType = {AEnum.class, BEnum.class})
private String tag;
```
比如某个枚举类

```java
/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:37
 */
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

```java
/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:42
 */
@Getter
public enum  BEnum {

    B1("b1"),
    B2("b2"),
    B3("b3");

    private String name;

    BEnum(String name) {
        this.name = name;
    }
}
```
<h2 id="range">range</h2>
表示修饰数字类型数据的范围

| 范围 | 详情 |
| ------ | ------ |
| [a, b] | 表示数字>=a且<=b |
| [a, b) | 表示数字>=a且<b |
| (a, b] | 表示数字>a且<=b |
| (a, b) | 表示数字>a且<b |
| (null, b] | 表示数字<=b |
| (null, b) | 表示数字<b |
| [a, null) | 表示数字>=a |
| (a, null) | 表示数字>a |

#### 例子：

```java
/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:02
 */
@Data
@Accessors(chain = true)
public class RangeEntity1 {

    @FieldWhiteMatcher(range = "[0,100]")
    private Integer age1;

    @FieldWhiteMatcher(range = "[0, 100]")
    private Integer age2;
}
```
<h2 id="condition">condition</h2>
是条件表达式语句，该条件表达式中支持Java的所有运算符和java.lang.math的所有函数，且也支持类自定义的两个占位符。现在列举如下：

<h3 id="运算符">运算符</h3>

| 类型 | 运算符 |
| ------ | ------ |
| 算术运算符 | +、-、*、/、%、++、-- |
| 关系运算符 | ==、!=、>、<、>=、<= |
| 位运算符 | &、&#124;、^、~、<<、>>、>>> |
| 逻辑运算符 | &&、&#124;&#124;、! |
| 赋值运算符 | =、+=、-=、*=、/=、(%)=、<<=、>>=、&=、^=、&#124;= |
| 其他运算符 | 条件运算符（?:）、instanceof运算符 |

<h3 id="math的函数">math的函数</h3>
除了支持运算符构成的条件表达式之外，这里也支持`java.lang.math`中的所有函数，比如：`min`、`max`和`abs`等等函数

<h3 id="自定义占位符">自定义占位符</h3>
自定义占位符有两个：

| 占位符 | 详解 |
| ------ | ------ |
| #root | 表示属性所在类的对象值 |
| #current | 表示当前属性的值 |

添加自定义占位符主要是用于在对象的属性间有相互约束的时候用，比如，属性a和属性b之和大于属性c，这种就可以使用自定义占位符使用。

<h3 id="用例">用例</h3>

```java
/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午12:09
 */
@Data
@Accessors(chain = true)
public class ConditionEntity1 {

    @FieldWhiteMatcher(condition = "#current + #root.num2 > 100")
    private Integer num1;

    @FieldWhiteMatcher(condition = "#current < 20")
    private Integer num2;

    @FieldWhiteMatcher(condition = "(++#current) >31")
    private Integer num3;
}
```

```java
@Data
@Accessors(chain = true)
public class ConditionEntity2 {

    @FieldWhiteMatcher(condition = "#root.judge")
    private Integer age;

    private Boolean judge;
}
```

```java
@Data
@Accessors(chain = true)
public class ConditionEntity3 {

    @FieldWhiteMatcher(condition = "min(#current, #root.num2) > #root.num3")
    private Integer num1;
    private Integer num2;
    private Integer num3;
}
```

<h2 id="regex">regex</h2>
自定义的正则表达式

#### 注意：
这里的正则表达式是Java的正则表达式

```java
/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:13
 */
@Data
@Accessors(chain = true)
public class RegexEntity {

    @FieldWhiteMatcher(regex = "^\\d+$")
    private String regexValid;

    @FieldBlackMatcher(regex = "^\\d+$")
    private String regexInValid;
}
```

<h2 id="judge">judge</h2>
除了上面的一些用法之外，这里还支持系统内部自己进行判断，其中表达式的格式为

```text
class全路径#函数名，比如：com.xxx.AEntity#isValid，其中isValid的入参是当前属性的类型
```

#### 用例：

```java
@Data
@Accessors(chain = true)
public class JudgeEntity {

    @FieldWhiteMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#ageValid")
    private Integer age;

    @FieldWhiteMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#nameValid")
    private String name;

    @FieldBlackMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#addressInvalid")
    private String address;

    @FieldWhiteMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;
}
```
其中系统的匹配判决函数

```java
public class JudgeCls {

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
     * 名称是否合法
     */
    private boolean nameValid(String name) {
        if(null == name){
            return false;
        }
        List<String> blackList = Arrays.asList("women", "haode");
        if (blackList.contains(name)) {
            return false;
        }
        return true;
    }

    /**
     * 地址匹配
     */
    private boolean addressInvalid(String address){
        if(null == address){
            return true;
        }
        List<String> blackList = Arrays.asList("beijing", "hangzhou");
        if (blackList.contains(address)) {
            return true;
        }
        return false;
    }
}
```
<h2 id="disable">disable</h2>
表示是否启用该注解，true启用，false不启用

<h1 id="使用">三、使用</h1>

<h2 id="基本黑白名单匹配">基本黑白名单匹配</h2>

```java
@Data
@Accessors(chain = true)
public class AEntity {
    @FieldCheck(includes = {"a","b","c","null"})
    private String name;
    @FieldCheck(excludes = {"null"})
    private Integer age;
    private String address;
}
```

```groovy
def "复杂类型白名单测试"() {
    given:
    WhiteAEntity entity = new WhiteAEntity()
    entity.setName(name as String)
    expect:
    Assert.assertEquals(result, Checks.check(entity))
    if (!Checks.check(entity)) {
        println Checks.getErrMsg()
    }
    where:
    name || result
    "a"  || true
    "b"  || true
    "c"  || true
    null || true
    "d"  || false
}
```
输出

```text
数据校验失败-->属性[name]的值[d]不在白名单[a, b, c, null]中-->自定义类型[WhiteAEntity]核查失败
```

<h2 id="复杂类型黑白名单匹配">复杂类型黑白名单匹配</h2>
更复杂结构

```java
@Data
@Accessors(chain = true)
public class WhiteCEntity {

    @Check
    private List<CEntity> cEntities;
    @Check
    private BEntity bEntity;
}
```

```java
@Data
@Accessors(chain = true)
public class
CEntity {

    @FieldWhiteMatcher({"a", "b"})
    private String name;
    @Check
    private List<BEntity> bEntities;
}
```

```java
@Data
@Accessors(chain = true)
public class BEntity {
    @FieldCheck(includes = {"a","b"})
    private String name;
    @FieldCheck
    private AEntity aEntity;
}
```

```java
@Data
@Accessors(chain = true)
public class AEntity {
    @FieldCheck(includes = {"a","b","c","null"})
    private String name;
    @FieldCheck(excludes = {"null"})
    private Integer age;
    private String address;
}
```

```groovy
def "复杂类型白名单集合复杂结构"() {
    given:
    WhiteCEntity entity = new WhiteCEntity();
    entity.setCEntities(Arrays.asList(new CEntity().setName(ccName)
            .setBEntities(Arrays.asList(new BEntity().setName(cb1Name), new BEntity().setName(cb2Name)))))
            .setBEntity(new BEntity().setName(cName).setAEntity(new AEntity().setName(cbaName).setAge(12)))
    Assert.assertEquals(result, Checks.check(entity))
    if (!Checks.check(entity)) {
        println Checks.getErrMsg()
    }
    expect:
    where:
    ccName | cb1Name | cb2Name | cName | cbaName || result
    "a"    | "a"     | "a"     | "a"   | "a"     || true
    "a"    | "a"     | "a"     | "a"   | "b"     || true
    "a"    | "a"     | "a"     | "a"   | "c"     || true
    "a"    | "a"     | "b"     | "a"   | "a"     || true
    "b"    | "a"     | "b"     | "a"   | "a"     || true
    "b"    | "c"     | "b"     | "a"   | "a"     || false
    "b"    | "a"     | "b"     | "a"   | null    || true
}
```
输出

```text
数据校验失败-->属性[name]的值[c]不在白名单[a, b]中-->类型[BEntity]核查失败-->类型[CEntity]的属性[bEntities]核查失败-->类型[CEntity]核查失败-->类型[WhiteCEntity]的属性[cEntities]核查失败-->类型[WhiteCEntity]核查失败
```

<h2 id="指定参数匹配">指定参数匹配</h2>

修饰实体

```java
@Data
@Accessors(chain = true)
public class TestEntity {

    @FieldBlackMatcher({"nihao", "ok"})
    private String name;
    @FieldWhiteMatcher(range = "[12, 32]")
    private Integer age;
    @FieldWhiteMatcher({"beijing", "shanghai"})
    private String address;
}
```

核查代码，只核查指定的参数，参数可指定多个

```groovy
def "测试指定的属性age"() {
    given:
    TestEntity entity = new TestEntity().setName(name).setAge(age)

    expect:
    def act = Checks.check(entity, "age");
    Assert.assertEquals(result, act)
    if (!act) {
        println Checks.errMsg
    }

    where:
    name     | age | result
    "nihao"  | 12  | true
    "ok"     | 32  | true
    "hehe"   | 20  | true
    "haohao" | 40  | false
}
```

<h2 id="分组匹配">分组匹配</h2>

修饰的实体代码

```java
@Data
@Accessors(chain = true)
public class GroupEntity {

    @FieldBlackMatchers({
        @FieldBlackMatcher(range = "[50, 100]"),
        @FieldBlackMatcher(group = "test1", range = "[12, 23]"),
        @FieldBlackMatcher(group = "test2", range = "[1, 10]")
    })
    private Integer age;

    @FieldWhiteMatchers({
        @FieldWhiteMatcher(value = {"beijing", "shanghai", "guangzhou"}),
        @FieldWhiteMatcher(group = "test1", value = {"beijing", "shanghai"}),
        @FieldWhiteMatcher(group = "test2", value = {"shanghai", "hangzhou"})
    })
    private String name;
}
```

核查代码
```groovy
def "测试默认分组"() {
    given:
    GroupEntity entity = new GroupEntity().setAge(age).setName(name)

    expect:
    def act = Checks.check(entity);
    Assert.assertEquals(result, act)
    if (!act) {
        println Checks.errMsg
    }

    where:
    age | name       | result
    12  | "shanghai" | true
    12  | "beijing"  | true
    49  | "beijing"  | true
    50  | "beijing"  | false
    100 | "beijing"  | false
    49  | "tianjin"  | false
}

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

更全面的测试详见代码中的测试类

<h1 id="注意点">四、注意点：</h1>
1.如果是集合类型，那么该工具只支持泛型中的直接指明的类型，比如

```java
@Check
List<AEntity> entityList;

@Check
List<List<AEntity>> entityList;
```
而下面的这些暂时是不支持的（后面可以考虑支持）

```java
@Check
List<?> dataList;

@Check
List dataList;

@Check
List<? extend AEntity> dataList;

@Check
List<T> dataList;
```

<h1 id="代码">五、代码：</h1>
https://github.com/SimonAlong/Mikilin

<h1 id="版本记录">六、版本记录：</h1>

[版本信息](https://github.com/SimonAlong/Mikilin/wiki/%E7%89%88%E6%9C%AC%E8%AE%B0%E5%BD%95)