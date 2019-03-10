package com.simon.mikilin.core;

import com.alibaba.fastjson.JSON;
import com.simon.mikilin.core.Checks.FieldValue;
import com.simon.mikilin.core.annotation.FieldEnum;
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
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhouzhenyong
 * @since 2018/12/24 下午10:31
 */
@SuppressWarnings("unchecked")
final class CheckDelegate {

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
    boolean available(Object object, Map<String, Map<String, FieldValue>> whiteSet,
        Map<String, Map<String, FieldValue>> blackSet){
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
    boolean availableBlack(Object object, Map<String, Map<String, FieldValue>> blackSet){
        return available(object, generateObjectFieldMap(blackSet), Collections.emptyMap(), blackSet);
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     * @param blackSet 对象的属性的禁用值列表
     */
    boolean availableBlack(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldValue>> blackSet){
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
    boolean availableWhite(Object object, Map<String, Map<String, FieldValue>> whiteSet){
        return available(object, generateObjectFieldMap(whiteSet), whiteSet, Collections.emptyMap());
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     * @param whiteSet 对象的属性的可用值列表
     */
    boolean availableWhite(Object object, Map<String, Set<String>> objectFieldMap,
        Map<String, Map<String, FieldValue>> whiteSet){
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
        Map<String, Map<String, FieldValue>> whiteSet, Map<String, Map<String, FieldValue>> blackSet){
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
        Map<String, Map<String, FieldValue>> whiteSet,Map<String, Map<String, FieldValue>> blackSet) {
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
    private boolean primaryFieldAvailable(Object object, Field field, Map<String, Map<String, FieldValue>> whiteFieldValue,
        Map<String, Map<String, FieldValue>> blackFieldValue) {
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
                if (!blackEmpty && fieldContain(object, field, blackFieldValue)){
                    append("属性[{0}]为空，命中黑名单", field.getName());
                    return false;
                }

                // 2.（白名单不空且不包含）则不放过
                if (!whiteEmpty && !fieldContain(object, field, whiteFieldValue)){
                    append("属性[{0}]为空，不在白名单", field.getName());
                    return false;
                }
                return true;
            }
            // 3.对象不空
            else {
                // 1.如果（黑名单不空且包含）则不放过
                if (!blackEmpty && fieldContain(object, field, blackFieldValue)) {
                    append("属性[{0}]的值[{1}]位于黑名单中{2}", field.getName(), field.get(object),
                        fieldSet(object, field, blackFieldValue));
                    return false;
                }

                // 2.如果（白名单不空且不包含）则不放过
                if(!whiteEmpty && !fieldContain(object, field, whiteFieldValue)){
                    append("属性[{0}]的值[{1}]不在白名单{2}中", field.getName(), field.get(object),
                        fieldSet(object, field, whiteFieldValue));
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
    private boolean fieldCheckIsEmpty(Object object, Field field, Map<String, Map<String, FieldValue>> valueSet){
        if (checkDisable(object, field, valueSet)){
            return true;
        }
        Map<String, FieldValue> fieldValueSetMap = valueSet.get(object.getClass().getCanonicalName());
        if(!CollectionUtil.isEmpty(fieldValueSetMap)){
            FieldValue fieldValue = fieldValueSetMap.get(field.getName());
            if(null != fieldValue) {
                return valuesIsEmpty(fieldValue)
                    && typeIsEmpty(fieldValue)
                    && regexIsEmpty(fieldValue)
                    && judgeIsEmpty(fieldValue);
            }
            return true;
        }
        return true;
    }

    private boolean valuesIsEmpty(FieldValue fieldValue) {
        return CollectionUtil.isEmpty(fieldValue.getValues());
    }

    private boolean typeIsEmpty(FieldValue fieldValue) {
        return null == fieldValue.getFieldEnum();
    }

    private boolean regexIsEmpty(FieldValue fieldValue) {
        return null == fieldValue.getPattern();
    }

    private boolean judgeIsEmpty(FieldValue fieldValue) {
        return null == fieldValue.getPredicate();
    }

    private boolean checkDisable(Object object, Field field, Map<String, Map<String, FieldValue>> valueSet){
        Map<String, FieldValue> fieldValueSetMap = valueSet.get(object.getClass().getCanonicalName());
        if(!CollectionUtil.isEmpty(fieldValueSetMap)){
            FieldValue fieldValue = fieldValueSetMap.get(field.getName());
            if(null != fieldValue){
                return fieldValue.getDisable();
            }
        }
        return true;
    }


    /**
     * 对象的某个属性可用或者不可用核查中是否包含
     * @param object 对象
     * @param field 对象的属性
     * @param valueSet 可用或者不可用数据
     */
    private boolean fieldContain(Object object, Field field, Map<String, Map<String, FieldValue>> valueSet){
        if (checkDisable(object, field, valueSet)) {
            return false;
        }

        Map<String, FieldValue> fieldValueSetMap = valueSet.get(object.getClass().getCanonicalName());
        if (!CollectionUtil.isEmpty(fieldValueSetMap)) {
            FieldValue fieldValue = fieldValueSetMap.get(field.getName());
            if (null != fieldValue) {
                // 注解中的value是否包含
                if (valuesContain(object, field, fieldValue)) {
                    return true;
                }

                // 属性类型是否匹配
                if (typeContain(object, field, fieldValue)) {
                    return true;
                }

                // 正则表达式是否匹配
                if (regexContain(object, field, fieldValue)) {
                    return true;
                }

                // 外部判决是否匹配
                if (judgeContain(object, field, fieldValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 对象的某个属性列表包含，前提是这个列表存在
     * @param object 对象
     * @param field 对象的属性
     * @param fieldValue 可用或者不可用数据
     */
    private boolean valuesContain(Object object, Field field, FieldValue fieldValue){
        Set<Object> fieldSet = fieldValue.getValues();
        try {
            field.setAccessible(true);
            Object data = field.get(object);
            if (!CollectionUtil.isEmpty(fieldSet) && fieldSet.contains(data)){
                append("属性[{0}]的值[{1}]位于名单中{2}", field.getName(), data, fieldSet);
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Set<Object> fieldSet(Object object, Field field, Map<String, Map<String, FieldValue>> valueSet){
        String clsStr = object.getClass().getCanonicalName();
        if (!valueSet.containsKey(clsStr)){
            return null;
        }

        Map<String, FieldValue> fieldMap = valueSet.get(clsStr);
        if(!fieldMap.containsKey(field.getName())){
            return null;
        }

        FieldValue fieldValue = fieldMap.get(field.getName());
        if (null != fieldValue) {
            return fieldValue.getValues();
        }
        return null;
    }

    /**
     * 对象的某个属性列表包含，前提是这个列表存在
     * @param object 对象
     * @param field 属性
     * @param fieldValue 属性值
     */
    private boolean typeContain(Object object, Field field, FieldValue fieldValue){
        field.setAccessible(true);
        try {
            Object data = field.get(object);
            FieldEnum fieldEnum = fieldValue.getFieldEnum();
            if(null != fieldEnum) {
                if (fieldEnum.valid((String) (data))) {
                    append("属性[{0}]的值[{1}]命中[FieldEnum-{2}]", field.getName(), data, fieldEnum.name());
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 对象的某个属性列表包含，前提是这个列表存在
     * @param object 对象
     * @param field 对象的属性
     * @param fieldValue 属性value实体
     */
    private boolean regexContain(Object object, Field field, FieldValue fieldValue){
        field.setAccessible(true);
        try {
            Object data = field.get(object);
            Pattern pattern = fieldValue.getPattern();
            if (null != pattern) {
                boolean result = pattern.matcher((String) (data)).matches();
                if (pattern.matcher((String) (data)).matches()) {
                    append("属性[{0}]的值[{1}]命中正则表达式[{2}]", field.getName(), data, pattern.pattern());
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 对象的某个属性列表包含，前提是这个列表存在
     * @param object 对象
     * @param field 对象的属性
     * @param fieldValue 属性value实体
     */
    private boolean judgeContain(Object object, Field field, FieldValue fieldValue){
        field.setAccessible(true);
        try {
            Object data = field.get(object);
            Predicate<Object> predicate = fieldValue.getPredicate();
            if(null != predicate){
                if(predicate.test(data)){
                    append("属性[{0}]的值[{1}]在回调中命中", field.getName(), data);
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据传入的自动构造映射树
     */
    private Map<String, Set<String>> generateObjectFieldMap(Map<String, Map<String, FieldValue>> valueSet){
        if(!CollectionUtil.isEmpty(valueSet)){
            return valueSet.entrySet().stream().collect(Collectors.toMap(Entry::getKey, d->new HashSet<>(d.getValue().keySet())));
        }
        return Collections.emptyMap();
    }

    /**
     * 根据传入的黑白名单一起构造映射树
     */
    private Map<String, Set<String>> generateObjectFieldMap(Map<String, Map<String, FieldValue>> whiteSet,
        Map<String, Map<String, FieldValue>> blackSet){
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

    void append(String errMsgStr, Object... keys){
        errMsg.get().append("-->").append(MessageFormat.format(errMsgStr, keys));
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
