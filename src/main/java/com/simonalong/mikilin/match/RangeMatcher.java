package com.simonalong.mikilin.match;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.express.ExpressParser;
import com.simonalong.mikilin.util.Maps;
import java.util.function.Predicate;
import org.codehaus.groovy.syntax.Numbers;

/**
 * 正则表达式判断，对应{@link FieldWhiteMatcher#range()}或者{@link FieldBlackMatcher#range()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
public class RangeMatcher extends AbstractBlackWhiteMatcher implements Builder<RangeMatcher, String> {

    private static final String LEFT_BRACKET_EQUAL = "[";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET_EQUAL = "]";
    private static final String RIGHT_BRACKET = ")";
    /**
     * 判决对象
     */
    private Predicate<Number> predicate;
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
        if (value instanceof Number){
            Number number = Number.class.cast(value);
            boolean result = predicate.test(number);
            if (result){
                setBlackMsg("属性[{0}]的值[{1}]位于黑名单对应的范围[{2}]中", name, number, express);
                return true;
            }else{
                setWhiteMsg("属性[{0}]的值[{1}]没有在白名单对应的范围[{2}]中", name, number, express);
            }
        } else{
            setWhiteMsg("属性[{0}]的值[{1}]不是数字类型", name, value);
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == predicate;
    }

    /**
     * 其中待构造的数据只匹配如下几种：[a,b]，[a,b)，(a,b]，(a,b)
     *
     * @param obj 待构造需要的数据
     */
    @Override
    @SuppressWarnings("all")
    public RangeMatcher build(String obj) {
        if(null == obj || "".equals(obj)){
            return null;
        }
        String startAli = null, endAli = null;
        Number begin = null, end = null;
        obj = obj.trim();
        if(obj.startsWith(LEFT_BRACKET_EQUAL)){
            startAli = LEFT_BRACKET_EQUAL;
            begin = parseNum(obj.substring(1, obj.indexOf(",")).trim());
        }else if(obj.startsWith(LEFT_BRACKET)){
            startAli = LEFT_BRACKET;
            begin = parseNum(obj.substring(1, obj.indexOf(",")).trim());
        }

        if(obj.endsWith(RIGHT_BRACKET_EQUAL)){
            endAli = RIGHT_BRACKET_EQUAL;
            end = parseNum(obj.substring(obj.indexOf(",") + 1, obj.indexOf("]")).trim());
        } else if(obj.endsWith(RIGHT_BRACKET)){
            endAli = RIGHT_BRACKET;
            end = parseNum(obj.substring(obj.indexOf(",") + 1, obj.indexOf(")")).trim());
        } else{
            return this;
        }

        String finalStartAli = startAli;
        String finalEndAli = endAli;
        express = obj;

        parser = new ExpressParser(Maps.of("begin", begin, "end", end));
        Number finalBegin = begin;
        Number finalEnd = end;
        predicate = o ->{
            parser.addBinding(Maps.of("o", o));
            if (null == finalBegin){
                if(null == finalEnd){
                    return true;
                }else{
                    if (RIGHT_BRACKET_EQUAL.equals(finalEndAli)) {
                        return parser.parse("o <= end");
                    } else if (RIGHT_BRACKET.equals(finalEndAli)) {
                        return parser.parse("o < end");
                    }
                }
            }else {
                if(null == finalEnd){
                    if (LEFT_BRACKET_EQUAL.equals(finalStartAli)) {
                        return parser.parse("begin <= o");
                    } else if (LEFT_BRACKET.equals(finalStartAli)) {
                        return parser.parse("begin < o");
                    }
                }else{
                    if (LEFT_BRACKET_EQUAL.equals(finalStartAli) && RIGHT_BRACKET_EQUAL.equals(finalEndAli)) {
                        return parser.parse("begin <= o && o <= end");
                    } else if (LEFT_BRACKET_EQUAL.equals(finalStartAli) && RIGHT_BRACKET.equals(finalEndAli)) {
                        return parser.parse("begin <= o && o < end");
                    } else if (LEFT_BRACKET.equals(finalStartAli) && RIGHT_BRACKET_EQUAL.equals(finalEndAli)) {
                        return parser.parse("begin < o && o <= end");
                    } else if (LEFT_BRACKET.equals(finalStartAli) && RIGHT_BRACKET.equals(finalEndAli)) {
                        return parser.parse("begin < o && o < end");
                    }
                }
            }

            return Boolean.parseBoolean(null);
        };
        return this;
    }

    private Number parseNum(String str){
        try {
            return Numbers.parseDecimal(str);
        }catch (NumberFormatException e){
            return null;
        }
    }
}
