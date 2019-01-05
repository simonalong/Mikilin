# Mikilin
在业务开发中，类的属性有些时候我们需要这个属性不允许有某些值，有些时候只要某些值，通常情况下在使用的时候，会进行单独核查。这里开发一个工具用于核查这样类的属性的可用和不可用值，同时在出现不符合要求的时候，也能够定位到类的某个属性。
## 实例
### 用法：
该工具可以核查基本类型（Boolean Byte Character Short Integer Long Double Float）和类型String和复杂类型。
### 基本类型核查
有两个函数供基本类型使用，（下面两个函数也可以测试复杂类型，但是不建议，复杂类型可用下面方式）
- Checks.checkWhite
- Checks.checkBlack

### 复杂类型核查
针对自定义类型我们这里引入两个注解（主要用于核查复杂结构（自定义类型、集合类型和Map类型（Map结构只解析value，key不考虑）））：
```
- @TypeCheck：用于修饰要核查的类
- @FieldCheck：用于核查要核查的类的属性，需要属性所在的类有TypeCheck修饰
```
#### TypeCheck
该注解只有一个属性disable 用于是否启动核查
#### FieldCheck 
该注解可修饰所有属性，但是修饰的属性类型不同处理也不一样，只有修饰基本类型（Boolean Byte Character Short Integer Long Double Float）和 String类型，
下面的includes和excludes才会起作用。如果修饰的是复杂类型（自定义类型、集合和Map类型）则该属性仅仅是起到一个排查复杂类型中包含的基本类型的作用，
因此，如果要核查的基本类型位于比较深层嵌套，那么需要将嵌套的路径上的所有复杂类型属性都加上FieldCheck
该注解有三个属性：
- includes：白名单，用于表示当前属性只允许的值
- excludes：黑名单，用于表示当前属性不允许的值
- disable：是否启动该参数核查


复杂类型主要是自定义的复杂类型，也包括集合和Map结构（Map结构只核查value为复杂类型）。
该核查只有一个函数
``` java
boolean check(Object object)
```
如果核查失败，则返回false，同时也可以返回核查失败的调用路径
```
String getErrMsg()
```
输出如下，比如：
##### 核查失败的信息定位
```
数据校验失败-->属性[name]的值[c]不在白名单[a, b]中-->自定义类型[BEntity]核查失败-->自定义类型[WhiteBEntity]的属性[bEntity]核查失败-->自定义类型[WhiteBEntity]核查失败
```
##### 用法
```java
@Data
@TypeCheck
@Accessors(chain = true)
public class AEntity {
    @FieldCheck(includes = {"a","b","c","null"})
    private String name;
    @FieldCheck(excludes = {"null"})
    private Integer age;
    private String address;
}
```
可以根据异常信息定位到哪个类的哪个属性的哪个值是不合法的
## 测试
这里我们放一个复杂类型的黑名单测试
```java
@Data
@TypeCheck
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
###### 输出
```
数据校验失败-->属性[name]的值[d]不在白名单[a, b, c, null]中-->自定义类型[WhiteAEntity]核查失败
```
#### 更复杂的结构
测试结构 WhiteCEntity 对应的对象
```java
@Data
@TypeCheck
@Accessors(chain = true)
public class WhiteCEntity {

    // 下面复杂结构添加 @FieldCheck 注解用于建立下一级CEntity中的数据，如果不添加，则下一级无法核查
    @FieldCheck
    private List<CEntity> cEntities;
    @FieldCheck
    private BEntity bEntity;
}
```
```java
@Data
@TypeCheck
@Accessors(chain = true)
public class
CEntity {

    @FieldCheck(includes = {"a", "b"})
    private String name;
    @FieldCheck
    private List<BEntity> bEntities;
}
```
```java
@Data
@TypeCheck
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
@TypeCheck
@Accessors(chain = true)
public class AEntity {
    @FieldCheck(includes = {"a","b","c","null"})
    private String name;
    @FieldCheck(excludes = {"null"})
    private Integer age;
    private String address;
}
```
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
###### 输出
```text
数据校验失败-->属性[name]的值[c]不在白名单[a, b]中-->自定义类型[BEntity]核查失败-->自定义类型[CEntity]的属性[bEntities]核查失败-->自定义类型[CEntity]核查失败-->自定义类型[WhiteCEntity]的属性[cEntities]核查失败-->自定义类型[WhiteCEntity]核查失败
```
更全面的测试详见类MikilinTest
