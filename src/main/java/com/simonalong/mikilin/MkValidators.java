package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.*;
import com.simonalong.mikilin.exception.MkCheckException;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.ClassUtil;
import com.simonalong.mikilin.util.CollectionUtil;
import com.simonalong.mikilin.util.ObjectUtil;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.simonalong.mikilin.MkConstant.DEFAULT_GROUP;
import static com.simonalong.mikilin.MkConstant.PARENT_KEY;

/**
 * @author shizi
 * @since 2020/3/3 上午12:16
 */
@SuppressWarnings("all")
@UtilityClass
public final class MkValidators {

    /**
     * 对象属性值白名单：key为group名字，value为属性的匹配器
     */
    private Map<String, MatchManager> whiteGroupMap;
    /**
     * 对象属性值黑名单：key为group名字，value为属性的匹配器
     */
    private Map<String, MatchManager> blackGroupMap;
    /**
     * 对象属性核查映射：key为规范化的类名，value为属性名
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
        return check(DEFAULT_GROUP, object);
    }

    /**
     * 针对对象的某些属性进行核查
     *
     * @param object   待核查对象
     * @param fieldSet 待核查对象的多个属性名字
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(Object object, String... fieldSet) {
        return check(DEFAULT_GROUP, object, fieldSet);
    }


    /**
     * 针对对象参数进行核查
     *
     * @param method   待核查参数所在的函数
     * @param parameter 待核查参数
     * @param baseValue 待核查参数対应的值
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(Method method, Parameter parameter, Object[] parameterValues, Integer parameterIndex) {
        return check(MkConstant.DEFAULT_GROUP, method, parameter, parameterValues, parameterIndex);
    }

    /**
     * 针对对象参数进行核查
     *
     * @param method   待核查参数所在的函数
     * @param parameterValues 待核查参数对应的值
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(Method method, Object[] parameterValues) {
        return check(DEFAULT_GROUP, method, parameterValues);
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，直接返回true
     *
     * @param group  分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param object 待核查对象
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(String group, Object object) {
        String groupDelegate = (null == group || "".equals(group)) ? DEFAULT_GROUP : group;
        if (ObjectUtil.isEmpty(object)) {
            return true;
        }

        // 待核查类型不核查，直接返回核查成功
        if (ClassUtil.isCheckedType(object.getClass())) {
            return true;
        } else {
            return check(groupDelegate, object, ClassUtil.allFieldsOfClass(ClassUtil.peel(object)), getObjFieldMap(object), getWhiteMap(), getBlackMap());
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
        String groupDelegate = (null == group || "".equals(group)) ? DEFAULT_GROUP : group;
        if (ObjectUtil.isEmpty(object)) {
            return true;
        }

        // 待核查类型不核查，直接返回核查成功
        if (ClassUtil.isCheckedType(object.getClass())) {
            return true;
        } else {
            return check(groupDelegate, object, getFieldToCheck(ClassUtil.peel(object), new HashSet<>(Arrays.asList(fieldSet))), getObjFieldMap(object), getWhiteMap(), getBlackMap());
        }
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，直接返回true
     *
     * @param group     分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param method    待核查参数所在函数
     * @param parameter 待核查参数
     * @param baseValue 待核查参数对应的值
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(String group, Method method, Parameter parameter, Object[] parameterValues, Integer parameterIndex) {
        String groupDelegate = (null == group || "".equals(group)) ? MkConstant.DEFAULT_GROUP : group;
        generateObjFieldMap(method);
        return check(groupDelegate, method, parameter, parameterValues, parameterIndex, getWhiteMap(), getBlackMap());
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，直接返回true
     *
     * @param group     分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param method    待核查参数所在函数
     * @param parameterValues 待核查参数对应的值
     * @return true：核查成功不拦截，false：核查失败进行拦截
     */
    public boolean check(String group, Method method, Object[] parameterValues) {
        String groupDelegate = (null == group || "".equals(group)) ? DEFAULT_GROUP : group;
        generateObjFieldMap(method);
        return check(groupDelegate, method, parameterValues, getWhiteMap(), getBlackMap());
    }

    /**
     * 自定义的复杂类型校验，待核查类型校验不校验，核查失败抛异常
     *
     * <p>
     *     同{@link MkValidators#check(Object)}，只是该方式会抛出异常
     *
     * @param object 待核查对象
     * @throws MkCheckException 核查失败异常
     * @since 1.2.2
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
     * @since 1.2.2
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
     * @since 1.2.2
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
     * @since 1.2.2
     */
    public void validate(String group, Object object, String... fieldSet) throws MkCheckException {
        if (!check(group, object, fieldSet)) {
            throw new MkCheckException(getErrMsg());
        }
    }

    public void validate(Method method, Parameter parameter, Object[] parameterValues, Integer parameterIndex) {
        if (!check(method, parameter, parameterValues, parameterIndex)) {
            throw new MkCheckException(getErrMsg());
        }
    }

    /**
     * 核查参数为基本类型对应的值
     *
     * <p>
     *     同{@link MkValidators#check(String, Method, Parameter, Object)}，只是该方式会抛出异常
     *
     * @param group  分组，为空则采用默认，为"_default_"，详{@link MkConstant#DEFAULT_GROUP}
     * @param method 待核查参数所在的函数
     * @param parameter 待核查基本对象的参数类型
     * @param baseValue 待核查基本对象对应的值
     * @throws MkCheckException 核查失败异常
     * @since 1.2.3
     */
    public void validate(String group, Method method, Parameter parameter, Object[] parameterValues, Integer index) {
        if (!check(group, method, parameter, parameterValues, index)) {
            throw new MkCheckException(getErrMsg());
        }
    }

    /**
     * 将要核查的属性转换为Field类型
     *
     * @param tClass      目标类型
     * @param fieldStrSet 调用方想要调用的属性的字符串名字集合
     * @return 属性的Field类型集合
     */
    private Set<Field> getFieldToCheck(Class tClass, Set<String> fieldStrSet) {
        return ClassUtil.allFieldsOfClass(tClass).stream().filter(f -> fieldStrSet.contains(f.getName())).collect(Collectors.toSet());
    }

    /**
     * 核查对象的对应属性
     *
     * @param group          分组
     * @param object         待核查的对象
     * @param fieldSet       待核查的属性
     * @param objectFieldMap 对象的属性映射表，key为类的canonicalName，value为当前类的属性的集合
     * @param whiteSet 参数的白名单映射表，key为group，value为白名单匹配管理器
     * @param blackSet 参数的黑名单映射表，key为group，value为黑名单匹配管理器
     * @return 核查结果 true：核查成功；false：核查失败
     */
    private boolean check(String group, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        delegate.setParameter(group, object);
        try {
            return delegate.available(null, object, fieldSet, objectFieldMap, whiteSet, blackSet);
        } finally {
            delegate.clear();
        }
    }

    private boolean check(String group, Method method, Parameter parameter, Object[] parameterValues, Integer parameterIndex, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        delegate.setParameter(group, parameterValues[parameterIndex]);
        try {
            context.beforeErrMsg();
            return delegate.doAvailable(method, parameter, parameterValues, parameterIndex, whiteSet, blackSet);
        } finally {
            // 防止threadLocal对应的group没有释放
            delegate.clear();
        }
    }

    /**
     * 核查函数的所有参数
     *
     * @param group           分组
     * @param method          待核查的函数
     * @param parameterValues 参数的值
     * @param whiteSet        参数的白名单映射表，key为group，value为白名单匹配管理器
     * @param blackSet        参数的黑名单映射表，key为group，value为黑名单匹配管理器
     * @return 核查结果 true：核查成功；false：核查失败
     */
    private boolean check(String group, Method method, Object[] parameterValues, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        delegate.setParameter(group, parameterValues);
        try {
            return delegate.available(method, parameterValues, whiteSet, blackSet);
        } finally {
            delegate.clear();
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

    public Map<String, Object> getErrMsgMap() {
        return delegate.getErrMsgMap();
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
        createObjectFieldMap(object);

        return objectFieldCheckMap;
    }

    private void generateObjFieldMap(Method method) {
        // 若对象已经创建属性索引树，则直接返回
        if (objectFieldCheckMap.containsKey(ObjectUtil.getMethodCanonicalName(method))) {
            return;
        }

        // 若当前对象没有对象属性索引树，则进行创建
        createObjectFieldMapForParameter(method);
    }

    private Map<String, MatchManager> getWhiteMap() {
        return whiteGroupMap;
    }

    private Map<String, MatchManager> getBlackMap() {
        return blackGroupMap;
    }

    /**
     * 根据对象的类型进行建立对象和属性映射树
     *
     * @param objectOrigin 待处理的对象
     */
    private void createObjectFieldMap(Object objectOrigin) {
        if (null == objectOrigin) {
            return;
        }
        Map.Entry<Object, Class<?>> objectAndClass = ObjectUtil.parseObject(objectOrigin);
        if (null == objectAndClass) {
            return;
        }
        Object object = objectAndClass.getKey();
        Class<?> cls = objectAndClass.getValue();
        String clsCanonicalName = cls.getCanonicalName();
        // 若已经解析，则不再解析
        if (objectFieldCheckMap.containsKey(clsCanonicalName)) {
            return;
        }

        Set<Field> fieldSet = ClassUtil.allFieldsOfClass(cls);
        if (!CollectionUtil.isEmpty(fieldSet)) {
            // 待核查类型用于获取注解的属性
            fieldSet.forEach(f -> {
                List<Matcher> matcherList = Arrays.asList(f.getAnnotationsByType(Matcher.class));
                for(Matcher matcher: matcherList) {
                    if (null != matcher && !matcher.disable()) {
                        addObjectFieldMap(clsCanonicalName, f.getName());
                        if (matcher.accept()) {
                            addValueMap(whiteGroupMap, clsCanonicalName, f, matcher);
                        } else {
                            addValueMap(blackGroupMap, clsCanonicalName, f, matcher);
                        }
                    }
                }

                Matchers matchers = f.getAnnotation(Matchers.class);
                if (null != matchers) {
                    Stream.of(matchers.value()).forEach(w -> {
                        addObjectFieldMap(clsCanonicalName, f.getName());
                        if (w.accept()) {
                            addValueMap(whiteGroupMap, clsCanonicalName, f, w);
                        } else {
                            addValueMap(blackGroupMap, clsCanonicalName, f, w);
                        }
                    });
                }
            });

            // 非待核查类型拆分开进行迭代分析
            fieldSet.stream().filter(f -> !ClassUtil.isCheckedType(f.getType())).forEach(f -> {
                // 该属性对应的类型是否添加了注解 Check
                if (f.isAnnotationPresent(Check.class)) {
                    addObjectFieldMap(clsCanonicalName, f.getName());
                    Object fieldData = null;
                    try {
                        f.setAccessible(true);
                        fieldData = f.get(object);
                        createObjectFieldMap(fieldData);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    private void createObjectFieldMapForParameter(Method method) {
        String clsCanonicalName = ObjectUtil.getMethodCanonicalName(method);
        // 若已经解析，则不再解析
        if (objectFieldCheckMap.containsKey(clsCanonicalName)) {
            return;
        }

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            List<Matcher> matcherList = Arrays.asList(parameter.getAnnotationsByType(Matcher.class));
            for(Matcher matcher: matcherList) {
                if (null != matcher && !matcher.disable()) {
                    addObjectFieldMap(clsCanonicalName, parameter.getName());
                    if (matcher.accept()) {
                        addValueMap(whiteGroupMap, clsCanonicalName, parameter, matcher);
                    } else {
                        addValueMap(blackGroupMap, clsCanonicalName, parameter, matcher);
                    }
                }
            }

            Matchers matchers = parameter.getAnnotation(Matchers.class);
            if (null != matchers) {
                Stream.of(matchers.value()).forEach(w -> {
                    addObjectFieldMap(clsCanonicalName, parameter.getName());
                    if (w.accept()) {
                        addValueMap(whiteGroupMap, clsCanonicalName, parameter, w);
                    } else {
                        addValueMap(blackGroupMap, clsCanonicalName, parameter, w);
                    }
                });
            }
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

    private void addValueMap(Map<String, MatchManager> groupMather, String clsCanonicalName, Field field, Matcher matcher) {
        Arrays.asList(matcher.group()).forEach(g -> groupMather.compute(g, (k, v) -> {
            if (null == v) {
                return new MatchManager().addMatch(clsCanonicalName, field, matcher, context);
            } else {
                v.addMatch(clsCanonicalName, field, matcher, context);
                return v;
            }
        }));
    }

    private void addValueMap(Map<String, MatchManager> groupMather, String clsCanonicalName, Parameter parameter, Matcher matcher) {
        Arrays.asList(matcher.group()).forEach(g -> groupMather.compute(g, (k, v) -> {
            if (null == v) {
                return new MatchManager().addMatch(clsCanonicalName, parameter, matcher, context);
            } else {
                v.addMatch(clsCanonicalName, parameter, matcher, context);
                return v;
            }
        }));
    }
}
