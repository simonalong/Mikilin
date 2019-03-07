package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.annotation.FieldExcludeCheck;
import com.simon.mikilin.core.annotation.FieldIncludeCheck;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    private Map<String, Map<String, Set<Object>>> whiteFieldValueMap;
    /**
     * 对象属性值黑名单
     */
    private Map<String, Map<String, Set<Object>>> blackFieldValueMap;
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
     * 自定义的复杂类型校验，基本类型校验不校验，基本类型校验为baseCheck
     */
    public boolean check(Object object) {
        if (delegate.isEmpty(object)) {
            delegate.append("数据为空");
            return false;
        }

        if (ClassUtil.isBaseField(object.getClass())) {
            return true;
        } else {
            return check(object, getObjFieldMap(object), getWhiteMap(object), getBlackMap(object));
        }
    }

    /**
     * 开放接口，用于用户自定义索引列表和黑白名单列表
     *
     * @param object 待核查的对象
     * @param objectFieldMap 对象的属性映射表，key为类的simpleName，value为当前类的属性的集合
     * @param whiteSet 属性的白名单映射表，key为类的simpleName，value为map，其中key为属性的名字，value为属性的可用值
     * @param blackSet 属性的白名单映射表，key为类的simpleName，value为map，其中key为属性的名字，value为属性的禁用值
     * @return 核查结果 true：核查成功；false：核查失败
     */
    public boolean check(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, Set<Object>>> whiteSet, Map<String, Map<String, Set<Object>>> blackSet) {
        return delegate.available(object, objectFieldMap, whiteSet, blackSet);
    }

    /**
     * 类型白名单核查 注意： 建议基本类型使用，自定义类型、集合和Map类型不建议使用
     */
    public <T> boolean checkWhite(T object, Set<T> whiteSet) {
        return check(object, whiteSet, Collections.emptySet());
    }

    public <T> boolean checkWhite(T object, List<T> whiteSet) {
        return check(object, new HashSet<>(whiteSet), Collections.emptySet());
    }

    public <T> boolean checkWhite(T object, T... whiteSet) {
        return check(object, new HashSet<>(Arrays.asList(whiteSet)), Collections.emptySet());
    }

    /**
     * 类型黑名单核查 注意： 建议基本类型使用，自定义类型、集合和Map类型不建议使用
     */
    public <T> boolean checkBlack(T object, Set<T> blackSet) {
        return check(object, Collections.emptySet(), blackSet);
    }

    public <T> boolean checkBlack(T object, List<T> blackSet) {
        return check(object, Collections.emptySet(), new HashSet<>(blackSet));
    }

    public <T> boolean checkBlack(T object, T... blackSet) {
        return check(object, Collections.emptySet(), new HashSet<>(Arrays.asList(blackSet)));
    }

    private <T> boolean check(T object, Set<T> whiteSet, Set<T> blackSet) {
        return delegate.available(object, whiteSet, blackSet);
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

    private Map<String, Map<String, Set<Object>>> getWhiteMap(Object object) {
        return whiteFieldValueMap;
    }

    private Map<String, Map<String, Set<Object>>> getBlackMap(Object object) {
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
                FieldIncludeCheck includeCheck = f.getAnnotation(FieldIncludeCheck.class);
                if (null != includeCheck && !includeCheck.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addValueMap(whiteFieldValueMap, objectClsName, f, includeCheck.value());
                }

                FieldExcludeCheck excludeCheck = f.getAnnotation(FieldExcludeCheck.class);
                if (null != excludeCheck && !excludeCheck.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addValueMap(blackFieldValueMap, objectClsName, f, excludeCheck.value());
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

    private void addValueMap(Map<String, Map<String, Set<Object>>> fieldMap, String objectName, Field field,
        String[] values) {
        if (values.length == 0) {
            return;
        }

        fieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.builder().add(field.getName(), new HashSet<>(getObjectSet(field, values))).build();
            } else {
                v.compute(field.getName(), (bk, bv) -> {
                    if (null == bv) {
                        return new HashSet<>(getObjectSet(field, values));
                    } else {
                        bv.addAll(getObjectSet(field, values));
                        return bv;
                    }
                });
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
