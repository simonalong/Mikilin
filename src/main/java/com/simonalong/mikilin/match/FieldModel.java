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
    PHONE_NUM("手机号", "^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$"),
    FIXED_PHONE("固定电话", "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)(\\d{7,8})(-(\\d{3,}))?$"),
    MAIL("邮箱", "^([\\w-_]+(?:\\.[\\w-_]+)*)@[\\w-]+(.[\\w_-]+)+"),
    IP_ADDRESS("IP地址", "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"),;

    @Getter
    private final String name;
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
