package com.simon.mikilin.core;

import com.alibaba.fastjson.JSON;
import com.simon.mikilin.core.match.FieldJudge;
import com.simon.mikilin.core.util.ClassUtil;
import com.simon.mikilin.core.util.CollectionUtil;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhouzhenyong
 * @since 2018/12/24 下午10:31
 */
@SuppressWarnings("unchecked")
public final class CheckDelegate {

    private ThreadLocal<StringBuilder> errMsg;

    CheckDelegate(){
        errMsg = new ThreadLocal<>();
        initErrMsg();
    }

    /**
     * 对象可用性校验
     * @param object 待校验对象（基本对象或者复杂自定义对象）
     */
    boolean available(Object object){
        initErrMsg();
        if(isEmpty(object)){
            append("数据为空");
            return false;
        }

        if(ClassUtil.isBaseField(object.getClass())){
            return available(object, Collections.emptySet(), Collections.emptySet());
        }else{
            return available(object, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }
    }

    /**
     * 对象可用性校验
     * @param object 待校验对象（复杂自定义对象）
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     */
    boolean available(Object object, Map<String, Set<String>> objectFieldMap){
        initErrMsg();
        if(isEmpty(object)){
            append("数据为空");
            return false;
        }

        if(ClassUtil.isBaseField(object.getClass())){
            return available(object, Collections.emptySet(), Collections.emptySet());
        }else{
            return available(object, objectFieldMap, Collections.emptyMap(), Collections.emptyMap());
        }
    }

    /**
     * 对象可用性校验
     * @param object 待校验对象（复杂自定义对象）
     * @param whiteSet 对象的属性的可用值列表
     * @param blackSet 对象的属性的禁用值列表
     */
    boolean available(Object object, Map<String, Map<String, FieldJudge>> whiteSet,
        Map<String, Map<String, FieldJudge>> blackSet){
        // 黑白名单都有的话，则按照白名单
        return available(object, generateObjectFieldMap(whiteSet, blackSet), whiteSet, blackSet);
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象（基本对象）
     * @param valueSet 对象的属性的禁用值列表
     */
    boolean availableBlack(Object object, Set<Object> valueSet){
        return available(object, Collections.emptySet(), valueSet);
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象（复杂对象）
     * @param blackSet 对象的属性的禁用值列表
     */
    boolean availableBlack(Object object, Map<String, Map<String, FieldJudge>> blackSet){
        return available(object, generateObjectFieldMap(blackSet), Collections.emptyMap(), blackSet);
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     * @param blackSet 对象的属性的禁用值列表
     */
    boolean availableBlack(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldJudge>> blackSet){
        return available(object, objectFieldMap, Collections.emptyMap(), blackSet);
    }

    /**
     * 对象值是否在可用列表中
     * @param object 待校验对象（基本对象）
     * @param valueSet 对象的属性的可用值列表
     */
    <T> boolean availableWhite(T object, Set<T> valueSet){
        return available(object, valueSet, Collections.emptySet());
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象（复杂对象）
     * @param whiteSet 对象的属性的可用值列表
     */
    boolean availableWhite(Object object, Map<String, Map<String, FieldJudge>> whiteSet){
        return available(object, generateObjectFieldMap(whiteSet), whiteSet, Collections.emptyMap());
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     * @param whiteSet 对象的属性的可用值列表
     */
    boolean availableWhite(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldJudge>> whiteSet){
        return available(object, objectFieldMap, whiteSet, Collections.emptyMap());
    }

    /**
     * 判断基本类型的数据值是否是可用的，这里判断逻辑是通过黑名单和白名单
     * @param object 基本类型的数据对象，如果是复杂对象，则直接放过
     * @param whiteSet 可用值列表
     * @param blackSet 不可用值列表
     */
    <T> boolean available(T object, Set<T> whiteSet, Set<T> blackSet){
        initErrMsg();
        if(null == object){
            // 1.（黑名单不空且包含）则不放过
            if (null != blackSet && !blackSet.isEmpty() && blackSet.contains(null)){
                append("对象为null，命中黑名单");
                return false;
            }

            // 2.（白名单不空且不包含）则不放过
            if (null != whiteSet && !whiteSet.isEmpty() && !whiteSet.contains(null)){
                append("对象为null，不在白名单");
                return false;
            }
            return true;
        }

        Class cls = object.getClass();
        if(ClassUtil.isBaseField(cls)){
            // 底层基本校验类型，则放过
            return baseAvailable(object, whiteSet, blackSet);
        } else if(Collection.class.isAssignableFrom(cls)){
            // 集合类型，则剥离集合，获取泛型的类型再进行判断
            Collection<T> collection = Collection.class.cast(object);
            if (!CollectionUtil.isEmpty(collection)){
                return collection.stream().allMatch(c-> available(c, whiteSet, blackSet));
            }else{
                // 集合空类型，认为不可用
                append("集合类型为空");
                return false;
            }
        } else if(Map.class.isAssignableFrom(cls)) {
            // Map 结构中的数据的判断，剥离key和value，只判断value的值
            Map<String, T> map = Map.class.cast(object);
            if (!CollectionUtil.isEmpty(map)) {
                return map.values().stream().filter(Objects::nonNull).allMatch(v -> available(v, whiteSet, blackSet));
            } else {
                append("Map类型为空");
                return false;
            }
        } else {
            // 自定义类型，这里不拦截自定义类型
            // 1.（黑名单不空且包含）则不放过
            if (null != blackSet && !blackSet.isEmpty() && blackSet.contains(object)){
                append("对象为[{0}]，命中黑名单", object);
                return false;
            }

            // 2.（白名单不空且不包含）则不放过
            if (null != whiteSet && !whiteSet.isEmpty() && !whiteSet.contains(object)){
                append("对象为[{0}]，不在白名单", object);
                return false;
            }
            return true;
        }
    }

    /**
     * 判断基本类型的数据值是否是可用的，这里判断逻辑是通过黑名单和白名单
     * @param object 基本类型的数据对象，如果是复杂对象，则直接放过
     * @param whiteSet 可用值列表
     * @param blackSet 不可用值列表
     * @return
     * 1.黑白名单都有空，则不核查该参数，放过
     * 2.对象为空
     *      1.只有（白名单不空且包含）则放过
     *      2.其他都不放过
     * 3.对象不空
     *      1.如果（黑名单不空且包含）则不放过
     *      2.如果（白名单不空且不包含）则不放过
     *      3.其他都放过
     */
    private <T> boolean baseAvailable(T object, Set<T> whiteSet, Set<T> blackSet){
        boolean whiteEmpty = CollectionUtil.isEmpty(whiteSet);
        boolean blackEmpty = CollectionUtil.isEmpty(blackSet);

        // 1.黑白名单都有空，则不核查该参数，放过
        if(whiteEmpty && blackEmpty){
            return true;
        }

        // 2.对象为空
        if(isEmpty(object)){
            // 1.（黑名单不空且包含）则不放过
            if (!blackEmpty && blackSet.contains(object)){
                append("对象为空，命中黑名单");
                return false;
            }

            // 2.（白名单不空且不包含）则不放过
            if (!whiteEmpty && !whiteSet.contains(object)){
                append("对象为空，不在白名单");
                return false;
            }
            return true;
        }
        // 3.对象不空
        else{
            // 这里只校验底层基本类型，其他的类型放过：
            // 1.Java基本类型（除了void）
            // 2.String类型
            if(!ClassUtil.isBaseField(object.getClass())){
                return true;
            }

            // 1.如果（黑名单不空且包含）则不放过
            if (!blackEmpty && blackSet.contains(object)){
                append("对象值[{0}]位于黑名单{1}", JSON.toJSONString(object), blackSet);
                return false;
            }

            // 2.如果（白名单不空且不包含）则不放过
            if(!whiteEmpty && !whiteSet.contains(object)){
                append("对象值[{0}]不在白名单{1}中", JSON.toJSONString(object), whiteSet);
                return false;
            }

            // 3.其他都放过
            return true;
        }
    }

    /**
     * 判断自定义结构的数据值是否是可用的，这里判断逻辑是通过黑名单和白名单
     * @param object 为集合、Map和自定义结构，其中基本类型，为另外一个重载函数
     * @param objectFieldMap 自定义对象属性的核查影射，key为类的名字，value为类中对应的属性名字
     * @param whiteSet 对象属性集合的可用值列表
     * @param blackSet 对象属性集合的不可用值列表
     * @return
     * false：如果对象中有某个属性不可用
     * true：所有属性都可用
     */
    boolean available(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldJudge>> whiteSet, Map<String, Map<String, FieldJudge>> blackSet){
        initErrMsg();
        if (null == object) {
            // 对于对象中的其他属性不核查
            return true;
        }

        Class cls = object.getClass();
        if(ClassUtil.isBaseField(cls)){
            // 底层基本校验类型，则放过
            return true;
        } else if(Collection.class.isAssignableFrom(cls)){
            // 集合类型，则剥离集合，获取泛型的类型再进行判断
            Collection collection = Collection.class.cast(object);
            if (!CollectionUtil.isEmpty(collection)){
                return collection.stream().allMatch(c-> available(c, objectFieldMap, whiteSet, blackSet));
            }else{
                // 为空则忽略
                return true;
            }
        } else if(Map.class.isAssignableFrom(cls)) {
            // Map 结构中的数据的判断，目前只判断value中的值
            Map map = Map.class.cast(object);
            if (!CollectionUtil.isEmpty(map)) {
                if(map.values().stream().filter(Objects::nonNull).allMatch(v -> available(v, objectFieldMap, whiteSet, blackSet))){
                    return true;
                }
                append("Map的value中有不合法");
                return false;
            } else {
                // 为空则忽略
                return true;
            }
        } else {
            // 自定义类型的话，则需要核查当前属性是否需要核查，不需要核查则略过
            if(!objectNeedCheck(object, objectFieldMap)){
                return true;
            }

            // 自定义类型，如果对象中任何一个属性不可用，则对象不可用
            if(ClassUtil.allFieldsOfClass(object.getClass()).stream().allMatch(f-> available(object, f, objectFieldMap, whiteSet, blackSet))){
                return true;
            }

            append("类型[{0}]核查失败", object.getClass().getSimpleName());
            return false;
        }
    }

    /**
     * 根据属性的类型，判断属性的值是否在对应的值列表中
     * @param object 对象
     * @param field 属性
     * @param whiteSet 属性值可用值列表
     * @param blackSet 属性值的不用值列表
     * @param objectFieldMap 对象核查的属性映射
     */
    private boolean available(Object object, Field field,  Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldJudge>> whiteSet,Map<String, Map<String, FieldJudge>> blackSet) {
        Class cls = field.getType();
        if(ClassUtil.isBaseField(cls)){
            // 基本类型，则直接校验
            return primaryFieldAvailable(object, field, whiteSet, blackSet);
        } else {
            // 不是基本类型，则按照复杂类型处理
            try {
                field.setAccessible(true);
                if(available(field.get(object), objectFieldMap, whiteSet, blackSet)){
                    return true;
                }

                append("类型[{0}]的属性[{1}]核查失败", object.getClass().getSimpleName(), field.getName());
                return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 判断对象的一个基本属性是否可用
     * @param object 属性的对象
     * @param field 属性
     * @param whiteFieldValue 属性的可用值列表
     * @param blackFieldValue 属性的不可用值列表
     * @return
     * 1.黑白名单都有空，则不核查该参数，放过
     * 2.对象为空
     *      1.只有（白名单不空且包含）则放过
     *      2.其他都不放过
     * 3.对象不空
     *      1.如果（黑名单不空且包含）则不放过
     *      2.如果（白名单不空且不包含）则不放过
     *      3.其他都放过
     */
    private boolean primaryFieldAvailable(Object object, Field field, Map<String, Map<String, FieldJudge>> whiteFieldValue,
        Map<String, Map<String, FieldJudge>> blackFieldValue) {
        boolean blackEmpty = fieldCheckIsEmpty(object, field, blackFieldValue);
        boolean whiteEmpty = fieldCheckIsEmpty(object, field, whiteFieldValue);
        // 1.黑白名单都有空，则不核查该参数，放过
        if(whiteEmpty && blackEmpty){
            return true;
        }

        try {
            field.setAccessible(true);
            // 2.对象为空
            if (isEmpty(field.get(object))) {

                // 1.（黑名单不空且包含）则不放过
                if (!blackEmpty && fieldContain(object, field, blackFieldValue, false)){
                    return false;
                }

                // 2.（白名单不空且不包含）则不放过
                if (!whiteEmpty && !fieldContain(object, field, whiteFieldValue, true)){
                    return false;
                }
                return true;
            }
            // 3.对象不空
            else {
                // 1.如果（黑名单不空且包含）则不放过
                if (!blackEmpty && fieldContain(object, field, blackFieldValue, false)) {
                    return false;
                }

                // 2.如果（白名单不空且不包含）则不放过
                if(!whiteEmpty && !fieldContain(object, field, whiteFieldValue, true)){
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
     * @return
     * true 对象需要继续核查
     * false 对象不需要通过黑白名单核查
     */
    private boolean objectNeedCheck(Object object,  Map<String, Set<String>> objectFieldMap){
        if(!CollectionUtil.isEmpty(objectFieldMap)){
            return objectFieldMap.containsKey(object.getClass().getCanonicalName());
        }
        return false;
    }

    /**
     * 对象的所有判断是否都为空
     * @param object 对象
     * @param field 对象的属性
     * @param valueSet 可用或者不可用的数据
     * @return true:所有为空，false属性都有
     */
    private boolean fieldCheckIsEmpty(Object object, Field field, Map<String, Map<String, FieldJudge>> valueSet){
        if (checkDisable(object, field, valueSet)){
            return true;
        }
        Map<String, FieldJudge> fieldValueSetMap = valueSet.get(object.getClass().getCanonicalName());
        if(!CollectionUtil.isEmpty(fieldValueSetMap)){
            return fieldValueSetMap.get(field.getName()).isEmpty();
        }
        return true;
    }

    private boolean checkDisable(Object object, Field field, Map<String, Map<String, FieldJudge>> valueSet){
        Map<String, FieldJudge> fieldValueSetMap = valueSet.get(object.getClass().getCanonicalName());
        if(!CollectionUtil.isEmpty(fieldValueSetMap)){
            FieldJudge fieldJudge = fieldValueSetMap.get(field.getName());
            if(null != fieldJudge){
                return fieldJudge.getDisable();
            }
        }
        return true;
    }

    /**
     * 对象的某个属性可用或者不可用核查中是否包含
     * @param object 对象
     * @param field 对象的属性
     * @param valueSet 可用或者不可用数据
     * @param whiteOrBlack true=white, false=black
     */
    private boolean fieldContain(Object object, Field field, Map<String, Map<String, FieldJudge>> valueSet, Boolean whiteOrBlack){
        if (checkDisable(object, field, valueSet)) {
            return false;
        }

        Map<String, FieldJudge> fieldValueSetMap = valueSet.get(object.getClass().getCanonicalName());
        if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
            FieldJudge fieldJudge = fieldValueSetMap.get(field.getName());
            if (null != fieldJudge) {
                field.setAccessible(true);
                Object data;
                try {
                    data = field.get(object);
                    if (whiteOrBlack) {
                        return fieldJudge.judgeWhite(object, data, this);
                    } else {
                        return fieldJudge.judgeBlack(object, data, this);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 根据传入的自动构造映射树
     */
    private Map<String, Set<String>> generateObjectFieldMap(Map<String, Map<String, FieldJudge>> valueSet) {
        if (!CollectionUtil.isEmpty(valueSet)) {
            return valueSet.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, d -> new HashSet<>(d.getValue().keySet())));
        }
        return Collections.emptyMap();
    }

    /**
     * 根据传入的黑白名单一起构造映射树
     */
    private Map<String, Set<String>> generateObjectFieldMap(Map<String, Map<String, FieldJudge>> whiteSet,
        Map<String, Map<String, FieldJudge>> blackSet){
        Map<String, Set<String>> dataMap = new HashMap<>(12);
        if (!CollectionUtil.isEmpty(whiteSet)){
            dataMap.putAll(whiteSet.entrySet().stream().collect(Collectors.toMap(Entry::getKey, d->new HashSet<>(d.getValue().keySet()))));
        }

        // map 合并
        if (!CollectionUtil.isEmpty(blackSet)){
            Map<String, Set<String>> blackMap = blackSet.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, d->new HashSet<>(d.getValue().keySet())));
            blackMap.forEach((key, value) -> dataMap.compute(key, (k, v) -> {
                if (v == null) {
                    return new HashSet<>();
                } else {
                    v.addAll(value);
                    return v;
                }
            }));
        }

        return dataMap;
    }

    private void append(String errMsgStr, Object... keys){
        errMsg.get().append("-->").append(MessageFormat.format(errMsgStr, keys));
    }

    public void append(String errMsgStr){
        errMsg.get().append("-->").append(errMsgStr);
    }

    private void initErrMsg(){
        errMsg.remove();
        errMsg.set(new StringBuilder().append("数据校验失败"));
    }

    String getErrMsg(){
        String msg = errMsg.get().toString();
        initErrMsg();
        return msg;
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
