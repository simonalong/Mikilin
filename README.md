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

从上面用例可以看到该框架使用非常简单。不过框架的功能性却很强大，那么强大在哪，在于注解属性的多样性，后面一一介绍。对于属性这里的核查函数其实就是只有一个，不同的重载

## 详细介绍 <a name="详细介绍"></a>
对于详细内容介绍，请见文档[Mikilin说明文档](https://www.yuque.com/simonalong/mikilin)

技术讨论群：
请先加WX
![WechatIMG22056.jpeg](https://cdn.nlark.com/yuque/0/2020/jpeg/126182/1585717997902-ce20cd02-4033-488b-aca7-b2fd51823569.jpeg#align=left&display=inline&height=296&name=WechatIMG22056.jpeg&originHeight=786&originWidth=564&size=55477&status=done&style=none&width=213)
