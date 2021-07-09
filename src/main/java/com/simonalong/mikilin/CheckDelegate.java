package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.match.FieldMatchManager;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.ClassUtil;
import com.simonalong.mikilin.util.CollectionUtil;
import com.simonalong.mikilin.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static com.simonalong.mikilin.MkConstant.PARENT_KEY;

/**
 * @author zhouzhenyong
 * @since 2018/12/24 下午10:31
 */
@Slf4j
final class CheckDelegate {

    private final ThreadLocal<String> localGroup;
    private final MkContext context;

    CheckDelegate(MkContext context) {
        localGroup = new ThreadLocal<>();
        this.context = context;
    }

    void setParameter(String group, Object parameter) {
        context.clearAll();
        context.setParameter(parameter);
        localGroup.set(group);
    }

    /**
     * 清理threadLocal保存的group
     */
    void clear() {
        localGroup.remove();
    }

    /**
     * 判断自定义结构的数据值是否是可用的，这里判断逻辑是通过黑名单和白名单
     *
     * @param parentField 属性对应的结构（如果结构为顶级则会null）
     * @param object 为集合、Map和自定义结构，其中待核查类型，为另外一个重载函数
     * @param fieldSet 待核查的属性的集合
     * @param objectFieldMap 自定义对象属性的核查影射，key为类的名字，value为类中对应的属性名字
     * @param whiteSet 对象属性集合的可用值列表
     * @param blackSet 对象属性集合的不可用值列表
     * @return false：如果对象中有某个属性不可用 true：所有属性都可用
     */
    boolean available(Field parentField, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        if (null == object) {
            return true;
        }

        Class<?> cls = object.getClass();
        if (ClassUtil.isCheckedType(cls)) {
            // 基本类型
            return true;
        } else if (Collection.class.isAssignableFrom(cls)) {
            // 集合类型
            return availableOfCollection(parentField, object, fieldSet, objectFieldMap, whiteSet, blackSet);
        } else if (Map.class.isAssignableFrom(cls)) {
            // map类型
            return availableOfMap(parentField, object, fieldSet, objectFieldMap, whiteSet, blackSet);
        } else if (cls.isArray()) {
            // 数组类型
            return availableOfArray(parentField, object, fieldSet, objectFieldMap, whiteSet, blackSet);
        } else {
            // 自定义类型
            return availableOfCustomize(parentField, object, fieldSet, objectFieldMap, whiteSet, blackSet);
        }
    }

    boolean available(Object object, Method method, Parameter parameter, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        boolean blackEmpty = fieldCheckIsEmpty(method, parameter, blackSet);
        boolean whiteEmpty = fieldCheckIsEmpty(method, parameter, whiteSet);

        // 黑白名单都有空，则不核查该参数，可用
        if (whiteEmpty && blackEmpty) {
            return true;
        }

        // 黑名单不空，而且匹配到了，则不可用
        if (!blackEmpty && fieldMatch(object, method, parameter, blackSet, false)) {
            context.append("参数 {0} 核查失败", parameter.getName());
            return false;
        }

        // 白名单不空，而且该属性没有匹配到，则不可用
        if (!whiteEmpty && !fieldMatch(object, method, parameter, whiteSet, true)) {
            context.append("参数 {0} 核查失败", parameter.getName());
            return false;
        }

        return true;



        boolean haveChangeTo = fieldChangeToIsEmpty(object, field, blackGroupMather);
        boolean blackEmpty = fieldCheckIsEmpty(object, field, blackGroupMather);
        boolean whiteEmpty = fieldCheckIsEmpty(object, field, whiteGroupMather);

        // 有转换配置且为白名单
        if (!haveChangeTo && whiteEmpty) {
            fieldMatchAndChangeTo(object, field, whiteGroupMather);
            return true;
        }

        // 黑白名单都有空，则不核查该参数，可用
        if (whiteEmpty && blackEmpty) {
            return true;
        }

        field.setAccessible(true);
        // 黑名单不空，而且匹配到了，则不可用
        if (!blackEmpty && fieldMatch(object, field, blackGroupMather, false)) {
            return false;
        }

        // 白名单不空，而且该属性没有匹配到，则不可用
        if (!whiteEmpty && !fieldMatch(object, field, whiteGroupMather, true)) {
            return false;
        }

        return true;
    }

    boolean doAvailable(Object object, Method method, Parameter parameter, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        context.beforeErrMsg();
        if (!available(object, method, parameter, whiteSet, blackSet)) {
            context.flush(PARENT_KEY);
            return false;
        }

        context.poll();
        return true;
    }

    boolean available(Method method, Object[] parameterValues, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        // 自定义类型，所有匹配成功才算成功，如果对象中任何一个属性不可用，则对象不可用，这里要核查自己的所有属性和继承的父类的public属性
        Parameter[] parameters = method.getParameters();
        context.beforeErrMsg();

        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Object parameterValue = parameterValues[index];

            boolean whiteEmpty = fieldCheckIsEmpty(method, parameter, whiteSet);
            boolean haveChangeTo = fieldChangeToIsEmpty(method, parameter, whiteSet);
            // 有转换配置且为白名单
            if (!haveChangeTo && whiteEmpty) {
                fieldMatchAndChangeTo(object, field, whiteGroupMather);
                return true;
            }

            if (!available(parameterValue, method, parameter, whiteSet, blackSet)) {
                context.append("函数 {0} 的参数 {1} 核查失败", method.getName(), parameter.getName());
                context.flush(PARENT_KEY);
                return false;
            }
        }

        method.invoke()
        context.poll();
        return true;
    }

    private boolean availableOfCollection(Field parentField, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        // 集合类型，则剥离集合，获取泛型的类型再进行判断
        Collection<?> collection = (Collection<?>) object;
        if (!CollectionUtil.isEmpty(collection)) {
            return collection.stream().allMatch(c -> available(parentField, c, fieldSet, objectFieldMap, whiteSet, blackSet));
        } else {
            // 为空则忽略
            return true;
        }
    }

    private boolean availableOfMap(Field parentField, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        // Map 结构中的数据的判断，目前只判断value中的值
        Map<?, ?> map = (Map<?, ?>) object;
        if (!CollectionUtil.isEmpty(map)) {
            // 检查所有不合法属性
            boolean allMatch = map.values().stream().filter(Objects::nonNull).allMatch(v -> available(parentField, v, fieldSet, objectFieldMap, whiteSet, blackSet));
            if (allMatch) {
                return true;
            }
            context.append("Map的value中有不合法");
            return false;
        } else {
            // 为空则忽略
            return true;
        }
    }

    private boolean availableOfArray(Field parentField, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        // 数组类型处理
        boolean available = true;
        int arrayLength = Array.getLength(object);
        for (int index = 0; index < arrayLength; index++) {
            if (!available(parentField, Array.get(object, index), fieldSet, objectFieldMap, whiteSet, blackSet)) {
                available = false;
                break;
            }
        }

        if (!available) {
            context.append("数组值有不合法");
            return false;
        }
        return true;
    }

    private boolean availableOfCustomize(Field parentField, Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        // 自定义类型的话，则需要核查当前属性是否需要核查，不需要核查则略过
        if (!objectNeedCheck(object, objectFieldMap)) {
            return true;
        }

        // 自定义类型，所有匹配成功才算成功，如果对象中任何一个属性不可用，则对象不可用，这里要核查自己的所有属性和继承的父类的public属性
        List<Field> fieldList = ClassUtil.allFieldsOfClass(object.getClass()).stream().filter(fieldSet::contains).collect(Collectors.toList());
        context.beforeErrMsg();

        for (Field field : fieldList) {
            if (!available(object, field, objectFieldMap, whiteSet, blackSet)) {
                context.append("类型 {0} 核查失败", object.getClass().getSimpleName());
                if (null != parentField) {
                    context.flush(parentField.getName());
                } else {
                    context.flush(PARENT_KEY);
                }
                return false;
            }
        }
        context.poll();
        return true;
    }

    /**
     * 根据属性的类型，判断属性的值是否在对应的值列表中
     *
     * @param object 对象
     * @param field 属性
     * @param whiteGroupMather 属性值可用值列表
     * @param blackGroupMather 属性值的不用值列表
     * @param objectFieldMap 对象核查的属性映射
     * @return true 可用， false 不可用
     */
    private boolean available(Object object, Field field, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteGroupMather, Map<String, MatchManager> blackGroupMather) {
        if (ClassUtil.isCheckedType(field.getType())) {
            // 待核查类型，则直接校验
            return primaryFieldAvailable(object, field, whiteGroupMather, blackGroupMather);
        } else {
            boolean result = true;
            // 不是待核查类型，先判断是否添加了黑白名单注解配置，否则按照复杂类型处理
            if (matcherContainField(object, field, whiteGroupMather, blackGroupMather)) {
                result = primaryFieldAvailable(object, field, whiteGroupMather, blackGroupMather);
                if (!result) {
                    return false;
                }
            }

            // 包含拆解注解，则要查看拆解后的处理
            if (field.isAnnotationPresent(Check.class)) {
                try {
                    field.setAccessible(true);
                    result = doAvailable(field, field.get(object), objectFieldMap, whiteGroupMather, blackGroupMather);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            if(!result) {
                context.append("类型 {0} 的属性 {1} 核查失败", object.getClass().getSimpleName(), field.getName());
            }
            return result;
        }
    }

    /**
     * 判断黑白名单是否都不包含该属性
     *
     * @param object 对象
     * @param field 属性
     * @param whiteGroupMather 属性值可用值列表
     * @param blackGroupMather 属性值的不用值列表
     * @return true：都不包含该属性，false：有包含该属性
     */
    private boolean matcherContainField(Object object, Field field, Map<String, MatchManager> whiteGroupMather,
        Map<String, MatchManager> blackGroupMather){
        boolean blackEmpty = fieldCheckIsEmpty(object, field, blackGroupMather);
        boolean whiteEmpty = fieldCheckIsEmpty(object, field, whiteGroupMather);
        // 黑白名单有任何一个不空，则可以进行匹配
        return !whiteEmpty || !blackEmpty;
    }

    private boolean doAvailable(Field parentField, Object object, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        if (null == object) {
            return true;
        }

        return available(parentField, object, ClassUtil.allFieldsOfClass(ClassUtil.peel(object)), objectFieldMap, whiteSet, blackSet);
    }

    /**
     * 判断对象的一个基本属性是否可用
     *
     * @param object 属性的对象
     * @param field 属性
     * @param whiteGroupMather 属性的可用值列表
     * @param blackGroupMather 属性的不可用值列表
     * @return true：可用，false：不可用
     */
    @SuppressWarnings("all")
    private boolean primaryFieldAvailable(Object object, Field field, Map<String, MatchManager> whiteGroupMather, Map<String, MatchManager> blackGroupMather) {
        boolean haveChangeTo = fieldChangeToIsEmpty(object, field, blackGroupMather);
        boolean blackEmpty = fieldCheckIsEmpty(object, field, blackGroupMather);
        boolean whiteEmpty = fieldCheckIsEmpty(object, field, whiteGroupMather);

        // 有转换配置且为白名单
        if (!haveChangeTo && whiteEmpty) {
            fieldMatchAndChangeTo(object, field, whiteGroupMather);
            return true;
        }

        // 黑白名单都有空，则不核查该参数，可用
        if (whiteEmpty && blackEmpty) {
            return true;
        }

        field.setAccessible(true);
        // 黑名单不空，而且匹配到了，则不可用
        if (!blackEmpty && fieldMatch(object, field, blackGroupMather, false)) {
            return false;
        }

        // 白名单不空，而且该属性没有匹配到，则不可用
        if (!whiteEmpty && !fieldMatch(object, field, whiteGroupMather, true)) {
            return false;
        }

        return true;
    }

    /**
     * 判断对象是否需要继续核查
     *
     * @param object 待判决对象
     * @param objectFieldMap 对象和属性的映射map
     * @return true 对象需要继续核查 false 对象不需要通过黑白名单核查
     */
    private boolean objectNeedCheck(Object object, Map<String, Set<String>> objectFieldMap) {
        if (!CollectionUtil.isEmpty(objectFieldMap)) {
            return objectFieldMap.containsKey(object.getClass().getCanonicalName());
        }
        return false;
    }

    /**
     * 对象的所有判断是否都为空
     *
     * @param object 对象
     * @param field 对象的属性
     * @param groupMather 可用或者不可用的分组匹配器
     * @return true:所有为空，false属性都有
     */
    private boolean fieldCheckIsEmpty(Object object, Field field, Map<String, MatchManager> groupMather) {
        if (checkAllMatcherDisable(object, field, groupMather)) {
            return true;
        }

        Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(localGroup.get()).getJudge(object.getClass().getCanonicalName());
        if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
            return fieldValueSetMap.get(field.getName()).stream().allMatch(FieldMatchManager::isEmpty);
        }
        return true;
    }

    private boolean fieldCheckIsEmpty(Method method, Parameter parameter, Map<String, MatchManager> groupMather) {
        if (checkAllMatcherDisable(method, parameter, groupMather)) {
            return true;
        }

        Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(localGroup.get()).getJudge(ObjectUtil.getMethodCanonicalName(method));
        if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
            return fieldValueSetMap.get(parameter.getName()).stream().allMatch(FieldMatchManager::isEmpty);
        }
        return true;
    }

    private boolean fieldChangeToIsEmpty(Object object, Field field, Map<String, MatchManager> groupMather) {
        if (checkAllMatcherDisable(object, field, groupMather)) {
            return true;
        }

        Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(localGroup.get()).getJudge(object.getClass().getCanonicalName());
        if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
            return fieldValueSetMap.get(field.getName()).stream().allMatch(FieldMatchManager::changeToValueIsEmpty);
        }
        return true;
    }

    private boolean fieldChangeToIsEmpty(Method method, Parameter parameter, Map<String, MatchManager> groupMather) {
        if (checkAllMatcherDisable(method, parameter, groupMather)) {
            return true;
        }

        Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(localGroup.get()).getJudge(ObjectUtil.getMethodCanonicalName(method));
        if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
            return fieldValueSetMap.get(parameter.getName()).stream().allMatch(FieldMatchManager::changeToValueIsEmpty);
        }
        return true;
    }


    /**
     * 返回当前属性的所有匹配器是否都禁用
     *
     * @param object 待核查对象
     * @param field 对象的属性
     * @param groupMather 组匹配器
     * @return true 都禁用，false 有可用的
     */
    private boolean checkAllMatcherDisable(Object object, Field field, Map<String, MatchManager> groupMather) {
        String group = localGroup.get();
        if (groupMather.containsKey(group)) {
            Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(group).getJudge(object.getClass().getCanonicalName());
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                List<FieldMatchManager> fieldMatchManagerList = fieldValueSetMap.get(field.getName());
                if (null != fieldMatchManagerList) {
                    for (FieldMatchManager fieldMatchManager : fieldMatchManagerList) {
                        // 有任何为false的，则认为可用
                        if (!fieldMatchManager.getDisable()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean checkAllMatcherDisable(Method method, Parameter parameter, Map<String, MatchManager> groupMather) {
        String group = localGroup.get();
        if (groupMather.containsKey(group)) {
            Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(group).getJudge(ObjectUtil.getMethodCanonicalName(method));
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                List<FieldMatchManager> fieldMatchManagerList = fieldValueSetMap.get(parameter.getName());
                if (null != fieldMatchManagerList) {
                    for (FieldMatchManager fieldMatchManager : fieldMatchManagerList) {
                        // 有任何为false的，则认为可用
                        if (!fieldMatchManager.getDisable()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 对象的某个属性进行匹配
     *
     * @param object 对象
     * @param field 对象的属性
     * @param groupMather 可用或者不可用数据
     * @param whiteOrBlack 黑白名单标示
     * @return true：有匹配器匹配上（匹配器内部任何一个匹配项匹配上就叫匹配上）则返回true，false：所有匹配器都没有匹配上（匹配器内部的所有匹配项都没有匹配上）则返回false
     */
    private boolean fieldMatch(Object object, Field field, Map<String, MatchManager> groupMather, Boolean whiteOrBlack) {
        if (checkAllMatcherDisable(object, field, groupMather)) {
            return false;
        }

        String group = localGroup.get();
        if (groupMather.containsKey(group)) {
            Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(group).getJudge(object.getClass().getCanonicalName());
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                List<FieldMatchManager> fieldMatchManagerList = fieldValueSetMap.get(field.getName());
                if (null != fieldMatchManagerList) {
                    for (FieldMatchManager fieldMatchManager : fieldMatchManagerList) {
                        if (fieldMatchManager.getDisable()) {
                            continue;
                        }
                        field.setAccessible(true);
                        Object data;
                        try {
                            data = field.get(object);
                            // 某个匹配器没有匹配上，则认为没有匹配上
                            if (!fieldMatchManager.match(object, data, context, whiteOrBlack)) {
                                return false;
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 对象的某个属性进行匹配
     *
     * @param object       对象
     * @param method       方法
     * @param parameter    参数
     * @param groupMather  可用或者不可用数据
     * @param whiteOrBlack 黑白名单标示
     * @return true：有匹配器匹配上（匹配器内部任何一个匹配项匹配上就叫匹配上）则返回true，false：所有匹配器都没有匹配上（匹配器内部的所有匹配项都没有匹配上）则返回false
     */
    private boolean fieldMatch(Object object, Method method, Parameter parameter, Map<String, MatchManager> groupMather, Boolean whiteOrBlack) {
        if (checkAllMatcherDisable(method, parameter, groupMather)) {
            return false;
        }

        String group = localGroup.get();
        if (groupMather.containsKey(group)) {
            Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(group).getJudge(ObjectUtil.getMethodCanonicalName(method));
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                List<FieldMatchManager> fieldMatchManagerList = fieldValueSetMap.get(parameter.getName());
                if (null != fieldMatchManagerList) {
                    for (FieldMatchManager fieldMatchManager : fieldMatchManagerList) {
                        if (fieldMatchManager.getDisable()) {
                            continue;
                        }
                        // 某个匹配器没有匹配上，则认为没有匹配上
                        if (!fieldMatchManager.match(object, context, whiteOrBlack)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 属性匹配并转换
     *
     * @param object           对象
     * @param field            属性
     * @param whitGroupMatcher 白名单组匹配器
     */
    private void fieldMatchAndChangeTo(Object object, Field field, Map<String, MatchManager> whitGroupMatcher) {
        if (checkAllMatcherDisable(object, field, whitGroupMatcher)) {
            return;
        }

        String group = localGroup.get();
        if (whitGroupMatcher.containsKey(group)) {
            Map<String, List<FieldMatchManager>> fieldValueSetMap = whitGroupMatcher.get(group).getJudge(object.getClass().getCanonicalName());
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                List<FieldMatchManager> fieldMatchManagerList = fieldValueSetMap.get(field.getName());
                if (null != fieldMatchManagerList) {
                    for (FieldMatchManager fieldMatchManager : fieldMatchManagerList) {
                        if (fieldMatchManager.getDisable()) {
                            continue;
                        }
                        field.setAccessible(true);
                        try {
                            // 有任何一个匹配器匹配上，则将该值进行转换
                            if (fieldMatchManager.match(object, context, true)) {
                                field.set(object, fieldMatchManager.getToChangeValue());
                                log.debug("field属性 {} 满足匹配条件，对应的值转换为{}", field.getName(), fieldMatchManager.getToChangeValue());
                            }
                        } catch (IllegalAccessException ignore) {}
                    }
                }
            }
        }
    }

    private void fieldMatchAndChangeTo(Object object, Method method, Parameter parameter, Map<String, MatchManager> whiteGroupMather) {
        if (checkAllMatcherDisable(method, parameter, whiteGroupMather)) {
            return;
        }

        String group = localGroup.get();
        if (whiteGroupMather.containsKey(group)) {
            Map<String, List<FieldMatchManager>> fieldValueSetMap = whiteGroupMather.get(group).getJudge(ObjectUtil.getMethodCanonicalName(method));
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                List<FieldMatchManager> fieldMatchManagerList = fieldValueSetMap.get(parameter.getName());
                if (null != fieldMatchManagerList) {
                    for (FieldMatchManager fieldMatchManager : fieldMatchManagerList) {
                        if (fieldMatchManager.getDisable()) {
                            continue;
                        }

                        try {
                            // 有任何一个匹配器匹配上，则将该值进行转换
                            if (fieldMatchManager.match(object, context, true)) {
                                field.set(object, fieldMatchManager.getToChangeValue());
                                log.debug("field属性 {} 满足匹配条件，对应的值转换为{}", field.getName(), fieldMatchManager.getToChangeValue());
                            }
                        } catch (IllegalAccessException ignore) {}
                    }
                }
            }
        }
        return;
    }

    String getErrMsgChain() {
        return context.getErrMsgChainLocal();
    }

    String getErrMsg () {
        return context.getLastErrMsg();
    }

    Map<String, Object> getErrMsgMap () {
        return context.getErrMsgMap();
    }
}
