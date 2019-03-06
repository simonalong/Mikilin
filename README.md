# Mikilin 介绍

在业务开发中，类的属性有些时候我们需要这个属性不允许有某些值，有些时候只要某些值，通常情况下在使用的时候，会进行单独核查。这里开发一个工具用于核查这样类的属性的可用和不可用值，同时在出现不符合要求的时候，也能够定位到类的某个属性，不管结构有多复杂都可以定位出来。
<a name="cea7b715"></a>
## 介绍：
该工具可以核查基本类型（Boolean Byte Character Short Integer Long Double Float）和类型String和复杂类型。
<a name="303f7c60"></a>
### 基本类型核查
有两个函数供基本类型使用，（下面两个函数也可以测试复杂类型，但是不建议，复杂类型可用下面方式）
* Checks.checkWhite
* Checks.checkBlack
<a name="104274b6"></a>
### 复杂类型核查
针对自定义类型我们这里引入一个注解（主要可以核查各种结构（基本类型、自定义类型、集合类型和Map类型（Map结构只解析value，key不考虑）））：
```
@FieldCheck：用于核查要核查的类的属性
```
<a name="4fe3d024"></a>
#### @FieldCheck
该注解可修饰所有属性，但是修饰的属性类型不同处理也不一样，只有修饰基本类型（Boolean Byte Character Short Integer Long Double Float）和 String类型，<br />
下面的includes和excludes才会起作用。如果修饰的是复杂类型（自定义类型、集合和Map类型）则该属性仅仅是起到一个排查复杂类型中包含的基本类型的作用，<br />
因此，如果要核查的基本类型位于比较深层嵌套，那么需要将嵌套的路径上的所有复杂类型属性都加上FieldCheck。<br />
该注解有三个属性：
* includes：白名单，用于表示当前属性只允许的值
* excludes：黑名单，用于表示当前属性不允许的值
* disable：是否启动该参数核查

复杂类型主要是自定义的复杂类型，也包括集合和Map结构（Map结构只核查value为复杂类型）。<br />
该核查只有一个函数
```java
boolean check(Object object)
```
如果核查失败，则返回false，同时也可以返回核查失败的调用路径
```
String getErrMsg()
```
输出如下，比如：
<a name="a485bf5a"></a>
##### 核查失败的信息定位
```
数据校验失败-->对象值["c"]不在白名单[null, a, b]中
```
上面是一层结构，对于更复杂的结构输出可继续看后面复杂结构
<a name="6c9ae3db"></a>
## 用法：
用法非常简单，只需一个函数即可
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

// 调用测试
if (!Checks.check(aEntity)) {
  println Checks.getErrMsg()
}
```
可以根据打印的信息定位到哪个类的哪个属性的哪个值是不合法的，对于更多的复杂结构测试，可见下面的测试方式
<a name="f7be1f51"></a>
## 测试：
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
<a name="8ba7c3a7"></a>
###### 输出
```
数据校验失败-->属性[name]的值[d]不在白名单[a, b, c, null]中-->自定义类型[WhiteAEntity]核查失败
```
<a name="396e6ab7"></a>
### 更复杂的结构
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
<a name="db06c78d"></a>
###### 测试
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
<a name="8ba7c3a7-1"></a>
###### 输出
```
数据校验失败-->属性[name]的值[c]不在白名单[a, b]中-->类型[BEntity]核查失败-->类型[CEntity]的属性[bEntities]核查失败-->类型[CEntity]核查失败-->类型[WhiteCEntity]的属性[cEntities]核查失败-->类型[WhiteCEntity]核查失败
```
更全面的测试详见类MikilinTest
