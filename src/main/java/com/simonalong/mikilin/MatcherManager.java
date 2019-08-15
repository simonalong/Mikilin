package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.match.FieldJudge;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.Maps;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 匹配器管理器
 *
 * @author zhouzhenyong
 * @since 2019/6/15 上午9:24
 */
final class MatcherManager {

    /**
     * 存储对象和属性以及属性对应的匹配器的映射，key为类的全路径，二级key为类的属性名字，二级value为属性的判断核查器
     */
    private Map<String, Map<String, FieldJudge>> targetFieldMap;

    MatcherManager() {
        targetFieldMap = new ConcurrentHashMap<>(16);
    }

    @SuppressWarnings("unchecked")
    MatcherManager addWhite(String objectName, Field field, FieldWhiteMatcher validValue, MkContext context) {
        targetFieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.of(field.getName(), FieldJudge.buildFromValid(field, validValue, context)).build();
            } else {
                v.put(field.getName(), FieldJudge.buildFromValid(field, validValue, context));
                return v;
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    MatcherManager addBlack(String objectName, Field field, FieldBlackMatcher validValue, MkContext context) {
        targetFieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.of(field.getName(), FieldJudge.buildFromInvalid(field, validValue, context)).build();
            } else {
                v.put(field.getName(), FieldJudge.buildFromInvalid(field, validValue, context));
                return v;
            }
        });
        return this;
    }

    Map<String, FieldJudge> getJudge(String targetClassName) {
        return targetFieldMap.get(targetClassName);
    }
}
