package com.github.simonalong.mikilin;

import com.github.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.github.simonalong.mikilin.match.FieldJudge;
import com.github.simonalong.mikilin.util.Maps;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouzhenyong
 * @since 2019/6/15 上午9:24
 */
public class MatcherManager {

    /**
     * 存储对象和属性以及属性对应的匹配器的映射，key为类的全路径，二级key为类的属性名字，二级value为属性的判断核查器
     */
    private Map<String, Map<String, FieldJudge>> targetFieldMap;

    public MatcherManager(){
        targetFieldMap = new ConcurrentHashMap<>(16);
    }

    @SuppressWarnings("unchecked")
    public MatcherManager addWhite(String objectName, Field field, FieldWhiteMatcher validValue){
        targetFieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.of().add(field.getName(), FieldJudge.buildFromValid(field, validValue)).build();
            } else {
                v.put(field.getName(), FieldJudge.buildFromValid(field, validValue));
                return v;
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public MatcherManager addBlack(String objectName, Field field, FieldBlackMatcher validValue){
        targetFieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.of().add(field.getName(), FieldJudge.buildFromInvalid(field, validValue)).build();
            } else {
                v.put(field.getName(), FieldJudge.buildFromInvalid(field, validValue));
                return v;
            }
        });
        return this;
    }

    public Map<String, FieldJudge> getJudge(String targetClassName){
        return targetFieldMap.get(targetClassName);
    }
}
