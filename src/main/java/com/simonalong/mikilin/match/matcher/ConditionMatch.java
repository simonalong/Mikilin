package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.express.ExpressParser;
import com.simonalong.mikilin.util.Maps;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * 正则表达式判断，对应{@link Matcher#condition()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
public class ConditionMatch extends AbstractBlackWhiteMatch {

    private static final String ROOT = "#root";
    private static final String CURRENT = "#current";
    /**
     * 表达式中的变量和实际的变量名字的对应
     */
    private final Map<String, String> fieldNameMap = new HashMap<>(6);
    /**
     * 判决对象
     */
    private BiPredicate<Object, Object> predicate;
    /**
     * 表达式解析对象
     */
    private ExpressParser parser;
    /**
     * 表达式
     */
    private String express;
    private String objectName;

    @Override
    public boolean match(Object object, String name, Object value) {
        if (predicate.test(object, value)) {
            setBlackMsg("属性 {0} 的值 {1} 命中禁用条件 {2} ", name, value, replaceSystem(express));
            return true;
        } else {
            setWhiteMsg("属性 {0} 的值 {1} 不符合条件 {2} ", name, value, replaceSystem(express));
            return false;
        }
    }

    @Override
    public boolean isEmpty() {
        return null == predicate;
    }

    public static ConditionMatch build(String objectName, String obj) {
        if (null == obj || "".equals(obj)) {
            return null;
        }

        ConditionMatch matcher = new ConditionMatch();
        matcher.express = obj;
        matcher.objectName = objectName;
        matcher.parser = new ExpressParser();
        matcher.predicate = (root, current) -> {
            matcher.parser.addBinding(matcher.parseConditionExpress(matcher.express, root, matcher.objectName, current));
            return matcher.parser.parse("import static java.lang.Math.*\n", matcher.rmvfix(matcher.express));
        };
        return matcher;
    }

    /**
     * 将条件表达式中的占位符进行替换为具体的数据
     *
     * @param express 表达式
     * @param root 当前属性所在的对象
     * @param objectName 当前对象名字
     * @param current 当前属性的值
     * @return 返回对应的替换的数据映射
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Maps parseConditionExpress(String express, Object root, String objectName, Object current) {
        Maps maps = Maps.of();
        if (null != root) {
            String regex = "(#root)\\.(\\w+)";
            java.util.regex.Matcher m = Pattern.compile(regex).matcher(express);
            while (m.find()) {
                String fieldFullName = m.group();
                Object fieldValue = getFieldValue(fieldFullName, root);
                String rmvFieldName = rmvfix(fieldFullName);
                maps.put(rmvFieldName, fieldValue);
                fieldNameMap.put(fieldFullName, rmvFieldName);
            }
        }

        if (express.contains(CURRENT)) {
            maps.put(rmvfix(CURRENT), current);
            fieldNameMap.put(CURRENT, objectName);
        }

        return maps;
    }

    private Object getFieldValue(String fullFieldName, Object root) {
        if (!fullFieldName.contains(ROOT)) {
            return null;
        }
        String filedName = fullFieldName.substring(ROOT.length() + 1);
        try {
            Field field = root.getClass().getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(root);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        }
        return null;
    }

    /**
     * 移除字符串中的#号和root
     * @param str 待转换的字符
     * @return #root 到 root
     */
    @SuppressWarnings("all")
    private String rmvfix(String str){
        if(str.contains("#root.")){
            str = str.replace("#root.", "");
        }

        if(str.contains("#")){
            return str.replace("#", "");
        }
        return str;
    }

    /**
     * 替换系统内置的变量，比如："{@code #root.age + #current < 100}  返回 {@code age + testInteger < 100}
     * @param str 输入的字符
     * @return 替换后的字符串表达式
     */
    private String replaceSystem(String str) {
        AtomicReference<String> temStr = new AtomicReference<>(str);
        fieldNameMap.forEach((key, value) -> temStr.set(temStr.get().replace(key, value)));
        return temStr.get();
    }
}
