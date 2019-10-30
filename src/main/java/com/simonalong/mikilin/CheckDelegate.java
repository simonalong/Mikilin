package com.simonalong.mikilin;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.match.FieldJudge;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.ClassUtil;
import com.simonalong.mikilin.util.CollectionUtil;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhouzhenyong
 * @since 2018/12/24 下午10:31
 */
@SuppressWarnings("unchecked")
final class CheckDelegate {

    private ThreadLocal<String> localGroup;
    private MkContext context;

    CheckDelegate(MkContext context) {
        localGroup = new ThreadLocal<>();
        this.context = context;
    }

    void setGroup(String group) {
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
    boolean available(Object object, Set<Field> fieldSet, Map<String, Set<String>> objectFieldMap,
        Map<String, MatcherManager> whiteSet, Map<String, MatcherManager> blackSet) {
        if (null == object) {
            // 对于对象中的其他属性不核查
            return true;
        }

        Class cls = object.getClass();
        if (ClassUtil.isCheckedType(cls)) {
            // 底层基本校验类型，则放过
            return true;
        } else if (Collection.class.isAssignableFrom(cls)) {
            // 集合类型，则剥离集合，获取泛型的类型再进行判断
            Collection collection = (Collection) object;
            if (!CollectionUtil.isEmpty(collection)) {
                return collection.stream().allMatch(c -> available(c, fieldSet, objectFieldMap, whiteSet, blackSet));
            } else {
                // 为空则忽略
                return true;
            }
        } else if (Map.class.isAssignableFrom(cls)) {
            // Map 结构中的数据的判断，目前只判断value中的值
            Map map = (Map) object;
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
        } else {
            // 自定义类型的话，则需要核查当前属性是否需要核查，不需要核查则略过
            if (!objectNeedCheck(object, objectFieldMap)) {
                return true;
            }

            // 自定义类型，所有匹配成功才算成功，如果对象中任何一个属性不可用，则对象不可用，这里要核查所有的属性
            long unAvailableCount = ClassUtil.allFieldsOfClass(object.getClass()).stream().filter(fieldSet::contains)
                .filter(f -> !available(object, f, objectFieldMap, whiteSet, blackSet)).count();
            if (0 == unAvailableCount) {
                return true;
            }

            context.append("类型 {0} 核查失败", object.getClass().getSimpleName());
            return false;
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
    private boolean available(Object object, Field field, Map<String, Set<String>> objectFieldMap,
        Map<String, MatcherManager> whiteGroupMather, Map<String, MatcherManager> blackGroupMather) {
        Class cls = field.getType();

        if (ClassUtil.isCheckedType(cls)) {
            // 待核查类型，则直接校验
            return primaryFieldAvailable(object, field, whiteGroupMather, blackGroupMather);
        } else {
            boolean result = true;
            // 不是待核查类型，先判断是否添加了黑白名单注解配置，否则按照复杂类型处理
            if (matcherContainField(object, field, whiteGroupMather, blackGroupMather)) {
                result = primaryFieldAvailable(object, field, whiteGroupMather, blackGroupMather);
            }

            // 包含拆解注解，则要查看拆解后的处理
            if (field.isAnnotationPresent(Check.class)) {
                try {
                    field.setAccessible(true);
                    result &= available(field.get(object), objectFieldMap, whiteGroupMather, blackGroupMather);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
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
    private boolean matcherContainField(Object object, Field field, Map<String, MatcherManager> whiteGroupMather,
        Map<String, MatcherManager> blackGroupMather){
        boolean blackEmpty = fieldCheckIsEmpty(object, field, blackGroupMather);
        boolean whiteEmpty = fieldCheckIsEmpty(object, field, whiteGroupMather);
        // 黑白名单有任何一个不空，则可以进行匹配
        return !whiteEmpty || !blackEmpty;
    }

    private boolean available(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, MatcherManager> whiteSet, Map<String, MatcherManager> blackSet) {
        if (null == object) {
            return true;
        }
        return available(object, ClassUtil.allFieldsOfClass(ClassUtil.peel(object)), objectFieldMap, whiteSet,
            blackSet);
    }

    /**
     * 判断对象的一个基本属性是否可用
     *
     * @param object 属性的对象
     * @param field 属性
     * @param whiteGroupMather 属性的可用值列表
     * @param blackGroupMather 属性的不可用值列表
     * @return 1.黑白名单都有空，则不核查该参数，放过 2.对象为空 1.只有（白名单不空且包含）则放过 2.其他都不放过 3.对象不空 1.如果（黑名单不空且包含）则不放过 2.如果（白名单不空且不包含）则不放过
     * 3.其他都放过
     */
    private boolean primaryFieldAvailable(Object object, Field field, Map<String, MatcherManager> whiteGroupMather,
        Map<String, MatcherManager> blackGroupMather) {
        boolean blackEmpty = fieldCheckIsEmpty(object, field, blackGroupMather);
        boolean whiteEmpty = fieldCheckIsEmpty(object, field, whiteGroupMather);
        // 1.黑白名单都有空，则不核查该参数，放过
        if (whiteEmpty && blackEmpty) {
            return true;
        }

        try {
            field.setAccessible(true);
            // 2.对象为空
            if (isEmpty(field.get(object))) {

                // 1.（黑名单不空且包含）则不放过
                if (!blackEmpty && fieldContain(object, field, blackGroupMather, false)) {
                    return false;
                }

                // 2.（白名单不空且不包含）则不放过
                if (!whiteEmpty && !fieldContain(object, field, whiteGroupMather, true)) {
                    return false;
                }
                return true;
            }
            // 3.对象不空
            else {
                // 1.如果（黑名单不空且包含）则不放过
                if (!blackEmpty && fieldContain(object, field, blackGroupMather, false)) {
                    return false;
                }

                // 2.如果（白名单不空且不包含）则不放过
                if (!whiteEmpty && !fieldContain(object, field, whiteGroupMather, true)) {
                    return false;
                }

                // 3.其他都放过
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
    private boolean fieldCheckIsEmpty(Object object, Field field, Map<String, MatcherManager> groupMather) {
        if (checkDisable(object, field, groupMather)) {
            return true;
        }

        Map<String, FieldJudge> fieldValueSetMap = groupMather.get(localGroup.get())
            .getJudge(object.getClass().getCanonicalName());
        if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
            return fieldValueSetMap.get(field.getName()).isEmpty();
        }
        return true;
    }

    /**
     * 返回当前匹配器是否可用
     *
     * @param object 待核查对象
     * @param field 对象的属性
     * @param groupMather 组匹配器
     * @return true 不可用，false 可用
     */
    private boolean checkDisable(Object object, Field field, Map<String, MatcherManager> groupMather) {
        String group = localGroup.get();
        if (groupMather.containsKey(group)) {
            Map<String, FieldJudge> fieldValueSetMap = groupMather.get(group)
                .getJudge(object.getClass().getCanonicalName());
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                FieldJudge fieldJudge = fieldValueSetMap.get(field.getName());
                if (null != fieldJudge) {
                    return fieldJudge.getDisable();
                }
            }
        }
        return true;
    }

    /**
     * 对象的某个属性可用或者不可用核查中是否包含
     *
     * @param object 对象
     * @param field 对象的属性
     * @param groupMather 可用或者不可用数据
     * @param whiteOrBlack true=white, false=black
     * @return true 包含，false 不包含
     */
    private boolean fieldContain(Object object, Field field, Map<String, MatcherManager> groupMather,
        Boolean whiteOrBlack) {
        if (checkDisable(object, field, groupMather)) {
            return false;
        }

        String group = localGroup.get();
        if (groupMather.containsKey(group)) {
            Map<String, FieldJudge> fieldValueSetMap = groupMather.get(group)
                .getJudge(object.getClass().getCanonicalName());
            if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
                FieldJudge fieldJudge = fieldValueSetMap.get(field.getName());
                if (null != fieldJudge) {
                    field.setAccessible(true);
                    Object data;
                    try {
                        data = field.get(object);
                        if (whiteOrBlack) {
                            return fieldJudge.judgeWhite(object, data, context);
                        } else {
                            return fieldJudge.judgeBlack(object, data, context);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    String getErrMsg() {
        return context.getErrMsg();
    }

    boolean isEmpty(Object object) {
        if (object instanceof String) {
            String str = (String) object;
            return "".equals(str) || "null".equals(str) || "undefined".equals(str);
        } else if (object instanceof Map) {
            Map map = (Map) object;
            return CollectionUtil.isEmpty(map);
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            return CollectionUtil.isEmpty(collection);
        } else {
            return object == null;
        }
    }
}
