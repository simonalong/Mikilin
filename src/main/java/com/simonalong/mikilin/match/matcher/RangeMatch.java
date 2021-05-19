package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.express.ExpressParser;
import com.simonalong.mikilin.match.Builder;
import com.simonalong.mikilin.util.LocalDateTimeUtil;
import com.simonalong.mikilin.util.Maps;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.syntax.Numbers;
import org.springframework.util.StringUtils;

import static com.simonalong.mikilin.MkConstant.MK_LOG_PRE;

/**
 * 范围匹配，对应{@link Matcher#range()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
@Slf4j
@SuppressWarnings("all")
public class RangeMatch extends AbstractBlackWhiteMatch implements Builder<RangeMatch, String> {

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
    private Pattern digitPattern = Pattern.compile("^[-+]?([1-9]+\\d*|0\\.(\\d*)|[1-9]\\d*\\.(\\d*))$");
    /**
     * 时间或者数字范围匹配
     */
    private Pattern rangePattern = Pattern.compile("^(\\(|\\[){1}(.*),(\\s)*(.*)(\\)|\\]){1}$");
    /**
     * 时间的前后计算匹配：(-|+)yMd(h|H)msS
     */
    private Pattern timePlusPattern = Pattern.compile("^([-+])?(\\d*y)?(\\d*M)?(\\d*d)?(\\d*H|\\d*h)?(\\d*m)?(\\d*s)?$");
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
                return match(name, value, RangeDataType.DATE_TYPE);
            } else {
                return match(name, value, RangeDataType.NUM_TYPE);
            }
        } else if (value instanceof Date) {
            return match(name, ((Date) value).getTime(), RangeDataType.DATE_TYPE);
        } else if (value instanceof LocalDateTime) {
            return match(name, LocalDateTimeUtil.localDateTimeToLong((LocalDateTime)value), RangeDataType.DATE_TYPE);
        } else if (value instanceof LocalDate) {
            return match(name, LocalDateTimeUtil.localDateToLong((LocalDate) value), RangeDataType.DATE_TYPE);
        } else if (value instanceof Timestamp) {
            return match(name, LocalDateTimeUtil.timestampToLong((Timestamp) value), RangeDataType.DATE_TYPE);
        } else if (value instanceof Collection) {
            return match(name, Collection.class.cast(value).size(), RangeDataType.COLLECTION_TYPE);
        } else if (value instanceof CharSequence) {
            return match(name, CharSequence.class.cast(value).length(), RangeDataType.CHAR_SEQUENCE);
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
            setWhiteMsg("属性 {0} 的 {1} 没有命中只允许的范围 {2}", name, format(value, dataType), replaceSystem(express));
            return false;
        }
    }

    private Object format(Object value, RangeDataType dataType) {
        switch (dataType) {
            case NUM_TYPE: {
                return "值 " + value;
            }
            case DATE_TYPE: {
                String time = null;
                if (value instanceof Date) {
                    time = LocalDateTimeUtil.dateToString((Date) value);
                } else if (value instanceof Timestamp) {
                    time = LocalDateTimeUtil.timestampToString((Timestamp) value);
                } else if (value instanceof LocalDateTime) {
                    time = LocalDateTimeUtil.localDateTimeToString((LocalDateTime) value);
                } else if (value instanceof LocalDate) {
                    time = LocalDateTimeUtil.localDateToString((LocalDate) value);
                } else if (value instanceof Long) {
                    time = LocalDateTimeUtil.longToString((Long) value);
                } else if (value instanceof LocalTime) {
                    time = LocalDateTimeUtil.localTimeToString((LocalTime) value);
                }

                if (null != time) {
                    return "时间对应的值 " + time;
                }
                return "";
            }
            case COLLECTION_TYPE: {
                return "集合个数 " + value;
            }
            case CHAR_SEQUENCE: {
                return "字符个数 " + value;
            }
            default:
                return "";
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
    public RangeMatch build(String obj) {
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

            Object beginFinal = begin;
            Object endFinal = end;
            if (rangeEntity.getDynamicTime()) {
                beginFinal = rangeEntity.generateBegin();
                endFinal = rangeEntity.generateEnd();
                parser.addBinding(Maps.of("begin", beginFinal, "end", endFinal));
            }

            if (null == beginFinal) {
                if (null == endFinal) {
                    return true;
                } else {
                    if (RIGHT_BRACKET_EQUAL.equals(endAli)) {
                        return parser.parse("o <= end");
                    } else if (RIGHT_BRACKET.equals(endAli)) {
                        return parser.parse("o < end");
                    }
                }
            } else {
                if (null == endFinal) {
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
        java.util.regex.Matcher matcher = rangePattern.matcher(input);
        if (matcher.find()) {
            String beginAli = matcher.group(1);
            String begin = matcher.group(2);
            String end = matcher.group(4);
            String endAli = matcher.group(5);

            if ((begin.equals(NULL_STR) || "".equals(begin)) && (end.equals(NULL_STR) || "".equals(end))) {
                log.error(MK_LOG_PRE + "range匹配器格式输入错误，start和end不可都为null或者空字符, input={}", input);
            } else if (begin.equals(PAST) || begin.equals(FUTURE)) {
                log.error(MK_LOG_PRE + "range匹配器格式输入错误, start不可含有past或者future, input={}", input);
            } else if (end.equals(PAST) || end.equals(FUTURE)) {
                log.error(MK_LOG_PRE + "range匹配器格式输入错误, end不可含有past或者future, input={}", input);
            }

            // 如果是数字，则按照数字解析
            if (digitPattern.matcher(begin).matches() || digitPattern.matcher(end).matches()) {
                return RangeEntity.build(beginAli, parseNum(begin), parseNum(end), endAli, false);
            } else if (timePlusPattern.matcher(begin).matches() || timePlusPattern.matcher(end).matches()) {
                // 解析动态时间
                DynamicTimeNum timeNumBegin = parseDynamicTime(begin);
                DynamicTimeNum timeNumEnd = parseDynamicTime(end);

                if (null != timeNumBegin && null != timeNumEnd && timeNumBegin.compareTo(timeNumEnd) > 0) {
                    log.error(MK_LOG_PRE + "时间的动态时间不正确，动态起点时间不应该大于动态终点时间");
                    return null;
                }

                if (null == timeNumBegin && null == timeNumEnd) {
                    log.error(MK_LOG_PRE + "动态时间解析失败");
                    return null;
                }
                return RangeEntity.build(beginAli, timeNumBegin, timeNumEnd, endAli);
            } else {
                Date beginDate = parseDate(begin);
                Date endDate = parseDate(end);
                if (null != beginDate && null != endDate) {
                    if (beginDate.compareTo(endDate) > 0) {
                        log.error(MK_LOG_PRE + "时间的范围起始点不正确，起点时间不应该大于终点时间");
                        return null;
                    }
                    return RangeEntity.build(beginAli, LocalDateTimeUtil.dateToLong(beginDate), LocalDateTimeUtil.dateToLong(endDate), endAli, true);
                } else if (null == beginDate && null == endDate) {
                    log.error(MK_LOG_PRE + "range 匹配器格式输入错误，解析数字或者日期失败, input={}", input);
                } else {
                    return RangeEntity.build(beginAli, LocalDateTimeUtil.dateToLong(beginDate), LocalDateTimeUtil.dateToLong(endDate), endAli, true);
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

    private DynamicTimeNum parseDynamicTime(String data) {
        if (null == data || "".equals(data)) {
            return null;
        }
        java.util.regex.Matcher matcher = timePlusPattern.matcher(data);
        if (matcher.find()) {
            return new DynamicTimeNum().setPlusOrMinus(matcher.group(1))
                .setYears(matcher.group(2))
                .setMonths(matcher.group(3))
                .setDays(matcher.group(4))
                .setHours(matcher.group(5))
                .setMinutes(matcher.group(6))
                .setSeconds(matcher.group(7));
        }
        return null;
    }

    private Number parseNum(String data) {
        try {
            if (data.equals(NULL_STR) || "".equals(data)) {
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
     * 时间格式为如下
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
        data = data.replace('\'', ' ').trim();
        if (StringUtils.isEmpty(data) || NULL_STR.equals(data)) {
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
            log.error(MK_LOG_PRE + "解析时间错误, data={}", data);
        } catch (Exception e) {
            log.error(MK_LOG_PRE + "解析时间错误, data={}, e={}", data, e);
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
            return RangeEntity.build(LEFT_BRACKET, null, System.currentTimeMillis(), RIGHT_BRACKET, true);
        } else if (data.equals(FUTURE)) {
            // 未来，则范围为(now, null)
            return RangeEntity.build(LEFT_BRACKET, System.currentTimeMillis(), null, RIGHT_BRACKET, true);
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
        Boolean dynamicTime = false;

        public static RangeEntity build(String beginAli, Object begin, Object end, String endAli, Boolean dateFlag) {
            RangeEntity result = new RangeEntity().setBeginAli(beginAli).setBegin(begin).setEnd(end).setEndAli(endAli);
            result.setDateFlag(dateFlag);
            return result;
        }

        public static RangeEntity build(String beginAli, DynamicTimeNum begin, DynamicTimeNum end, String endAli) {
            RangeEntity result = new RangeEntity().setBeginAli(beginAli).setBegin(begin).setEnd(end).setEndAli(endAli);
            return result.setDynamicTime(true);
        }

        public Object generateBegin() {
            if (null != begin) {
                DynamicTimeNum timeNum = DynamicTimeNum.class.cast(begin);
                if (timeNum.plusOrMinus) {
                    return LocalDateTimeUtil.plusTime(System.currentTimeMillis(), timeNum.getYears(), timeNum.getMonths(), timeNum.getDays(), timeNum.getHours(),
                        timeNum.getMinutes(), timeNum.getSeconds());
                } else {
                    return LocalDateTimeUtil.plusTime(System.currentTimeMillis(), -timeNum.getYears(), -timeNum.getMonths(), -timeNum.getDays(), -timeNum.getHours(),
                        -timeNum.getMinutes(), -timeNum.getSeconds());
                }
            }
            return null;
        }

        public Object generateEnd() {
            if (null != end) {
                DynamicTimeNum timeNum = DynamicTimeNum.class.cast(end);
                if (timeNum.plusOrMinus) {
                    return LocalDateTimeUtil.plusTime(System.currentTimeMillis(), timeNum.getYears(), timeNum.getMonths(), timeNum.getDays(), timeNum.getHours(),
                        timeNum.getMinutes(), timeNum.getSeconds());
                } else {
                    return LocalDateTimeUtil.plusTime(System.currentTimeMillis(), -timeNum.getYears(), -timeNum.getMonths(), -timeNum.getDays(), -timeNum.getHours(),
                        -timeNum.getMinutes(), -timeNum.getSeconds());
                }
            }
            return null;
        }
    }

    @Getter
    private static class DynamicTimeNum implements Comparable<DynamicTimeNum> {

        /**
         * 是增加（正数）还是减少（负数）：true-增加，false-减少
         */
        private Boolean plusOrMinus = true;
        private Integer years;
        private Integer months;
        private Integer days;
        private Integer hours;
        private Integer minutes;
        private Integer seconds;

        public Long generateTimeMillis() {
            if (plusOrMinus) {
                return LocalDateTimeUtil.plusTime(System.currentTimeMillis(), years, months, days, hours, minutes, seconds);
            } else {
                return LocalDateTimeUtil.plusTime(System.currentTimeMillis(), -years, -months, -days, -hours, -minutes, -seconds);
            }
        }

        public DynamicTimeNum setPlusOrMinus(String plusOrMinusStr) {
            if (null != plusOrMinusStr && !"".equals(plusOrMinusStr)) {
                if ("-".equals(plusOrMinusStr)) {
                    plusOrMinus = false;
                }
            }
            return this;
        }

        public DynamicTimeNum setYears(String yearsStr) {
            this.years = doGetNum(yearsStr);
            return this;
        }

        public DynamicTimeNum setMonths(String monthsStr) {
            this.months = doGetNum(monthsStr);
            return this;
        }

        public DynamicTimeNum setDays(String daysStr) {
            this.days = doGetNum(daysStr);
            return this;
        }

        public DynamicTimeNum setHours(String hoursStr) {
            this.hours = doGetNum(hoursStr);
            return this;
        }

        public DynamicTimeNum setMinutes(String minutesStr) {
            this.minutes = doGetNum(minutesStr);
            return this;
        }

        public DynamicTimeNum setSeconds(String secondsStr) {
            this.seconds = doGetNum(secondsStr);
            return this;
        }

        private Integer doGetNum(String dataStr) {
            if (null != dataStr && !"".equals(dataStr)) {
                return Integer.valueOf(dataStr.substring(0, dataStr.length() - 1));
            }
            return 0;
        }

        @Override
        public int compareTo(DynamicTimeNum o) {
            return generateTimeMillis().compareTo(o.generateTimeMillis());
        }
    }

    public enum RangeDataType {
        DATE_TYPE,
        NUM_TYPE,
        COLLECTION_TYPE,
        CHAR_SEQUENCE
    }
}
