# Mikilin 介绍

该框架是我在业务开发中，经常需要校验核查对象中的属性的可用值和不可用值，进而开发的一个核查框架。该框架通过引入黑白名单机制，能够核查所有的基本属性，针对复杂类型，通过拆分后获取基本类型进行核查。该框架具有以下特性：

#### 功能性：
* 全类型：可以核查所有类型，基本类型，复杂类型，集合和Map等各种对象化类型
* 多方式：对类型的核查支持多种方式：值列表、属性类型、正则表达式、外部逻辑回调、范围和小型表达式语言核查
* 黑白机制：核查机制引入正反的黑白名单机制，白名单表示只要的值，黑名单表示禁用的值

#### 非功能性：
* 零侵入：对代码无侵入，仅作为一个工具类存在
* 易使用：使用超级简单，三个注解，一个核查函数，一个核查失败函数，多种核查通道
* 高性能：所有的核查均是内存直接调用
* 可扩展：针对一些不好核查的属性，可以设置外部回调，可设置自己定义的核查逻辑

## 目录：

* [一、介绍](#介绍)
    * [@FieldValidCheck 和 @FieldInvalidCheck](#check)
    * [基本类型核查](#基本类型核查)
    * [复杂类型核查](#复杂类型核查)
* [二、用法](#用法)
    * [核查函数](#核查函数)
    * [错误信息](#错误信息)
* [三、用例](#用例)
* [四、测试](#测试)
    * [输出](#输出)
    * [更复杂的结构](#更复杂的结构)
        * [测试](#测试)
        * [输出](#输出)
* [五、注意点](#注意点)

<h2 id="介绍">一、介绍：</h2>

该工具可以核查基本类型、集合类型和各种复杂等的类型。
<h4 id="check">@FieldValidCheck 和 @FieldInvalidCheck</h4>
                    
这两个注解值修饰基本类型`（Boolean Byte Character Short Integer Long Double Float）`和 `String`类型，而复杂类型是由基本类型组成的，复杂类型的修饰通过注解`@Check`进行修饰，解析时候才会解析内部的基本类型注解。<br />上面两种注解都有下面的属性：
* value：（禁用或者可用的）值列表
* type：（禁用或者可用的）既定的类型：身份证，手机号，固定电话，IP地址，邮箱
* enumType：（禁用或者可用的）枚举类型，可以设置对应的枚举，属性只有为String才识别
* range：（禁用或者可用的）范围类型，支持数值类型的范围：[a,b],[a,b),(a,b],(a,b),[a,null),(a,null],(null,b],(null,b)
* condition：（禁用或者可用的）java条件表达式，可以支持Java的所有运算符和所有返回值为Boolean的表达式，是一个小型表达式语言，两个占位符：#root（属性所在对象），#current（当前属性）。以及java.lang.Math中的所有函数，比如：min(#root.num1, #root.num2) > #current
* regex：（禁用或者可用的）正则表达式
* judge：（禁用或者可用的）属性外部配置，可设置自定义的核查逻辑，用于要核查的数据逻辑或者数据量特别大
* disable：（禁用或者可用的）是否启动该核查

上面所有的核查都有专门的例子进行解释，详情请见对应的测试

<h2 id="用法">二、用法：</h2>

<h3 id="基本类型核查">基本类型核查</h3>

有两个函数供基本类型使用，（下面两个函数也可以测试复杂类型，但是不建议，复杂类型可用后面讲述方式）
```text
Checks.checkWhite
Checks.checkBlack
```

<h3 id="复杂类型核查">复杂类型核查</h3>

针对自定义类型我们这里引入两个注解（主要可以核查各种结构（基本类型、自定义类型、集合类型和Map类型（Map结构只解析value，key不考虑）））：
```text
@FieldValidCheck：用于设置属性可用的值
@FieldInvalidCheck：用于设置属性禁用的值
@Check：修饰复杂结构体，用于向内部解析
```
核查函数为：
```text
Checks.check(Object)
```

<h4 id="核查函数">核查函数</h4>

该核查对基本类型有两种函数，多个重载函数
```text
// 核查可用的值
public <T> boolean checkWhite(T object, Set<T> whiteSet)
public <T> boolean checkWhite(T object, List<T> whiteSet)
public <T> boolean checkWhite(T object, T... whiteSet)

// 核查禁用的值
public <T> boolean checkBlack(T object, Set<T> blackSet)
public <T> boolean checkBlack(T object, List<T> blackSet)
public <T> boolean checkBlack(T object, T... blackSet)
```
核查复杂类型就一个函数
```text
public boolean check(Object object)
```
<h4 id="错误信息">错误信息</h4>

如果核查失败，则返回false，同时也可以返回核查失败的调用路径
```text
public String getErrMsg()
```

比如：核查失败的信息定位
```
数据校验失败-->对象值["c"]不在白名单[null, a, b]中
```
上面是一层结构，对于更复杂的结构输出可继续看后面复杂结构

<h2 id="用例">三、用例：</h2>

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
```text
// 调用测试
if (!Checks.check(aEntity)) {
  println Checks.getErrMsg()
}
```
可以根据打印的信息定位到哪个类的哪个属性的哪个值是不合法的，对于更多的复杂结构测试，可见下面的测试方式
<h2 id="测试">四、测试：</h2>

这里我们放一个复杂类型的黑名单测试
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
<h5 id="输出">输出</h5>

```
数据校验失败-->属性[name]的值[d]不在白名单[a, b, c, null]中-->自定义类型[WhiteAEntity]核查失败
```
<h3 id="更复杂的结构">更复杂的结构</h3>

测试结构 WhiteCEntity 对应的对象
```java
@Data
@Accessors(chain = true)
public class WhiteCEntity {
    // 下面复杂结构添加 @FieldCheck 注解用于建立下一级CEntity中的数据，如果不添加，则下一级无法核查
    @FieldCheck
    private List cEntities;
    @FieldCheck
    private BEntity bEntity;
}
```
```java
@Data
@Accessors(chain = true)
public class
CEntity {
    @FieldCheck(includes = {"a", "b"})
    private String name;
    @FieldCheck
    private List bEntities;
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
<h4 id="测试">测试</h4>

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
<h4 id="输出">输出</h4>
异常信息

```
数据校验失败-->属性[name]的值[c]不在白名单[a, b]中-->类型[BEntity]核查失败-->类型[CEntity]的属性[bEntities]核查失败-->类型[CEntity]核查失败-->类型[WhiteCEntity]的属性[cEntities]核查失败-->类型[WhiteCEntity]核查失败
```
更全面的测试详见测试类：FieldValueTest、FieldTypeTest、FieldRegexTest和FieldJudgeTest

<h2 id="注意点">注意点</h2>
1.如果是集合类型，那么该工具只支持泛型中的直接指明的类型，比如

```text
@Check
List<AEntity> entityList;

@Check
List<List<AEntity>> entityList;
```
而下面的这些暂时是不支持的（后面可以考虑支持）
```text
@Check
List<?> dataList;

@Check
List dataList;

@Check
List<? extend AEntity> dataList;

@Check
List<T> dataList;
```


