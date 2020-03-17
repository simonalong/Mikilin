package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.*;
import com.simonalong.mikilin.exception.MkCheckException;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.ClassUtil;
import com.simonalong.mikilin.util.CollectionUtil;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author shizi
 * @since 2020/3/3 上午12:16
 */
@SuppressWarnings("all")
@UtilityClass
public final class MkValidators {

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
    /**
     * 核查上下文
     */
    private MkContext context;

    static {
        init();
    }

    private void init() {
        whiteGroupMap = new ConcurrentHashMap<>(2);
        blackGroupMap = new ConcurrentHashMap<>(2);
        objectFieldCheckMap = new ConcurrentHashMap<>(16);
        context = new MkContext();
        delegate = new CheckDelegate(context);
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，直接返回true
     *
     * @param object 待核查对象
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(Object object) {
        return check(MkConstant.DEFAULT_GROUP, object);
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * @param object   待核查对象
     * @param fieldSet 待核查对象的多个属性名字
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(Object object, String... fieldSet) {
        return check(MkConstant.DEFAULT_GROUP, object, fieldSet);
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，直接返回true
     *
     * @param group  分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param object 待核查对象
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(String group, Object object) {
        String groupDelete = (null == group || "".equals(group)) ? MkConstant.DEFAULT_GROUP : group;
        if (delegate.isEmpty(object)) {
            context.append("数据为空");
            return false;
        }

        // 待核查类型不核查，直接返回核查成功
        if (ClassUtil.isCheckedType(object.getClass())) {
            return true;
        } else {
            return check(groupDelete, object, ClassUtil.allFieldsOfClass(object.getClass()), getObjFieldMap(object), getWhiteMap(), getBlackMap());
        }
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * @param group    分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param object   待核查对象
     * @param fieldSet 待核查对象的多个属性名字
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(String group, Object object, String... fieldSet) {
        String groupDelete = (null == group || "".equals(group)) ? MkConstant.DEFAULT_GROUP : group;
        if (delegate.isEmpty(object)) {
            context.append("数据为空");
            return false;
        }

        // 待核查类型不核查，直接返回核查成功
        if (ClassUtil.isCheckedType(object.getClass())) {
            return true;
        } else {
            return check(groupDelete, object, getFieldToCheck(object, new HashSet<>(Arrays.asList(fieldSet))), getObjFieldMap(object), getWhiteMap(), getBlackMap());
        }
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，核查失败抛异常
     *
     * <p>
     *     同{@link MkValidators#check(Object)}，只是该方式会抛出异常
     *
     * @param object 待核查对象
     * @throws MkCheckException 核查失败异常
     * @since 1.4.4
     */
    public void validate(Object object) throws MkCheckException {
        if (!check(object)) {
            throw new MkCheckException(getErrMsg());
        }
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * <p>
     *     同{@link MkValidators#check(Object, String...)}，只是该方式会抛出异常
     *
     * @param object   待核查对象
     * @param fieldSet 待核查对象的多个属性名字
     * @throws MkCheckException 核查失败异常
     * @since 1.4.4
     */
    public void validate(Object object, String... fieldSet) throws MkCheckException {
        if (!check(object, fieldSet)) {
            throw new MkCheckException(getErrMsg());
        }
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，核查失败抛异常
     *
     * <p>
     *     同{@link MkValidators#check(String, Object)}，只是该方式会抛出异常
     *
     * @param group  分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param object 待核查对象
     * @throws MkCheckException 核查失败异常
     * @since 1.4.4
     */
    public void validate(String group, Object object) throws MkCheckException {
        if (!check(group, object)) {
            throw new MkCheckException(getErrMsg());
        }
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * <p>
     *     同{@link MkValidators#check(String, Object, String...)}，只是该方式会抛出异常
     *
     * @param group    分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param object   待核查对象
     * @param fieldSet 待核查对象的多个属性名字
     * @throws MkCheckException 核查失败异常
     * @since 1.4.4
     */
    public void validate(String group, Object object, String... fieldSet) throws MkCheckException {
        if (!check(group, object, fieldSet)) {
            throw new MkCheckException(getErrMsg());
        }
    }

    /**
     * 将要核查的属性转换为Field类型
     *
     * @param object      目标对象
     * @param fieldStrSet 调用方想要调用的属性的字符串名字集合
     * @return 属性的Field类型集合
     */
    private Set<Field> getFieldToCheck(Object object, Set<String> fieldStrSet) {
        return ClassUtil.allFieldsOfClass(object.getClass()).stream().filter(f -> fieldStrSet.contains(f.getName())).collect(Collectors.toSet());
    }

    /**
     * 用于索引列表和黑白名单列表核查
     *
     * @param group          分组
     * @param object         待核查的对象
     * @param fieldSet       待核查的属性
     * @param objectFieldMap 对象的属性映射表，key为类的canonicalName，value为当前类的属性的集合
     * @param whiteSet       属性的白名单映射表，key为类的canonicalName，value为map，其中key为属性的名字，value为属性的可用值
     * @param blackSet       属性的白名单映射表，key为类的canonicalName，value为map，其中key为属性的名字，value为属性的禁用值
     * @return 核查结果 true：核查成功；false：核查失败
     */
    private boolean check(String group, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatcherManager> whiteSet,
        Map<String, MatcherManager> blackSet) {
        delegate.setGroup(group);
        try {
            return delegate.available(object, fieldSet, objectFieldMap, whiteSet, blackSet);
        } finally {
            // 防止threadLocal对应的group没有释放
            delegate.clearGroup();
        }
    }

    /**
     * 返回错误信息链
     * <p>
     * 返回的结果是这种{@code xxxx没有匹配上 --> xxx的属性不符合需求 --> ...}
     *
     * @return 多个匹配错误的信息
     */
    public String getErrMsgChain() {
        return delegate.getErrMsgChain();
    }

    /**
     * 获取其中一个错误信息（即最后的一个错误信息）
     *
     * @return 错误信息
     */
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
        if (objectFieldCheckMap.containsKey(objectClsName)) {
            return;
        }

        Set<Field> fieldSet = ClassUtil.allFieldsOfClass(cls);
        if (!CollectionUtil.isEmpty(fieldSet)) {
            // 待核查类型用于获取注解的属性
            fieldSet.forEach(f -> {
                WhiteMatcher whiteMatcher = f.getAnnotation(WhiteMatcher.class);
                if (null != whiteMatcher && !whiteMatcher.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addWhiteValueMap(whiteGroupMap, objectClsName, f, whiteMatcher);
                }

                BlackMatcher blackMatcher = f.getAnnotation(BlackMatcher.class);
                if (null != blackMatcher && !blackMatcher.disable()) {
                    addObjectFieldMap(objectClsName, f.getName());
                    addBlackValueMap(blackGroupMap, objectClsName, f, blackMatcher);
                }

                WhiteMatchers whiteMatchers = f.getAnnotation(WhiteMatchers.class);
                if (null != whiteMatchers) {
                    Stream.of(whiteMatchers.value()).forEach(w -> {
                        addObjectFieldMap(objectClsName, f.getName());
                        addWhiteValueMap(whiteGroupMap, objectClsName, f, w);
                    });
                }

                BlackMatchers blackMatchers = f.getAnnotation(BlackMatchers.class);
                if (null != blackMatchers) {
                    Stream.of(blackMatchers.value()).forEach(w -> {
                        addObjectFieldMap(objectClsName, f.getName());
                        addBlackValueMap(blackGroupMap, objectClsName, f, w);
                    });
                }
            });

            // 非待核查类型拆分开进行迭代分析
            fieldSet.stream().filter(f -> !ClassUtil.isCheckedType(f.getType())).forEach(f -> {
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

    private void addWhiteValueMap(Map<String, MatcherManager> groupMather, String objectName, Field field, WhiteMatcher whiteMatcher) {
        Arrays.asList(whiteMatcher.group()).forEach(g -> groupMather.compute(g, (k, v) -> {
            if (null == v) {
                return new MatcherManager().addWhite(objectName, field, whiteMatcher, context);
            } else {
                v.addWhite(objectName, field, whiteMatcher, context);
                return v;
            }
        }));
    }

    private void addBlackValueMap(Map<String, MatcherManager> groupMather, String objectName, Field field, BlackMatcher blackMatcher) {
        Arrays.asList(blackMatcher.group()).forEach(g -> groupMather.compute(g, (k, v) -> {
            if (null == v) {
                return new MatcherManager().addBlack(objectName, field, blackMatcher, context);
            } else {
                v.addBlack(objectName, field, blackMatcher, context);
                return v;
            }
        }));
    }
}
