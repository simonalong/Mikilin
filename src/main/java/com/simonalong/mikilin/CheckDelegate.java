package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.match.FieldMatchManager;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.ClassUtil;
import com.simonalong.mikilin.util.CollectionUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhouzhenyong
 * @since 2018/12/24 下午10:31
 */
final class CheckDelegate {

    private ThreadLocal<String> localGroup;
    private MkContext context;

    CheckDelegate(MkContext context) {
        localGroup = new ThreadLocal<>();
        this.context = context;
    }

    void setGroup(String group) {
        context.clear();
        localGroup.set(group);
    }

    /**
     * 清理threadLocal保存的group
     */
    void clearGroup() {
        localGroup.remove();
    }

    /**
     * 判断自定义结构的数据值是否是可用的，这里判断逻辑是通过黑名单和白名单
     *
     * @param object 为集合、Map和自定义结构，其中待核查类型，为另外一个重载函数
     * @param fieldSet 待核查的属性的集合
     * @param objectFieldMap 自定义对象属性的核查影射，key为类的名字，value为类中对应的属性名字
     * @param whiteSet 对象属性集合的可用值列表
     * @param blackSet 对象属性集合的不可用值列表
     * @return false：如果对象中有某个属性不可用 true：所有属性都可用
     */
    boolean available(Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap, Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        if (null == object) {
            // 对于对象中的其他属性不核查
            return true;
        }

        Class<?> cls = object.getClass();
        if (ClassUtil.isCheckedType(cls)) {
            // 底层基本校验类型，则放过
            return true;
        } else if (Collection.class.isAssignableFrom(cls)) {
            // 集合类型，则剥离集合，获取泛型的类型再进行判断
            Collection<?> collection = (Collection<?>) object;
            if (!CollectionUtil.isEmpty(collection)) {
                return collection.stream().allMatch(c -> available(c, fieldSet, objectFieldMap, whiteSet, blackSet));
            } else {
                // 为空则忽略
                return true;
            }
        } else if (Map.class.isAssignableFrom(cls)) {
            // Map 结构中的数据的判断，目前只判断value中的值
            Map<?, ?> map = (Map<?, ?>) object;
            if (!CollectionUtil.isEmpty(map)) {
                // 检查所有不合法属性
                boolean allMatch = map.values().stream().filter(Objects::nonNull).allMatch(v -> available(v, fieldSet, objectFieldMap, whiteSet, blackSet));
                if (allMatch) {
                    return true;
                }
                context.append("Map的value中有不合法");
                return false;
            } else {
                // 为空则忽略
                return true;
            }
        } else if (cls.isArray()) {
            // 数组类型处理
            boolean available = true;
            int arrayLength = Array.getLength(object);
            for (int index = 0; index < arrayLength; index++) {
                if (!available(Array.get(object, index), fieldSet, objectFieldMap, whiteSet, blackSet)) {
                    available = false;
                    break;
                }
            }

            if (!available) {
                context.append("数组值有不合法");
                return false;
            }
            return true;
        } else {
            // 自定义类型的话，则需要核查当前属性是否需要核查，不需要核查则略过
            if (!objectNeedCheck(object, objectFieldMap)) {
                return true;
            }

            // 自定义类型，所有匹配成功才算成功，如果对象中任何一个属性不可用，则对象不可用，这里要核查自己的所有属性和继承的父类的public属性
            List<Field> fieldList = ClassUtil.allFieldsOfClass(object.getClass()).stream().filter(fieldSet::contains).collect(Collectors.toList());
            for (Field field : fieldList) {
                if (!available(object, field, objectFieldMap, whiteSet, blackSet)) {
                    context.append("类型 {0} 核查失败", object.getClass().getSimpleName());
                    return false;
                }
            }
            return true;
        }
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
        Class<?> cls = field.getType();

        if (ClassUtil.isCheckedType(cls)) {
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
                    result = available(field.get(object), objectFieldMap, whiteGroupMather, blackGroupMather);
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

    private boolean available(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, MatchManager> whiteSet, Map<String, MatchManager> blackSet) {
        if (null == object) {
            return true;
        }

        return available(object, ClassUtil.allFieldsOfClass(ClassUtil.peel(object)), objectFieldMap, whiteSet, blackSet);
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
    private boolean primaryFieldAvailable(Object object, Field field, Map<String, MatchManager> whiteGroupMather,
        Map<String, MatchManager> blackGroupMather) {
        boolean blackEmpty = fieldCheckIsEmpty(object, field, blackGroupMather);
        boolean whiteEmpty = fieldCheckIsEmpty(object, field, whiteGroupMather);
        // 1.黑白名单都有空，则不核查该参数，可用
        if (whiteEmpty && blackEmpty) {
            return true;
        }

        field.setAccessible(true);
        // 2.黑名单不空，而且匹配到了，则不可用
        if (!blackEmpty && fieldMatch(object, field, blackGroupMather, false)) {
            return false;
        }

        // 3.白名单不空，而且该属性没有匹配到，则不可用
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
            return fieldValueSetMap.get(field.getName()).isEmpty();
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
            Map<String, List<FieldMatchManager>> fieldValueSetMap = groupMather.get(group)
                .getJudge(object.getClass().getCanonicalName());
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

    /**
     * 对象的某个属性进行匹配
     *
     * @param object 对象
     * @param field 对象的属性
     * @param groupMather 可用或者不可用数据
     * @return true：所有匹配器匹配上（匹配器内部任何一个匹配项匹配上就叫匹配上）则返回true，false：有匹配器都没有匹配上（有一个匹配器内部的匹配项都没有匹配上）则返回false
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

    String getErrMsgChain() {
        return context.getErrMsgChain();
    }

    String getErrMsg () {
        return context.getLastErrMsg();
    }

    boolean isEmpty(Object object) {
        if (object instanceof String) {
            String str = (String) object;
            return "".equals(str) || "null".equals(str) || "undefined".equals(str);
        } else if (object instanceof Map) {
            Map<?,?> map = (Map<?,?>) object;
            return CollectionUtil.isEmpty(map);
        } else if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            return CollectionUtil.isEmpty(collection);
        } else {
            return object == null;
        }
    }
}
