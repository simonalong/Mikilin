# mikilin
在业务开发中，类的属性有些时候我们需要这个属性不允许有某些值，有些时候只要某些值，通常情况下在使用的时候，会进行单独核查。这里开发一个工具用于核查这样类的属性的可用和不可用值，同时在出现不符合要求的时候，也能够定位到类的某个属性。
## 实例
#### 用法：
我们这里有两个注解：TypeCheck和FieldCheck
TypeCheck用于修饰要核查的类
FieldCheck用于核查要核查的类的属性，需要属性所在的类有TypeCheck修饰
##### TypeCheck
该注解只有一个属性disable 用于是否启动核查
##### FieldCheck 
该注解可修饰所有属性，但是修饰的属性类型不同处理也不一样，只有修饰基本类型（Boolean Byte Character Short Integer Long Double Float）和 String类型，
下面的includes和excludes才会起作用。如果修饰的是复杂类型（自定义类型、集合和Map类型）则该属性仅仅是起到一个排查复杂类型中包含的基本类型的作用，
因此，如果要核查的基本类型位于比较深层嵌套，那么需要将嵌套的路径上的所有复杂类型属性都加上FieldCheck
该注解有三个属性：
- includes：白名单，用于表示当前属性只允许的值
- excludes：黑名单，用于表示当前属性不允许的值
- disable：是否启动该参数核查

### 基本类型核查
有两个函数供基本类型使用
Checks.checkWhite 和 Checks.checkBlack

### 复杂类型核查
复杂类型主要是自定义的复杂类型，也包括集合和Map结构（Map结构只核查value为复杂类型）。
该核查只有一个函数Checks.check，如果核查失败，则返回false，同时也可以返回核查失败的调用路径，比如：
Checks.getErrMsg
```
数据校验失败-->属性[name]的值[c]不在白名单[a, b]中-->自定义类型[BEntity]核查失败-->自定义类型[WhiteBEntity]的属性[bEntity]核查失败-->自定义类型[WhiteBEntity]核查失败
```
```angular2html
@Data
@TypeCheck
@Accessors(chain = true)
public class WhiteBEntity {

    private BEntity bEntity;
    @FieldCheck(includes = {"a", "b"})
    private String name;
}
```
```angular2html
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
```angular2html
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
具体使用和测试可查看测试类MikilinTest
