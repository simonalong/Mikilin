package com.simonalong.mikilin.annotation;

import com.simonalong.mikilin.MkConstant;
import com.simonalong.mikilin.match.FieldModel;

import java.lang.annotation.*;

/**
 * 属性核查器
 * 修饰基本的属性（Boolean Byte Character Short Integer Long Double Float）、String和java.util.Date类型，属性的所有可用的值
 *
 * @author zhouzhenyong
 * @since 2019/3/7 下午9:47
 */
@Repeatable(Matchers.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Matcher {

    /**
     * 针对不同场景下所需的匹配模式的不同，默认"_default_"，详见{@link com.simonalong.mikilin.MkConstant#DEFAULT_GROUP}
     * <p>
     * 该参数使用一般结合{@link Matchers}这个注解使用
     *
     * @return 分组
     */
    String[] group() default {MkConstant.DEFAULT_GROUP};

    /**
     * 匹配属性为对应的类型，比如Integer.class，Long.class等等
     * <p>
     * 注意：type对应的类型为修饰属性的类型或者类型的子类型才行，否则转换失败
     *
     * @return 指定的类型
     */
    Class<?>[] type() default {};

    /**
     * 如果要匹配值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null，也可以使用属性{@link Matcher#notNull()}
     *
     * @return 只允许的值的列表
     */
    String[] value() default {};

    /**
     * 为'true'：则修饰的字符串不可为null
     * 为'false'（或者其他）：则修饰的字符串只可为null
     * <p>废弃，替换为isNull
     *
     * @return 匹配的值是否为空
     */
    @Deprecated
    String notNull() default "";

    /**
     * 为'true'：则修饰的字符串不可为null，也不可为空字符
     * 为'false'（或者其他）：则修饰的字符串只可为null或者空字符
     *
     * <p>只有修饰类型为{@link CharSequence}的类及子类才会生效，否则抛出异常{@link com.simonalong.mikilin.exception.MkException}
     * <p>废弃，替换为isNull
     * @return 匹配的值是否为空字符
     */
    @Deprecated
    String notBlank() default "";

    /**
     * 为'true'：数据为null则匹配上
     * 为'false'（或者其他）：数据不为null则匹配上
     *
     * @return 匹配的值是否为空
     */
    String isNull() default "";

    /**
     * 为'true'：数据为null或者空字符，则匹配上
     * 为'false'（或者其他）：数据不为null，而且也不是空字符，则匹配上
     *
     * <p>只有修饰类型为{@link CharSequence}的类及子类才会生效，否则抛出异常{@link com.simonalong.mikilin.exception.MkException}
     * @return 匹配的值是否为空字符
     */
    String isBlank() default "";

    /**
     * 可用的值对应的类型
     *
     * @return 对应的枚举类型
     */
    FieldModel model() default FieldModel.DEFAULT;

    /**
     * 枚举类型的判断
     * <p>
     * 注意：该类型只用于修饰属性的值为String类型或者Integer类型的属性，String为枚举的 name() 方法，Integer是枚举的original() 方法
     *
     * @return 该属性为枚举对应的类，否则不生效
     */
    Class<? extends Enum>[] enumType() default {};

    /**
     * 数据范围的判断
     * <p> 该字段修饰的类型可以为数值类型（数值大小）、也可以为时间类型（long大小）、也可以为集合类型、（size个数的范围）和字符串类型（CharSequence类型，长度大小）
     *
     * <ul>
     * <li>
     * 数值类型，则比较的是数值的范围，使用比如：[a,b]，[a,b)，(a,b]，(a,b)，(null,b]或者(, b]，(null,b)或者(,b)，[a, null)或者[a,), (a, null)或者(a,)
     * </li>
     * <li>
     * 集合类型，则比较的是集合的size大小，使用和上面一样，比如：[a,b]等等
     * </li>
     * <li>
     * 字符串类型，则比较的是字符串的长度，用法同上
     * </li>
     * <li>
     * 时间类型，可以使用这种，比如["2019-08-03 12:00:32.222", "2019-08-03 15:00:32.222")，也可以用单独的一个函数关键字
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
     * </li>
     * <li>
     * 时间范围类型计算，比如(-4y2M,)：表示4年2个月前之后的所有时间，其中支持的字段为
     * </li>
     * <li>
     *     <ul>
     *         <li>-/+：表示往前推还是往后推</li>
     *         <li>y：年</li>
     *         <li>M：月</li>
     *         <li>d：天</li>
     *         <li>H（h）：小时</li>
     *         <li>m：分钟</li>
     *         <li>s：秒</li>
     *     </ul>
     * </li>
     *</ul>
     * @return 返回的是范围的字符串，比如："[0,1]"。具体的请看上面说明
     */
    String range() default "";

    /**
     * 数据条件的判断
     * <p>
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
     *
     * @return 对应的正则表达式
     */
    String regex() default "";

    /**
     * 自定义匹配模式
     *
     * @return 调用的核查的类和函数对应的表达式，比如："com.xxx.AEntity#isValid"，其中#后面是方法，方法返回boolean或者包装类，表示是否匹配上，其中参数根据个数支持的类型也是不同，参考对应测试类
     */
    String customize() default "";

    /**
     * 核查失败后的返回语句，其中提供 #current和#root.xxx（xxx为对应属性所在对象的其他属性名） 替换符，在打印的时候会替换当前修饰的属性的值
     *
     * @return 核查失败后返回的语句
     */
    String errMsg() default "";

    /**
     * 匹配后转换为某个值
     * <p>
     * 该值配置了，则不再进行匹配判断（即：accept() 和 errMsg() 不再使用）
     *
     * @return 待转换的值
     */
    String matchChangeTo() default "";

    /**
     * 过滤器模式
     * <p>
     * 其他的属性都是匹配，而该属性表示匹配之后对应的数据的处理，是接受放进来，还是只拒绝这样的数据
     *
     * @return true：accept（放进来），false：deny（拒绝）
     */
    boolean accept() default true;

    /**
     * 是否启用核查
     *
     * @return true：禁用核查，false：启用核查
     */
    boolean disable() default false;
}
