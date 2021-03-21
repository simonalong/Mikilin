# Mikilin 介绍
该框架是对象的属性核查框架。直接对标hibernate.validate，但是却比起功能更多，使用和扩展更简单。秉承大道至简的思想，引入核查器和匹配器机制，可以将各种复杂的匹配变得特别简单。核查器为内置的两种：黑名单和白名单。而匹配器针对各种类型的设定不同匹配策略。大部分的匹配都是基于基本的类型，而复杂类型（集合、map或者自定义类型）又都是由基本类型组成的。框架支持对复杂类型会进行拆解并核查内部的匹配类型进而对复杂类型进行拦截。该框架具有以下特性：

## 功能性：
- 全类型：可以核查所有类型，基本类型，复杂类型，集合和Map等各种有固定属性的类型
- 匹配器：对类型的匹配机制：分组、值列表、属性class、指定模型类型、正则表达式、系统回调（扩展）、枚举类型、范围判决（支持时间范围）和表达式语言判决
- 黑白机制：匹配完之后，数据是拒绝还是接收。接收表示只接收匹配的值，为白名单概念。拒绝表示只拒绝匹配的值，为黑名单概念

## 非功能性：
- 零侵入：对代码零侵入，仅作为一个工具类存在
- 易使用：使用超级简单，一个类，两类核查器，三个注解，多种匹配器
- 高性能：所有的核查均是内存直接调用，第一次构建匹配树后，后面就无须重建
- 可扩展：针对一些不好核查的属性，可以通过自定义匹配器属性，也可以使用spring的Bean作为系统匹配器类

## 使用文档
[Mikilin文档](https://persimon.gitbook.io/mikilin/)

# 一、快速入门 
本工具用法极其简单，可以说，只要会用一个注解`Matcher`和一个方法`MkValidators.check(Object obj)`即可。`Matcher`表示匹配器，内部根据accept区分白名单和黑名单，就是只要匹配到注解中的属性，则表示当前的值是可以通过的，否则函数`MkValidators.check(Object obj)`返回失败，并通过`MkValidators.getErrMsgChain`获取所有错误信息或者通过`MkValidators.getErrMsg`获取某一项错误信息。

## maven引入 
已发布到中央仓库，可自行获取
```xml
<dependency>
    <groupId>com.github.simonalong</groupId>
    <artifactId>mikilin</artifactId>
    <!--请替换为最新版本-->
    <version>${latest.release.version}</version>
</dependency>
```

## 使用 
该框架使用极其简单（直接参考spring-validate框架用法即可），如下：给需要拦截的属性添加注解即可
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

#### 硬编码
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
#### 自动核查
版本：>= 1.6.0
在1.6.0版本中添加`@AutoCheck`注解，修饰类和方法，会自动核查方法或者类的方法中的参数，不符合需求，则会上报异常`MkException`
```java
/**
 * 自动核查注解
 *
 * <p> 针对修饰的类和方法中的参数进行核查
 *     <ul>
 *         <li>1.修饰类：则会核查类下面所有函数的所有参数</li>
 *         <li>2.修饰函数：则会核查函数对应的所有参数</li>
 *     </ul>
 * @author shizi
 * @since 2020/6/25 11:20 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoCheck {

    /**
     * 同{@link AutoCheck#group()}
     * @return 分组
     */
    String value() default MkConstant.DEFAULT_GROUP;

    /**
     * 核查的分组
     * @return 分组
     */
    String group() default MkConstant.DEFAULT_GROUP;
}

```
用法
```java
@AutoCheck
@RequestMapping("${api-prefix}/deploy")
@RestController
public class DeployController {

    //...
        
    /**
     * 启动构建
     */
    @AutoCheck("startBuild")
    @PutMapping("startBuild")
    public Integer startBuild(@RequestBody AppIdReq appIdReq) {
        //...
    }
    //...
}
```
# 二、常见场景用法
以下场景均来自实际业务场景

#### 1.字段不可为空
用户id不可为空
```java
/**
 * 被邀请的用户id
 */
@Matcher(notNull = "true")
private Long userId;
```

#### 2.字符串属性不可为空
用户名不可为空
```java
/**
 * 用户名
 */
@Matcher(notBlank = "true")
private String name;
```

#### 3.类型只可为0和1
对应属性的类型，只可为两个值，或者多个值
```java
/**
 * 是否必需，0：不必填，1：必填
 */
@Matcher(value = {"0", "1"})
private Integer needKey;
```

#### 4.类型为多个固定的值
对应属性的状态，只可为0、1、2、3、4、5、6、7中的一个
```java
/**
 * 状态：0未构建，1编译中，2打包中，3部署中，4测试中，5测试完成，6发布中，7发布完成
 */
//@Matcher(value = {"0", "1", "2", "3", "4", "5", "6", "7"}) 也可以使用下面
@Matcher(range = "[0, 8]") 
private Integer deployStatus = 0;
```

#### 5.对应的值为邮箱
字段为邮箱判断。除了邮箱之外，还有手机号、固定电话、身份证号和IP地址这么四个固定的类型判断。
```java
/**
 * 邮箱
 */
@Matcher(notNull = "false", model = FieldModel.MAIL, errMsg = "邮箱：#current 不符合邮件要求")
private String email;
```

#### 6.对应集合的长度
前端上传的图片最多为三个
```java
/**
 * 预览图最多为三个
 */
@Matcher(range = "(, 3]")
private List<String> prePicUrlList;
```

#### 7.字符长度最长为128
对前端传递过来的字符长度限制为128，因为数据库字段存储为最长128
```java
/**
 * 地址长度
 */
@Matcher(range = "[0, 128]")
private String nameStr;
```

#### 8.数据为空或者不空的话，长度不能超过200
其中@Matcher内部多个属性之间的匹配是或的关系
```java
/**
 * 项目描述，可以为空，但是最长不可超过200
 */
@Matcher(notBlank = "false", range = "[0, 200]", errMsg = "描述的值不可过长，最长为200")
private String proDesc;
```

#### 9.前端传递过来的id必须在db中存在，而且数据不可为空
@Matcher支持多个叠加形式，表示多个条件的与操作
```java
@Matcher(notNull = "true")
@Matcher(customize = "com.xxx.yyy.ExistMatch#proIdExist", errMsg = "proId：#current在db中不存在")
private Long projectId;
```
其中匹配的写法
```java
@Service
public class ExistMatch {

    @Autowired
    private ProjectService projectService;

    /**
     * appId存在
     */
    public boolean proIdExist(Long proId) {
        return projectService.exist(proId);
    }
}
```

#### 10.项目名不可为空，而且数据库中不能存在，如果存在则拒绝
其中属性accept表示如果前面的条件匹配则拒绝，默认为true
```java
/**
 * 项目名称
 */
@Matcher(notBlank = "true")
@Matcher(customize = "com.isyscore.iop.panda.service.ProjectService#projectNameExist", accept = false, errMsg = "已经存在名字为 #current 的项目")
private String proName;
```

#### 11.在某个配置项下对应的字段值
业务场景：在字段为1的时候，另外一个对应的字段不可为空
```java
/**
 * 处理类型：0，新增；1，编辑；2，搜索；3，表展示；4，表扩展
 */
@Matcher(range = "[0, 4]", errMsg = "不识别类型 #current")
private Integer handleType;

/**
 * 在编辑模式下，禁用的表字段
 */
@Matcher(condition = "(#current == null && #root.handleType != 1) || (#current != null && !#current.isEmpty() && #root.handleType == 1)", errMsg = "cantEditColumnList 需要在handleType为1的时候才有值")
private List<String> cantEditColumnList;
```

#### 12.时间必须是过去的时间
```java
/**
 * 应用发布时间
 */
@Matcher(range = "past")
@ApiModelProperty(value = "应用发布时间")
private Date createTime;
```

#### 13.分页数据必须满足>0
```java
@Matcher(range = "[0, )", errMsg = "分页数据不满足")
private Integer pageNo;
@Matcher(range = "[0, )", errMsg = "pageSize数据不满足")
private Integer pageSize;
```

#### 14.【复杂场景】应用id在不同的场景下处理方式不同

1. 在启动构建时候，状态必须在“开始”阶段
1. 在测试完成动作，状态必须在‘测试中’阶段
1. 在启动发布动作，状态必须在“测试完成”阶段
1. 在停止动作，状态必须在“部署”状态之前
1. 退出动作，要保证应用在“部署”状态之前

此外最基本的就是应用id不可为空，而且在db中必须存在。上面的几个动作都是不同的接口，但是所有的参数都相同，那么用group是最好的方式。其中group里面可以添加多个分组，其中group相同的，则表示两个@Mather之间是与的关系
```java
@Data
public class AppIdReq {

    @Matchers({
        @Matcher(notNull = "true"),
        @Matcher(group = {MkConstant.DEFAULT_GROUP, "startBuild", "finishTest", "startDeploy", "stop", "quite"}, customize = "com.xxx.yyy.ExistMatch#appIdExist", errMsg = "应用id: #current 不存在"),
        // 启动构建 动作的状态核查
        @Matcher(group = "startBuild", customize = "com.xxx.yyy.DeployStatusMatch#startBuild", errMsg = "应用id: #current 不在阶段'未编译'，请先退出"),
        // 测试完成 动作的状态核查
        @Matcher(group = "finishTest", customize = "com.xxx.yyy.DeployStatusMatch#finishTest", errMsg = "应用id: #current 不在阶段'测试中'"),
        // 启动发布 动作的状态核查
        @Matcher(group = "startDeploy", customize = "com.xxx.yyy.DeployStatusMatch#startDeploy", errMsg = "应用id: #current 不在阶段'测试完成'"),
        // 停止 动作的状态核查
        @Matcher(group = "stop", customize = "com.xxx.yyy.DeployStatusMatch#stopDeploy", errMsg = "停止的动作需要保证应用 #current 在部署状态之前"),
        // 退出 动作的状态核查
        @Matcher(group = "quite", customize = "com.xxx.yyy.DeployStatusMatch#stopDeploy", errMsg = "退出的动作需要保证应用 #current 在部署状态之前")
    })
    @ApiModelProperty(value = "应用id", example = "42342354")
    private Long appId;
}
```
对应的接口使用
```java
@AutoCheck
@RequestMapping("${api-prefix}/deploy")
@RestController
public class DeployController {

    ...
        
    /**
     * 启动构建
     */
    @AutoCheck("startBuild")
    @PutMapping("startBuild")
    public Integer startBuild(@RequestBody AppIdReq appIdReq) {
        Long appId = appIdReq.getAppId();
        devopsService.startBuild(appId);
        return 1;
    }

    /**
     * 完成测试
     */
    @AutoCheck("finishTest")
    @PutMapping("finishTest")
    public Integer finishTest(@RequestBody AppIdReq appIdReq) {
        Long appId = appIdReq.getAppId();
        appService.chgDeployStatus(appId, DeployStatusEnum.TEST_FINISH);
        return 1;
    }

    /**
     * 启动发布
     */
    @AutoCheck("startDeploy")
    @PutMapping("startDeploy")
    public Integer startDeploy(@RequestBody AppIdReq appIdReq) {
        Long appId = appIdReq.getAppId();
        appService.startDeploy(appId, UserInfoContext.getUserContext().getUserId());
        return 1;
    }

    /**
     * 停止
     * <p>只有在发布之前，停止按钮可见
     */
    @AutoCheck("stop")
    @PutMapping("stop")
    public Integer stop(@RequestBody AppIdReq appIdReq) {
        appService.stopApp(appIdReq.getAppId());
        return 1;
    }

    /**
     * 退出
     * <p>如果已经进行过“发布”，则不可退出，而且该退出按钮只有在停止后才能退出
     */
    @AutoCheck("quite")
    @PutMapping("quite")
    public Integer quite(@RequestBody AppIdReq appIdReq) {
        appService.quite(appIdReq.getAppId());
        return 1;
    }
}
```

#### 15.核查参数
version >= v1.6.1

注解`@Matcher`和`@Matchers`除了可以修饰Field类型外，也可以修饰函数的入参
如下示例
```java
@Slf4j
@AutoCheck
@RequestMapping("/api/test/mikilin")
@RestController
public class TestController {

    @PostMapping("fun1")
    public String fun1(
        @Matcher(value = {"song", "zhou"}) String name,
        @Matcher(range = "[0, 3]") Integer age
    ) {
        return name + "-" + age;
    }
}
```

#### 16.时间的计算
version >= v1.6.1

就是对range属性修饰的时间的扩充，比如匹配前3天2小时这种，就可以使用range="(-3d2h,)"，表示匹配当前时间往前推3天两个小时后的所有时间

如下示例
```java
@Data
@Accessors(chain = true)
public class RangeTimeEntity1 {

    // 过去四年2月5天3小时2分钟3秒
    @Matcher(range = "(-4y2M5d3h2m3s,)")
    private Date date;
}
```

## 详细介绍 
对于详细内容介绍，请见文档[Mikilin说明文档](https://www.yuque.com/simonalong/mikilin)

## 技术咨询
目前咨询人数不多，暂时没有建群，有需求请添加微信
zhoumo187108
