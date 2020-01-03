package com.simonalong.mikilin.match.matcher;

import com.alibaba.fastjson.JSON;
import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.express.ExpressParser;
import com.simonalong.mikilin.match.Builder;
import com.simonalong.mikilin.util.Maps;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.syntax.Numbers;
import org.springframework.util.StringUtils;

/**
 * 正则表达式判断，对应{@link WhiteMatcher#range()}或者{@link BlackMatcher#range()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
@Slf4j
@SuppressWarnings("all")
public class RangeMatcher extends AbstractBlackWhiteMatcher implements Builder<RangeMatcher, String> {

    private static final String LEFT_BRACKET_EQUAL = "[";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET_EQUAL = "]";
    private static final String RIGHT_BRACKET = ")";
    private static final String NULL_STR = "null";
    private static final String NOW = "now";
    private static final String PAST = "past";
    private static final String FUTURE = "future";
    private static final Pattern yPattern = Pattern.compile("^(\\d){4}$");
    private static final Pattern ymPattern = Pattern.compile("^(\\d){4}-(\\d){2}$");
    private static final Pattern ymdPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2}$");
    private static final Pattern ymdhPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}$");
    private static final Pattern ymdhmPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}$");
    private static final Pattern ymdhmsPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}$");
    private static final Pattern ymdhmssPattern = Pattern.compile("^(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}.(\\d){3}$");
    private static final SimpleDateFormat yFormat = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat ymFormat = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat ymdhFormat = new SimpleDateFormat("yyyy-MM-dd HH");
    private static final SimpleDateFormat ymdhmFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat ymdhmsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat ymdhmssFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /**
     * 全是数字匹配（整数，浮点数，0，负数）
     */
    private Pattern digitPattern = Pattern.compile("^(-?[1-9]\\d*)|0|(-?[1-9]\\d*.\\d*)|(0.\\d*[1-9]\\d*)|(0?.0+)$");
    /**
     * 时间或者数字范围匹配
     */
    private Pattern rangePattern = Pattern.compile("^(\\(|\\[){1}(.+),(\\s)*(.+)(\\)|\\]){1}$");
    /**
     * 判决对象
     */
    private Predicate<Object> predicate;
    /**
     * 时间类型标示
     */
    private Boolean dateFlag = false;
    /**
     * 属性的类型：0-数字类型，1-时间类型，2-集合类型
     */
    private Integer dataType;
    /**
     * 表达式解析对象
     */
    private ExpressParser parser;
    /**
     * 表达式
     */
    private String express;
    /**
     * 存放now这个时间的map
     */
    private Map<String, Date> timeMap = new HashMap<>();

    @Override
    public boolean match(Object object, String name, Object value) {
        if (value instanceof Number) {
            if (dateFlag) {
                try {
                    value = new Date(Number.class.cast(value).longValue());
                } catch (Exception ignore) {}
            }
            return match(name, value, RangeDataType.NUM_TYPE);
        } else if (value instanceof Date) {
            return match(name, value, RangeDataType.DATE_TYPE);
        } else if (value instanceof Collection) {
            return match(name, Collection.class.cast(value).size(), RangeDataType.COLLECTION_TYPE);
        } else {
            setWhiteMsg("属性 {0} 的值 {1} 不是数字也不是时间类型", name, value);
        }
        return false;
    }

    private Boolean match(String name, Object value, RangeDataType dataType) {
        boolean result = predicate.test(value);
        if (result) {
            setBlackMsg("属性 {0} 的 {1} 位于禁用的范围 {2} 中", name, format(value, dataType), replaceSystem(express));
            return true;
        } else {
            setWhiteMsg("属性 {0} 的 {1} 没有命中只允许的范围 {2} 中", name, format(value, dataType), replaceSystem(express));
            return false;
        }
    }

    private Object format(Object value, RangeDataType dataType){
        switch (dataType){
            case NUM_TYPE:{
                return "值 " + value;
            }
            case DATE_TYPE:{
                return "值 " + ymdhmssFormat.format(value);
            }
            case COLLECTION_TYPE:{
                return "集合个数 " + JSON.toJSONString(value);
            }
            default:return "";
        }
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
    public RangeMatcher build(String obj) {
        if (null == obj || "".equals(obj)) {
            return null;
        }

        RangeEntity rangeEntity = parseRange(obj);
        if (null == rangeEntity) {
            return null;
        }

        express = obj;
        this.dateFlag = rangeEntity.getDateFlag();

        String beginAli = rangeEntity.getBeginAli();
        Object begin = rangeEntity.getBegin();
        Object end = rangeEntity.getEnd();
        String endAli = rangeEntity.getEndAli();
        parser = new ExpressParser(Maps.of("begin", begin, "end", end));

        predicate = o -> {
            parser.addBinding(Maps.of("o", o));
            if (null == begin) {
                if (null == end) {
                    return true;
                } else {
                    if (RIGHT_BRACKET_EQUAL.equals(endAli)) {
                        return parser.parse("o <= end");
                    } else if (RIGHT_BRACKET.equals(endAli)) {
                        return parser.parse("o < end");
                    }
                }
            } else {
                if (null == end) {
                    if (LEFT_BRACKET_EQUAL.equals(beginAli)) {
                        return parser.parse("begin <= o");
                    } else if (LEFT_BRACKET.equals(beginAli)) {
                        return parser.parse("begin < o");
                    }
                } else {
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
     * @return 解析后的时间范围类型
     */
    private RangeEntity parseRange(String input) {
        input = input.trim();
        Matcher matcher = rangePattern.matcher(input);
        if (matcher.find()) {
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
            if (digitPattern.matcher(begin).matches() || digitPattern.matcher(end).matches()) {
                return RangeEntity.build(beginAli, parseNum(begin), parseNum(end), endAli);
            } else {
                Date beginDate = parseDate(begin);
                Date endDate = parseDate(end);
                if (null != beginDate && null != endDate) {
                    if(beginDate.compareTo(endDate) > 0){
                        log.error("时间的范围起始点不正确，起点时间不应该大于终点时间");
                        return null;
                    }
                    return RangeEntity.build(beginAli, beginDate, endDate, endAli);
                } else if(null == beginDate && null == endDate){
                    log.error("range 匹配器格式输入错误，解析数字或者日期失败, input={}", input);
                } else {
                    return RangeEntity.build(beginAli, beginDate, endDate, endAli);
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

    private Number parseNum(String data) {
        try {
            if (NULL_STR.equals(data)){
                return null;
            }
            return Numbers.parseDecimal(data);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析时间
     * <p>
     *     时间格式为如下
     * <ul>
     * <li>yyyy
     * <li>yyyy-MM
     * <li>yyyy-MM-dd
     * <li>yyyy-MM-dd HH
     * <li>yyyy-MM-dd HH:mm
     * <li>yyyy-MM-dd HH:mm:ss
     * <li>yyyy-MM-dd HH:mm:ss.SSS
     * </ul>
     *
     * @param data 可以为指定的几个时间格式，也可以为两个时间函数
     * @return yyyy-MM-dd HH:mm:ss.SSS 格式的时间类型
     */
    private Date parseDate(String data) {
        data = data.trim();
        data = data.replace('\'',' ').trim();
        if (StringUtils.isEmpty(data) || NULL_STR.equals(data)){
            return null;
        }
        if (data.equals(NOW)) {
            Date now = new Date();
            timeMap.put(NOW, now);
            return now;
        }
        try {
            if (yPattern.matcher(data).matches()) {
                return yFormat.parse(data);
            } else if (ymPattern.matcher(data).matches()) {
                return ymFormat.parse(data);
            } else if (ymdPattern.matcher(data).matches()) {
                return ymdFormat.parse(data);
            } else if (ymdhPattern.matcher(data).matches()) {
                return ymdhFormat.parse(data);
            } else if (ymdhmPattern.matcher(data).matches()) {
                return ymdhmFormat.parse(data);
            } else if (ymdhmsPattern.matcher(data).matches()) {
                return ymdhmsFormat.parse(data);
            } else if (ymdhmssPattern.matcher(data).matches()) {
                return ymdhmssFormat.parse(data);
            }
            log.error("解析时间错误, data={}", data);
        } catch (Exception e) {
            log.error("解析时间错误, data={}, e={}", data, e);
        }
        return null;
    }

    /**
     * 解析过去和未来的时间
     *
     * @param data past或者future两个函数
     * @return 时间范围的实体()
     */
    private RangeEntity parseRangeDate(String data) {
        if (data.equals(PAST)) {
            // 过去，则范围为(null, now)
            return RangeEntity.build(LEFT_BRACKET, null, new Date(), RIGHT_BRACKET);
        } else if (data.equals(FUTURE)) {
            // 未来，则范围为(now, null)
            return RangeEntity.build(LEFT_BRACKET, new Date(), null, RIGHT_BRACKET);
        }
        return null;
    }

    /**
     * 替换系统内置的变量
     *
     * @param str 待替换的字符
     * @return 替换后的字符串表达式
     */
    private String replaceSystem(String str) {
        AtomicReference<String> result = new AtomicReference<>(str);
        timeMap.forEach((key, value) -> {
            if (str.contains(key)) {
                result.set(str.replace(key, ymdhmssFormat.format(value)));
            }
        });
        return result.get();
    }

    @Data
    @Accessors(chain = true)
    static class RangeEntity {

        String beginAli = null;
        Object begin;
        Object end;
        String endAli = null;
        Boolean dateFlag = false;

        public static RangeEntity build(String beginAli, Object begin, Object end, String endAli) {
            RangeEntity result = new RangeEntity().setBeginAli(beginAli).setBegin(begin).setEnd(end).setEndAli(endAli);
            if ((begin instanceof Date) || (end instanceof Date)){
                result.setDateFlag(true);
            }
            return result;
        }
    }

    public enum RangeDataType {
        DATE_TYPE,
        NUM_TYPE,
        COLLECTION_TYPE
    }
}
