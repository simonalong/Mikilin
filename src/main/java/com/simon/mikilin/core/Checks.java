package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMather;
import com.simon.mikilin.core.match.FieldJudge;
import com.simon.mikilin.core.util.ClassUtil;
import com.simon.mikilin.core.util.CollectionUtil;
import com.simon.mikilin.core.util.Maps;
import com.simon.mikilin.core.util.Objects;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/12/25 下午4:16
 */
@UtilityClass
@SuppressWarnings("unchecked")
public final class Checks {

    /**
     * 对象属性值白名单
     */
    private Map<String, Map<String, FieldJudge>> whiteFieldValueMap;
    /**
     * 对象属性值黑名单
     */
    private Map<String, Map<String, FieldJudge>> blackFieldValueMap;
    /**
     * 对象属性核查映射
     */
    private Map<String, Set<String>> objectFieldCheckMap;

    private CheckDelegate delegate;

    static {
        init();
    }

    private void init() {
        whiteFieldValueMap = new ConcurrentHashMap<>(20);
        blackFieldValueMap = new ConcurrentHashMap<>(20);
        objectFieldCheckMap = new ConcurrentHashMap<>(16);
        delegate = new CheckDelegate();
    }

    /**
     * 自定义的复杂类型校验，基本类型校验不校验，直接返回true
     *
     * @param object 待核查对象
     * @return true：成功，false：核查失败
     */
    public boolean check(Object object) {
        if (delegate.isEmpty(object)) {
            delegate.append("数据为空");
            return false;
        }

        if (ClassUtil.isBaseField(object.getClass())) {
            return true;
        } else {
            return check(object, getObjFieldMap(object), getWhiteMap(), getBlackMap());
        }
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * @param object 待核查对象
     * @param fieldsStr 待核查对象的多个属性名字
     * @return true：成功，false：核查失败
     */
    public boolean check(Object object, String... fieldsStr){
        // todo
        return false;
    }

    /**
     * 用于索引列表和黑白名单列表核查
     *
     * @param object 待核查的对象
     * @param objectFieldMap 对象的属性映射表，key为类的simpleName，value为当前类的属性的集合
     * @param whiteSet 属性的白名单映射表，key为类的simpleName，value为map，其中key为属性的名字，value为属性的可用值
     * @param blackSet 属性的白名单映射表，key为类的simpleName，value为map，其中key为属性的名字，value为属性的禁用值
     * @return 核查结果 true：核查成功；false：核查失败
     */
    private boolean check(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldJudge>> whiteSet, Map<String, Map<String, FieldJudge>> blackSet) {
        return delegate.available(object, objectFieldMap, whiteSet, blackSet);
    }

    public String getErrMsg() {
        return delegate.getErrMsg();
    }

    private Map<String, Set<String>> getObjFieldMap(Object object) {
        if (null == object) {
            return Collections.emptyMap();
        }

        // 若对象已经创建属性索引树，则直接返回
        if (objectFieldCheckMap.containsKey(object.getClass().getCanonicalName())) {
            return objectFieldCheckMap;
        }

        // 若当前对象没有对象属性索引树，则进行创建
        createObjectFieldMap(ClassUtil.peel(object));

        return objectFieldCheckMap;
    }

    private Map<String, Map<String, FieldJudge>> getWhiteMap() {
        return whiteFieldValueMap;
    }

    private Map<String, Map<String, FieldJudge>> getBlackMap() {
        return blackFieldValueMap;
    }

    /**
     * 根据对象的类型进行建立对象和属性映射树
     */
    private void createObjectFieldMap(Class<?> cls) {
        if (null == cls) {
            return;
        }
        // 剥离外部的一些壳之后类的类型
        String objectClsName = cls.getCanonicalName();

        Set<Field> fieldSet = ClassUtil.allFieldsOfClass(cls);
        if (!CollectionUtil.isEmpty(fieldSet)) {
            // 基本类型用于获取注解的属性
            fieldSet.forEach(f -> {
                FieldWhiteMather includeCheck = f.getAnnotation(FieldWhiteMather.class);
                if (null != includeCheck && !includeCheck.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addWhiteValueMap(whiteFieldValueMap, objectClsName, f, includeCheck);
                }

                FieldBlackMatcher excludeCheck = f.getAnnotation(FieldBlackMatcher.class);
                if (null != excludeCheck && !excludeCheck.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addBlackValueMap(blackFieldValueMap, objectClsName, f, excludeCheck);
                }
            });

            // 非基本类型拆分开进行迭代分析
            fieldSet.stream().filter(f -> !ClassUtil.isBaseField(f.getType())).forEach(f -> {
                // 该属性对应的类型是否添加了注解 TypeCheck
                Check check = f.getAnnotation(Check.class);
                if (null != check) {
                    addObjectFieldMap(objectClsName, f.getName());
                    createObjectFieldMap(ClassUtil.peel(f.getGenericType()));
                }
            });
        }
    }

    private void addObjectFieldMap(String objectClsName, String fieldName) {
        objectFieldCheckMap.compute(objectClsName, (k, v) -> {
            if (null == v) {
                Set<String> fieldSet = new HashSet<>();
                fieldSet.add(fieldName);
                return fieldSet;
            } else {
                v.add(fieldName);
                return v;
            }
        });
    }

    private void addWhiteValueMap(Map<String, Map<String, FieldJudge>> fieldMap, String objectName, Field field,
        FieldWhiteMather validValue) {
        fieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.of().add(field.getName(), FieldJudge.buildFromValid(field, validValue)).build();
            } else {
                v.put(field.getName(), FieldJudge.buildFromValid(field, validValue));
                return v;
            }
        });
    }

    private void addBlackValueMap(Map<String, Map<String, FieldJudge>> fieldMap, String objectName, Field field,
        FieldBlackMatcher invalidValue) {
        fieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.of().add(field.getName(), FieldJudge.buildFromInvalid(field, invalidValue)).build();
            } else {
                v.put(field.getName(), FieldJudge.buildFromInvalid(field, invalidValue));
                return v;
            }
        });
    }

    /**
     * 将设置的数据转换为对应结构类型的数据
     *
     * @param field 对象的属性类型
     * @param valueSet 属性的可用的或者不可用列表String形式
     * @return 转换为属性对象的值的可用或者不可用数据列表
     */
    private Set<Object> getObjectSet(Field field, String[] valueSet) {
        return Arrays.stream(valueSet).map(i -> {
            if (null != i && !"".equals(i)) {
                return Objects.cast(field.getType(), i);
            } else {
                return null;
            }
        }).collect(Collectors.toSet());
    }
}
