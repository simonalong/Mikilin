package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.annotation.FieldType;
import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import com.simon.mikilin.core.match.FieldJudge;
import com.simon.mikilin.core.util.ClassUtil;
import com.simon.mikilin.core.util.CollectionUtil;
import com.simon.mikilin.core.util.Maps;
import com.simon.mikilin.core.util.Objects;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
     * 自定义的复杂类型校验，基本类型校验不校验，基本类型校验为baseCheck
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
     * 开放接口，用于用户自定义索引列表和黑白名单列表
     *
     * @param object 待核查的对象
     * @param objectFieldMap 对象的属性映射表，key为类的simpleName，value为当前类的属性的集合
     * @param whiteSet 属性的白名单映射表，key为类的simpleName，value为map，其中key为属性的名字，value为属性的可用值
     * @param blackSet 属性的白名单映射表，key为类的simpleName，value为map，其中key为属性的名字，value为属性的禁用值
     * @return 核查结果 true：核查成功；false：核查失败
     */
    public boolean check(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldJudge>> whiteSet, Map<String, Map<String, FieldJudge>> blackSet) {
        return delegate.available(object, objectFieldMap, whiteSet, blackSet);
    }

    /**
     * 类型白名单核查 注意： 建议基本类型使用，自定义类型、集合和Map类型不建议使用
     * @param object 待核查对象
     * @param whiteSet 白名单集合
     * @param <T> 对象类型
     * @return 核查结果 true：核查成功；false：核查失败
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
     * @param object 待核查对象
     * @param blackSet 黑名单集合
     * @param <T> 对象类型
     * @return 核查结果 true：核查成功；false：核查失败
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
                FieldValidCheck includeCheck = f.getAnnotation(FieldValidCheck.class);
                if (null != includeCheck && !includeCheck.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addWhiteValueMap(whiteFieldValueMap, objectClsName, f, includeCheck);
                }

                FieldInvalidCheck excludeCheck = f.getAnnotation(FieldInvalidCheck.class);
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
        FieldValidCheck validValue) {
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
        FieldInvalidCheck invalidValue) {
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

//    @Getter
//    @Setter
//    @Accessors(chain = true)
//    static class FieldJudge {
//
//        /**
//         * 属性名字
//         */
//        private String name;
//        /**
//         * 指定的值判断，对应{@link FieldValidCheck#value()}或者{@link FieldInvalidCheck#value()}
//         */
//        private Set<Object> values;
//        /**
//         * 指定的类型判断，对应{@link FieldValidCheck#type()}或者{@link FieldInvalidCheck#type()}
//         */
//        private FieldType fieldType;
//        /**
//         * 正则表达式判断，对应{@link FieldValidCheck#regex()}或者{@link FieldInvalidCheck#regex()}
//         */
//        private Pattern range;
//        /**
//         * 正则表达式判断，对应{@link FieldValidCheck#enumType()}或者{@link FieldInvalidCheck#enumType()}
//         */
//        private Pattern pattern;
//        /**
//         * 正则表达式判断，对应{@link FieldValidCheck#range()}或者{@link FieldInvalidCheck#range()}
//         */
//        private Pattern pattern;
//        /**
//         * 正则表达式判断，对应{@link FieldValidCheck#condition()}或者{@link FieldInvalidCheck#condition()}
//         */
//        private Pattern pattern;
//        /**
//         * 系统自行判断，对应{@link FieldValidCheck#judge()}或者{@link FieldInvalidCheck#judge()}
//         */
//        private Predicate<Object> predicate;
//        /**
//         * 属性核查禁用标示，对应{@link FieldValidCheck#disable()}或者{@link FieldInvalidCheck#disable()}
//         */
//        private Boolean disable;
//
////        /**
////         * 判断是否符合以上的匹配
////         * @param object 待校验的数据
////         * @return true：匹配上，false：没有匹配上
////         */
////        public Boolean match(Object object){
////
////        }
////
////        /**
////         * 过滤条件是否为空
////         * @return true：条件为空，false：条件不空
////         */
////        public Boolean isEmpty(){
////
////        }
//
//        static FieldJudge buildFromValid(Field field, FieldValidCheck validCheck){
//            return this.setFieldType(buildFieldEnum(validCheck.type()))
//                .setPattern(buildPattern(validCheck.regex()))
//                .setPredicate(buildPredicate(field, validCheck.judge()))
//                .setDisable(validCheck.disable());
//        }
//
//        static FieldJudge buildFromInvalid(Field field, FieldInvalidCheck invalidCheck){
//            return this.setFieldType(buildFieldEnum(invalidCheck.type()))
//                .setPattern(buildPattern(invalidCheck.regex()))
//                .setPredicate(buildPredicate(field, invalidCheck.judge()))
//                .setDisable(invalidCheck.disable());
//        }
//
//        private static FieldType buildFieldEnum(FieldType fieldType){
//            if (fieldType.equals(FieldType.DEFAULT)){
//                return null;
//            }
//            return fieldType;
//        }
//
//        private static Pattern buildPattern(String regex){
//            if (null == regex || "".equals(regex)){
//                return null;
//            }
//            return Pattern.compile(regex);
//        }
//
//        /**
//         * 将一个类中的函数转换为一个过滤器
//         *
//         * @return com.xxx.ACls#isValid -> predicate
//         */
//        @SuppressWarnings("all")
//        private static Predicate<Object> buildPredicate(Field field, String judge){
//            if (null == judge || judge.isEmpty() || !judge.contains("#")){
//               return null;
//            }
//            Integer index = judge.indexOf("#");
//            String classStr = judge.substring(0, index);
//            String funStr = judge.substring(index + 1);
//
//            try {
//                Class<?> cls = Class.forName(classStr);
//                Method method = cls.getDeclaredMethod(funStr, field.getType());
//                Object object = cls.newInstance();
//                Class<?> returnType = method.getReturnType();
//
//                String booleanStr = "boolean";
//                if (returnType.getSimpleName().equals(Boolean.class.getSimpleName())
//                    || returnType.getSimpleName().equals(booleanStr)){
//                    return obj -> {
//                        try {
//                            method.setAccessible(true);
//                            return (boolean) method.invoke(object, obj);
//                        } catch (IllegalAccessException | InvocationTargetException e) {
//                            e.printStackTrace();
//                        }
//                        return false;
//                    };
//                }
//            } catch (ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e ) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
}
