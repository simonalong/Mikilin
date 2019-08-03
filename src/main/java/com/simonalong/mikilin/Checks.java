package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldBlackMatchers;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatchers;
import com.simonalong.mikilin.util.ClassUtil;
import com.simonalong.mikilin.util.CollectionUtil;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    private Map<String, MatcherManager> whiteGroupMap;
    /**
     * 对象属性值黑名单
     */
    private Map<String, MatcherManager> blackGroupMap;
    /**
     * 对象属性核查映射
     */
    private Map<String, Set<String>> objectFieldCheckMap;
    private CheckDelegate delegate;

    static {
        init();
    }

    private void init() {
        whiteGroupMap = new ConcurrentHashMap<>(2);
        blackGroupMap = new ConcurrentHashMap<>(2);
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
        return check(MkConstant.DEFAULT_GROUP, object);
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * @param object 待核查对象
     * @param fieldSet 待核查对象的多个属性名字
     * @return true：成功，false：核查失败
     */
    public boolean check(Object object, String... fieldSet){
        return check(MkConstant.DEFAULT_GROUP, object, fieldSet);
    }

    /**
     * 自定义的复杂类型校验，基本类型校验不校验，直接返回true
     *
     * @param group 分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param object 待核查对象
     * @return true：成功，false：核查失败
     */
    public boolean check(String group, Object object) {
        String groupDelete = (null == group || "".equals(group)) ? MkConstant.DEFAULT_GROUP : group;
        if (delegate.isEmpty(object)) {
            delegate.append("数据为空");
            return false;
        }

        // 基本类型不核查，直接返回核查成功
        if (ClassUtil.isBaseField(object.getClass())) {
            return true;
        } else {
            return check(groupDelete, object, ClassUtil.allFieldsOfClass(object.getClass()), getObjFieldMap(object),
                getWhiteMap(), getBlackMap());
        }
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * @param group 分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param object 待核查对象
     * @param fieldSet 待核查对象的多个属性名字
     * @return true：成功，false：核查失败
     */
    public boolean check(String group, Object object, String... fieldSet) {
        String groupDelete = (null == group || "".equals(group)) ? MkConstant.DEFAULT_GROUP : group;
        if (delegate.isEmpty(object)) {
            delegate.append("数据为空");
            return false;
        }

        // 基本类型不核查，直接返回核查成功
        if (ClassUtil.isBaseField(object.getClass())) {
            return true;
        } else {
            return check(groupDelete, object, getFieldToCheck(object, new HashSet<>(Arrays.asList(fieldSet))),
                getObjFieldMap(object), getWhiteMap(), getBlackMap());
        }
    }

    /**
     * 将要核查的属性转换为Field类型
     *
     * @param object 目标对象
     * @param fieldStrSet 调用方想要调用的属性的字符串名字集合
     * @return 属性的Field类型集合
     */
    private Set<Field> getFieldToCheck(Object object, Set<String> fieldStrSet){
        return ClassUtil.allFieldsOfClass(object.getClass()).stream().filter(f -> fieldStrSet.contains(f.getName()))
            .collect(Collectors.toSet());
    }

    /**
     * 用于索引列表和黑白名单列表核查
     *
     * @param group 分组
     * @param object 待核查的对象
     * @param fieldSet 待核查的属性
     * @param objectFieldMap 对象的属性映射表，key为类的canonicalName，value为当前类的属性的集合
     * @param whiteSet 属性的白名单映射表，key为类的canonicalName，value为map，其中key为属性的名字，value为属性的可用值
     * @param blackSet 属性的白名单映射表，key为类的canonicalName，value为map，其中key为属性的名字，value为属性的禁用值
     * @return 核查结果 true：核查成功；false：核查失败
     */
    private boolean check(String group, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap,
        Map<String,MatcherManager> whiteSet, Map<String, MatcherManager> blackSet) {
        delegate.setGroup(group);
        try {
            return delegate.available(object, fieldSet, objectFieldMap, whiteSet, blackSet);
        }finally {
            // 防止threadLocal对应的group没有释放
            delegate.clearGroup();
        }
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

    private Map<String, MatcherManager> getWhiteMap() {
        return whiteGroupMap;
    }

    private Map<String, MatcherManager> getBlackMap() {
        return blackGroupMap;
    }

    /**
     * 根据对象的类型进行建立对象和属性映射树
     *
     * @param cls 待处理的对象的类
     */
    private void createObjectFieldMap(Class<?> cls) {
        if (null == cls) {
            return;
        }
        // 剥离外部的一些壳之后类的类型
        String objectClsName = cls.getCanonicalName();
        // 若已经解析，则不再解析
        if (objectFieldCheckMap.containsKey(objectClsName)){
            return;
        }

        Set<Field> fieldSet = ClassUtil.allFieldsOfClass(cls);
        if (!CollectionUtil.isEmpty(fieldSet)) {
            // 基本类型用于获取注解的属性
            fieldSet.forEach(f -> {
                FieldWhiteMatcher whiteMatcher = f.getAnnotation(FieldWhiteMatcher.class);
                if (null != whiteMatcher && !whiteMatcher.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addWhiteValueMap(whiteGroupMap, objectClsName, f, whiteMatcher);
                }

                FieldBlackMatcher blackMatcher = f.getAnnotation(FieldBlackMatcher.class);
                if (null != blackMatcher && !blackMatcher.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addBlackValueMap(blackGroupMap, objectClsName, f, blackMatcher);
                }

                FieldWhiteMatchers whiteMatchers = f.getAnnotation(FieldWhiteMatchers.class);
                if (null != whiteMatchers) {
                    Stream.of(whiteMatchers.value()).forEach(w-> {
                        addObjectFieldMap(objectClsName, f.getName());
                        addWhiteValueMap(whiteGroupMap, objectClsName, f, w);
                    });
                }

                FieldBlackMatchers blackMatchers = f.getAnnotation(FieldBlackMatchers.class);
                if (null != blackMatchers) {
                    Stream.of(blackMatchers.value()).forEach(w-> {
                        addObjectFieldMap(objectClsName, f.getName());
                        addBlackValueMap(blackGroupMap, objectClsName, f, w);
                    });
                }
            });

            // 非基本类型拆分开进行迭代分析
            fieldSet.stream().filter(f -> !ClassUtil.isBaseField(f.getType())).forEach(f -> {
                // 该属性对应的类型是否添加了注解 Check
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

    private void addWhiteValueMap(Map<String, MatcherManager> groupMather, String objectName, Field field,
        FieldWhiteMatcher fieldWhiteMatcher) {
        Arrays.asList(fieldWhiteMatcher.group()).forEach(g-> groupMather.compute(g, (k, v) -> {
            if (null == v) {
                return new MatcherManager().addWhite(objectName, field, fieldWhiteMatcher);
            } else {
                v.addWhite(objectName, field, fieldWhiteMatcher);
                return v;
            }
        }));
    }

    private void addBlackValueMap(Map<String, MatcherManager> groupMather, String objectName, Field field,
        FieldBlackMatcher fieldBlackMatcher) {
        Arrays.asList(fieldBlackMatcher.group()).forEach(g-> groupMather.compute(g, (k, v) -> {
            if (null == v) {
                return new MatcherManager().addBlack(objectName, field, fieldBlackMatcher);
            } else {
                v.addBlack(objectName, field, fieldBlackMatcher);
                return v;
            }
        }));
    }
}
