package com.simonalong.mikilin.match;

import java.util.regex.Pattern;
import lombok.Getter;

/**
 * 常见的匹配的类型
 *
 * @author zhouzhenyong
 * @since 2019/3/7 下午9:31
 */
public enum FieldModel {
    /**
     * 默认全部可用
     */
    DEFAULT("全部", "^.*$"),
    ID_CARD("身份证号"),
    PHONE_NUM("手机号", "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[35678]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|66\\d{2})\\d{6}$"),
    FIXED_PHONE("固定电话", "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)(\\d{7,8})(-(\\d{3,}))?$"),
    MAIL("邮箱", "^([\\w-_]+(?:\\.[\\w-_]+)*)@[\\w-]+(.[\\w_-]+)+"),
    IP_ADDRESS("IP地址", "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"),;

    @Getter
    private String name;
    private String regex;

    FieldModel(String name) {
        this.name = name;
    }

    FieldModel(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    public boolean match(String content) {
        if (null == content) {
            return false;
        }
        return Pattern.matches(this.regex, content);
    }
}
