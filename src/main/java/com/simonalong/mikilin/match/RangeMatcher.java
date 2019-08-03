package com.simonalong.mikilin.match;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.express.ExpressParser;
import com.simonalong.mikilin.util.Maps;
import java.util.Date;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.syntax.Numbers;

/**
 * 正则表达式判断，对应{@link FieldWhiteMatcher#range()}或者{@link FieldBlackMatcher#range()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
@Slf4j
public class RangeMatcher extends AbstractBlackWhiteMatcher implements Builder<RangeMatcher, String> {

    private static final String LEFT_BRACKET_EQUAL = "[";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET_EQUAL = "]";
    private static final String RIGHT_BRACKET = ")";
    private static final String NULL_STR = "null";
    private static final String PAST = "past";
    private static final String FUTURE = "future";
    /**
     * 全是数字匹配
     */
    private Pattern digitPattern = Pattern.compile("^\\d*$");
    /**
     * 时间或者数字范围匹配
     */
    private Pattern rangePattern = Pattern.compile("^(\\(|\\[){1}(\\S+),(\\s)*(\\S+)(\\)|\\]){1}$");
    /**
     * 判决对象
     */
    private Predicate<Object> predicate;
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
            Number number = (Number) value;
            // todo 这里也要区分，如果是long类型，则要考虑是否是时间类型的解析
            boolean result = predicate.test(number);
            if (result){
                setBlackMsg("属性[{0}]的值[{1}]位于黑名单对应的范围[{2}]中", name, number, express);
                return true;
            }else{
                setWhiteMsg("属性[{0}]的值[{1}]没有在白名单对应的范围[{2}]中", name, number, express);
            }
        } else if(value instanceof Date){
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

        RangeEntity rangeEntity = parseRange(obj);
        if (null == rangeEntity){
           return null;
        }

        express = obj;

        String beginAli = rangeEntity.getBeginAli();
        Object begin = rangeEntity.getBegin();
        Object end = rangeEntity.getEnd();
        String endAli = rangeEntity.getEndAli();
        parser = new ExpressParser(Maps.of("begin", begin, "end", end));

        predicate = o ->{
            parser.addBinding(Maps.of("o", o));
            if (null == begin){
                if(null == end){
                    return true;
                }else{
                    if (RIGHT_BRACKET_EQUAL.equals(endAli)) {
                        return parser.parse("o <= end");
                    } else if (RIGHT_BRACKET.equals(endAli)) {
                        return parser.parse("o < end");
                    }
                }
            }else {
                if(null == end){
                    if (LEFT_BRACKET_EQUAL.equals(beginAli)) {
                        return parser.parse("begin <= o");
                    } else if (LEFT_BRACKET.equals(beginAli)) {
                        return parser.parse("begin < o");
                    }
                }else{
                    if (LEFT_BRACKET_EQUAL.equals(beginAli) && RIGHT_BRACKET_EQUAL.equals(endAli)) {
                        return parser.parse("begin <= o && o <= end");
                    } else if (LEFT_BRACKET_EQUAL.equals(beginAli) && RIGHT_BRACKET.equals(endAli)) {
                        return parser.parse("begin <= o && o < end");
                    } else if (LEFT_BRACKET.equals(beginAli) && RIGHT_BRACKET_EQUAL.equals(endAli)) {
                        return parser.parse("begin < o && o <= end");
                    } else if (LEFT_BRACKET.equals(beginAli) && RIGHT_BRACKET.equals(endAli)) {
                        return parser.parse("begin < o && o < end");
                    }
                }
            }
            return Boolean.parseBoolean(null);
        };
        return this;
    }

    /**
     * 解析时间范围格式
     *
     * @param input 时间范围格式
     */
    private RangeEntity parseRange(String input) {
        input = input.trim();
        Matcher matcher = rangePattern.matcher(input);
        if(matcher.find()){
            String beginAli = matcher.group(1);
            String begin = matcher.group(2);
            String end = matcher.group(4);
            String endAli = matcher.group(5);

            if (begin.equals(NULL_STR) && end.equals(NULL_STR)) {
                log.error("range匹配器格式输入错误，start和end不可都为null, input={}", input);
            } else if (begin.equals(PAST) || begin.equals(FUTURE)) {
                log.error("range匹配器格式输入错误, start不可含有past或者future, input={}", input);
            } else if (end.equals(PAST) || end.equals(FUTURE)) {
                log.error("range匹配器格式输入错误, end不可含有past或者future, input={}", input);
            }

            // 如果是数字，则按照数字解析
            if (digitPattern.matcher(begin).matches() && digitPattern.matcher(end).matches()) {
                return RangeEntity.build(beginAli, parseNum(begin), parseNum(end), endAli);
            } else {
                Date beginDate = parseDate(begin);
                Date endDate = parseDate(end);
                if (null != beginDate && null != endDate) {
                    return RangeEntity.build(beginAli, beginDate, endDate, endAli);
                } else {
                    log.error("range 匹配器格式输入错误，解析数字或者日期失败, input={}", input);
                }
                return null;
            }
        } else {
            // 匹配过去和未来的时间
            if (input.equals(PAST) || input.equals(FUTURE)) {
                return parseRangeDate(input);
            }
        }
        return null;
    }


    private Number parseNum(String data){
        try {
            return Numbers.parseDecimal(data);
        }catch (NumberFormatException e){
            return null;
        }
    }

    /**
     * 解析时间
     * 时间格式为如下
     * yyyy
     * yyyy-MM
     * yyyy-MM-dd
     * yyyy-MM-dd HH
     * yyyy-MM-dd HH:mm
     * yyyy-MM-dd HH:mm:ss
     * yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param data 可以为指定的几个时间格式，也可以为两个时间函数
     * @return yyyy-MM-dd HH:mm:ss.SSS 格式的时间类型
     */
    private Date parseDate(String data){
        // todo
    }

    /**
     * 解析过去和未来的时间
     *
     * @param data past或者future两个函数
     * @return 时间范围的实体()
     */
    private RangeEntity parseRangeDate(String data){
        // todo
    }

    @Data
    @Accessors(chain = true)
    static class RangeEntity{
        String beginAli = null;
        Object begin;
        Object end;
        String endAli = null;

        public static RangeEntity build(String beginAli, Object begin, Object end, String endAli){
            return new RangeEntity().setBeginAli(beginAli).setBegin(begin).setEnd(end).setEndAli(endAli);
        }
    }
}
