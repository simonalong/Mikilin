package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMather;
import com.simon.mikilin.core.express.ExpressParser;
import com.simon.mikilin.core.util.Maps;
import java.lang.reflect.Field;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式判断，对应{@link FieldWhiteMather#condition()}或者{@link FieldBlackMatcher#condition()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
public class ConditionMatcher extends AbstractBlackWhiteMatcher implements Builder<ConditionMatcher, String> {

    private static final String ROOT = "#root";
    private static final String CURRENT = "#current";
    /**
     * 判决对象
     */
    private BiPredicate<Object, Number> predicate;
    /**
     * 表达式解析对象
     */
    private ExpressParser parser;
    /**
     * 表达式
     */
    private String express;

    @Override
    public boolean match(Object object, String name, Object value) {
        Boolean result = predicate.test(object, (Number) value);
        if(result){
            setBlackMsg("属性[{0}]的值[{1}]命中黑名单表达式[{2}]", name, value, express);
            return true;
        }else{
            setBlackMsg("属性[{0}]的值[{1}]没有命中白名单表达式[{2}]", name, value, express);
            return false;
        }
    }

    @Override
    public boolean isEmpty() {
        return null == predicate;
    }

    @Override
    public ConditionMatcher build(String obj) {
        if (null == obj || "".equals(obj)) {
            return null;
        }
        express = obj;
        parser = new ExpressParser();
        predicate = (root, current) -> {
            parser.addBinding(parseConditionExpress(express, root, current));
            return parser.parse("import static java.lang.Math.*\n", rmvfix(express));
        };
        return this;
    }

    /**
     * 将条件表达式中的占位符进行替换为具体的数据
     *
     * @param express 表达式
     * @param root 当前属性所在的对象
     * @param current 当前属性的值
     * @return 返回对应的替换的数据映射
     */
    @SuppressWarnings("unchecked")
    private Maps parseConditionExpress(String express, Object root, Number current) {
        Maps maps = Maps.of();
        String regex = "(#root)\\.(\\w+)";
        Matcher m = Pattern.compile(regex).matcher(express);
        while (m.find()) {
            String fieldFullName = m.group();
            Object fieldValue = getFieldValue(fieldFullName, root);
            if (null != fieldValue) {
                maps.put(rmvfix(fieldFullName), fieldValue);
            }
        }

        if (express.contains(CURRENT)) {
            maps.put(rmvfix(CURRENT), current);
        }

        return maps;
    }

    private Object getFieldValue(String fullFieldName, Object root) {
        if (!fullFieldName.contains(ROOT)) {
            return null;
        }
        String filedName = fullFieldName.substring(ROOT.length() + 1, fullFieldName.length());
        try {
            Field field = root.getClass().getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(root);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 移除字符串中的#号和root
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

}
