package com.simon.mikilin.core;

import com.alibaba.fastjson.JSON;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhouzhenyong
 * @since 2018/12/24 下午10:31
 */
@SuppressWarnings("unchecked")
class CheckDelegate {

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
            return available(object, Collections.emptyList(), Collections.emptyList());
        }else{
            return available(object, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }
    }

    /**
     * 对象可用性校验
     * @param object 待校验对象（复杂自定义对象）
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     */
    boolean available(Object object, Map<String, List<String>> objectFieldMap){
        initErrMsg();
        if(isEmpty(object)){
            append("数据为空");
            return false;
        }

        if(ClassUtil.isBaseField(object.getClass())){
            return available(object, Collections.emptyList(), Collections.emptyList());
        }else{
            return available(object, objectFieldMap, Collections.emptyMap(), Collections.emptyMap());
        }
    }

    /**
     * 对象可用性校验
     * @param object 待校验对象（复杂自定义对象）
     * @param whiteList 对象的属性的可用值列表
     * @param blackList 对象的属性的禁用值列表
     */
    boolean available(Object object, Map<String, Map<String, List<Object>>> whiteList,
        Map<String, Map<String, List<Object>>> blackList){
        // 黑白名单都有的话，则按照白名单
        return available(object, generateObjectFieldMap(whiteList, blackList), whiteList, blackList);
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象（基本对象）
     * @param valueList 对象的属性的禁用值列表
     */
    boolean availableBlack(Object object, List<Object> valueList){
        return available(object, Collections.emptyList(), valueList);
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象（复杂对象）
     * @param blackList 对象的属性的禁用值列表
     */
    boolean availableBlack(Object object, Map<String, Map<String, List<Object>>> blackList){
        return available(object, generateObjectFieldMap(blackList), Collections.emptyMap(), blackList);
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     * @param blackList 对象的属性的禁用值列表
     */
    boolean availableBlack(Object object, Map<String, List<String>> objectFieldMap,
        Map<String, Map<String, List<Object>>> blackList){
        return available(object, objectFieldMap, Collections.emptyMap(), blackList);
    }

    /**
     * 对象值是否在可用列表中
     * @param object 待校验对象（基本对象）
     * @param valueList 对象的属性的可用值列表
     */
    <T> boolean availableWhite(T object, List<T> valueList){
        return available(object, valueList, Collections.emptyList());
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象（复杂对象）
     * @param whiteList 对象的属性的可用值列表
     */
    boolean availableWhite(Object object, Map<String, Map<String, List<Object>>> whiteList){
        return available(object, generateObjectFieldMap(whiteList), whiteList, Collections.emptyMap());
    }

    /**
     * 对象值是否在不可用列表中
     * @param object 待校验对象
     * @param objectFieldMap 对象的待测属性映射：key：类的simpleName，value：属性的名字
     * @param whiteList 对象的属性的可用值列表
     */
    boolean availableWhite(Object object, Map<String, List<String>> objectFieldMap,
        Map<String, Map<String, List<Object>>> whiteList){
        return available(object, objectFieldMap, whiteList, Collections.emptyMap());
    }

    /**
     * 判断基本类型的数据值是否是可用的，这里判断逻辑是通过黑名单和白名单
     * @param object 基本类型的数据对象，如果是复杂对象，则直接放过
     * @param whiteList 可用值列表
     * @param blackList 不可用值列表
     */
    <T> boolean available(T object, List<T> whiteList, List<T> blackList){
        initErrMsg();
        if(null == object){
            // 1.（黑名单不空且包含）则不放过
            if (null != blackList && !blackList.isEmpty() && blackList.contains(null)){
                append("对象为null，命中黑名单");
                return false;
            }

            // 2.（白名单不空且不包含）则不放过
            if (null != whiteList && !whiteList.isEmpty() && !whiteList.contains(null)){
                append("对象为null，不在白名单");
                return false;
            }
            return true;
        }

        Class cls = object.getClass();
        if(ClassUtil.isBaseField(cls)){
            // 底层基本校验类型，则放过
            return baseAvailable(object, whiteList, blackList);
        } else if(Collection.class.isAssignableFrom(cls)){
            // 集合类型，则剥离集合，获取泛型的类型再进行判断
            Collection<T> collection = Collection.class.cast(object);
            if (!CollectionUtil.isEmpty(collection)){
                return collection.stream().allMatch(c-> available(c, whiteList, blackList));
            }else{
                // 集合空类型，认为不可用
                append("集合类型为空");
                return false;
            }
        } else if(Map.class.isAssignableFrom(cls)) {
            // Map 结构中的数据的判断，剥离key和value，只判断value的值
            Map<String, T> map = Map.class.cast(object);
            if (!CollectionUtil.isEmpty(map)) {
                return map.values().stream().filter(Objects::nonNull).allMatch(v -> available(v, whiteList, blackList));
            } else {
                append("Map类型为空");
                return false;
            }
        } else {
            // 自定义类型，这里不拦截自定义类型
            // 1.（黑名单不空且包含）则不放过
            if (null != blackList && !blackList.isEmpty() && blackList.contains(object)){
                append("对象为[{0}]，命中黑名单", object);
                return false;
            }

            // 2.（白名单不空且不包含）则不放过
            if (null != whiteList && !whiteList.isEmpty() && !whiteList.contains(object)){
                append("对象为[{0}]，不在白名单", object);
                return false;
            }
            return true;
        }
    }

    /**
     * 判断基本类型的数据值是否是可用的，这里判断逻辑是通过黑名单和白名单
     * @param object 基本类型的数据对象，如果是复杂对象，则直接放过
     * @param whiteList 可用值列表
     * @param blackList 不可用值列表
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
    private <T> boolean baseAvailable(T object, List<T> whiteList, List<T> blackList){
        boolean whiteEmpty = CollectionUtil.isEmpty(whiteList);
        boolean blackEmpty = CollectionUtil.isEmpty(blackList);

        // 1.黑白名单都有空，则不核查该参数，放过
        if(whiteEmpty && blackEmpty){
            return true;
        }

        // 2.对象为空
        if(isEmpty(object)){
            // 1.（黑名单不空且包含）则不放过
            if (!blackEmpty && blackList.contains(object)){
                append("对象为空，命中黑名单");
                return false;
            }

            // 2.（白名单不空且不包含）则不放过
            if (!whiteEmpty && !whiteList.contains(object)){
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
            if (!blackEmpty && blackList.contains(object)){
                append("对象值[{0}]位于黑名单{1}", JSON.toJSONString(object), blackList);
                return false;
            }

            // 2.如果（白名单不空且不包含）则不放过
            if(!whiteEmpty && !whiteList.contains(object)){
                append("对象值[{0}]不在白名单{1}中", JSON.toJSONString(object), whiteList);
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
     * @param whiteList 对象属性集合的可用值列表
     * @param blackList 对象属性集合的不可用值列表
     * @return
     * false：如果对象中有某个属性不可用
     * true：所有属性都可用
     */
    boolean available(Object object, Map<String, List<String>> objectFieldMap,
        Map<String, Map<String, List<Object>>> whiteList, Map<String, Map<String, List<Object>>> blackList){
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
                return collection.stream().allMatch(c-> available(c, objectFieldMap, whiteList, blackList));
            }else{
                // 为空则忽略
                return true;
            }
        } else if(Map.class.isAssignableFrom(cls)) {
            // Map 结构中的数据的判断，目前只判断value中的值
            Map map = Map.class.cast(object);
            if (!CollectionUtil.isEmpty(map)) {
                if(map.values().stream().filter(Objects::nonNull).allMatch(v -> available(v, objectFieldMap, whiteList, blackList))){
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
            if(ClassUtil.allFieldsOfClass(object.getClass()).stream().allMatch(f-> available(object, f, objectFieldMap, whiteList, blackList))){
                return true;
            }

            append("自定义类型[{0}]核查失败", object.getClass().getSimpleName());
            return false;
        }
    }

    /**
     * 根据属性的类型，判断属性的值是否在对应的值列表中
     * @param object 对象
     * @param field 属性
     * @param whiteList 属性值可用值列表
     * @param blackList 属性值的不用值列表
     * @param objectFieldMap 对象核查的属性映射
     */
    private boolean available(Object object, Field field,  Map<String, List<String>> objectFieldMap,
        Map<String, Map<String, List<Object>>> whiteList,Map<String, Map<String, List<Object>>> blackList) {
        Class cls = field.getType();
        if(ClassUtil.isBaseField(cls)){
            // 基本类型，则直接校验
            return primaryFieldAvailable(object, field, whiteList, blackList);
        } else {
            // 不是基本类型，则按照复杂类型处理
            try {
                field.setAccessible(true);
                if(available(field.get(object), objectFieldMap, whiteList, blackList)){
                    return true;
                }

                append("自定义类型[{0}]的属性[{1}]核查失败", object.getClass().getSimpleName(), field.getName());
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
     * @param whiteList 属性的可用值列表
     * @param blackList 属性的不可用值列表
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
    private boolean primaryFieldAvailable(Object object, Field field, Map<String, Map<String, List<Object>>> whiteList,
        Map<String, Map<String, List<Object>>> blackList) {
        boolean blackEmpty = fieldListIsEmpty(object, field, blackList);
        boolean whiteEmpty = fieldListIsEmpty(object, field, whiteList);
        // 1.黑白名单都有空，则不核查该参数，放过
        if(whiteEmpty && blackEmpty){
            return true;
        }

        try {
            field.setAccessible(true);
            // 2.对象为空
            if (isEmpty(field.get(object))) {

                // 1.（黑名单不空且包含）则不放过
                if (!blackEmpty && fieldListContain(object, field, blackList)){
                    append("属性[{0}]为空，命中黑名单", field.getName());
                    return false;
                }

                // 2.（白名单不空且不包含）则不放过
                if (!whiteEmpty && !fieldListContain(object, field, whiteList)){
                    append("属性[{0}]为空，不在白名单", field.getName());
                    return false;
                }
                return true;
            }
            // 3.对象不空
            else {
                // 1.如果（黑名单不空且包含）则不放过
                if (!blackEmpty && fieldListContain(object, field, blackList)) {
                    append("属性[{0}]的值[{1}]位于黑名单中{2}", field.getName(), field.get(object),
                        fieldList(object, field, blackList));
                    return false;
                }

                // 2.如果（白名单不空且不包含）则不放过
                if(!whiteEmpty && !fieldListContain(object, field, whiteList)){
                    append("属性[{0}]的值[{1}]不在白名单{2}中", field.getName(), field.get(object),
                        fieldList(object, field, whiteList));
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
    private boolean objectNeedCheck(Object object,  Map<String, List<String>> objectFieldMap){
        if(!CollectionUtil.isEmpty(objectFieldMap)){
            return objectFieldMap.containsKey(object.getClass().getSimpleName());
        }
        return false;
    }


    /**
     * 对象的某个属性列表为空
     * @param object 对象
     * @param field 对象的属性
     * @param valueList 可用或者不可用数据
     */
    private boolean fieldListIsEmpty(Object object, Field field, Map<String, Map<String, List<Object>>> valueList){
        Map<String, List<Object>> fieldValueListMap = valueList.get(object.getClass().getSimpleName());
        if(!CollectionUtil.isEmpty(fieldValueListMap)){
            List<Object> fieldValueList = fieldValueListMap.get(field.getName());
            return CollectionUtil.isEmpty(fieldValueList);
        }
        return true;
    }

    /**
     * 对象的某个属性列表包含，前提是这个列表存在
     * @param object 对象
     * @param field 对象的属性
     * @param valueList 可用或者不可用数据
     */
    private boolean fieldListContain(Object object, Field field, Map<String, Map<String, List<Object>>> valueList){
        List<Object> fieldList = fieldList(object, field, valueList);
        try {
            field.setAccessible(true);
            Object data = field.get(object);
            if (fieldList.contains(data)){
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Object> fieldList(Object object, Field field, Map<String, Map<String, List<Object>>> valueList){
        return valueList.get(object.getClass().getSimpleName()).get(field.getName());
    }

    /**
     * 根据传入的自动构造映射树
     */
    private Map<String, List<String>> generateObjectFieldMap(Map<String, Map<String, List<Object>>> valueList){
        if(!CollectionUtil.isEmpty(valueList)){
            return valueList.entrySet().stream().collect(Collectors.toMap(Entry::getKey, d->new ArrayList<>(d.getValue().keySet())));
        }
        return Collections.emptyMap();
    }

    /**
     * 根据传入的黑白名单一起构造映射树
     */
    private Map<String, List<String>> generateObjectFieldMap(Map<String, Map<String, List<Object>>> whiteList,
        Map<String, Map<String, List<Object>>> blackList){
        Map<String, List<String>> dataMap = new HashMap<>(12);
        if (!CollectionUtil.isEmpty(whiteList)){
            dataMap.putAll(whiteList.entrySet().stream().collect(Collectors.toMap(Entry::getKey, d->new ArrayList<>(d.getValue().keySet()))));
        }

        // map 合并
        if (!CollectionUtil.isEmpty(blackList)){
            Map<String, List<String>> blackMap = blackList.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, d->new ArrayList<>(d.getValue().keySet())));
            blackMap.forEach((key, value) -> dataMap.compute(key, (k, v) -> {
                if (v == null) {
                    return new ArrayList<>();
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
