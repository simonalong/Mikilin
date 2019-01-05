package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import com.simon.mikilin.core.annotation.TypeCheck;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/12/25 下午4:16
 */
@UtilityClass
@SuppressWarnings("unchecked")
public class Checks {

    /**
     * 对象属性值白名单
     */
    private Map<String, Map<String, List<Object>>> whiteMapFieldMap;
    /**
     * 对象属性值黑名单
     */
    private Map<String, Map<String, List<Object>>> blackMapFieldMap;
    /**
     * 对象属性核查映射
     */
    private Map<String, List<String>> objectFieldCheckMap;

    private CheckDelegate delegate;

    static{
        init();
    }

    private void init(){
        whiteMapFieldMap = new ConcurrentHashMap<>(20);
        blackMapFieldMap = new ConcurrentHashMap<>(20);
        objectFieldCheckMap = new ConcurrentHashMap<>(16);
        delegate = new CheckDelegate();
    }

    /**
     * 自定义的复杂类型校验，基本类型校验不校验，基本类型校验为baseCheck
     */
    public boolean check(Object object){
        if(delegate.isEmpty(object)){
            delegate.append("数据为空");
            return false;
        }

        if(ClassUtil.isBaseField(object.getClass())){
            return true;
        }else{
            return delegate.available(object, getObjFieldMap(object), getWhiteMap(object), getBlackMap(object));
        }
    }

    /**
     * 类型白名单核查
     * 注意：
     * 建议基本类型使用，自定义类型、集合和Map类型不建议使用
     */
    public <T> boolean checkWhite(T object, List<T> whiteList){
        return check(object, whiteList, Collections.emptyList());
    }

    /**
     * 类型黑名单核查
     * 注意：
     * 建议基本类型使用，自定义类型、集合和Map类型不建议使用
     */
    public <T> boolean checkBlack(T object, List<T> blackList){
        return check(object, Collections.emptyList(), blackList);
    }

    private <T> boolean check(T object, List<T> whiteList, List<T> blackList){
        return delegate.available(object, whiteList, blackList);
    }

    public String getErrMsg(){
        return delegate.getErrMsg();
    }

    private Map<String, List<String>> getObjFieldMap(Object object){
        if (null == object){
            return Collections.emptyMap();
        }

        String objKey = object.getClass().getSimpleName();
        List<String> fields = objectFieldCheckMap.get(objKey);
        if (!CollectionUtil.isEmpty(fields)){
            return objectFieldCheckMap;
        }

        // 若当前对象没有对象属性索引树，则进行创建
        createObjectFieldMap(ClassUtil.peel(object));

        return objectFieldCheckMap;
    }

    private Map<String, Map<String, List<Object>>> getWhiteMap(Object object){
        return whiteMapFieldMap;
    }

    private Map<String, Map<String, List<Object>>> getBlackMap(Object object){
        return blackMapFieldMap;
    }

    /**
     * 根据对象的类型进行建立对象和属性映射树
     */
    private void createObjectFieldMap(Class<?> cls){
        if (null == cls){
            return;
        }
        // 剥离外部的一些壳之后类的类型
        TypeCheck typeCheck = cls.getAnnotation(TypeCheck.class);
        if (null != typeCheck && !typeCheck.disable()){
            String objectClsName = cls.getSimpleName();

            List<Field> fieldList = ClassUtil.allFieldsOfClass(cls);
            if (!CollectionUtil.isEmpty(fieldList)){
                // 基本类型用于获取注解的属性
                fieldList.forEach(f->{
                    FieldCheck fieldCheck = f.getAnnotation(FieldCheck.class);
                    if(null != fieldCheck && !fieldCheck.disable()){
                        addObjectFieldMap(objectClsName, f.getName());
                        addValueMap(whiteMapFieldMap, objectClsName, f, fieldCheck.includes());
                        addValueMap(blackMapFieldMap, objectClsName, f, fieldCheck.excludes());
                    }
                });

                // 非基本类型拆分开进行迭代分析
                fieldList.stream().filter(f->!ClassUtil.isBaseField(f.getType())).forEach(f->{

                    Class<?> fClass = ClassUtil.peel(f.getGenericType());
                    // 该属性对应的类型是否添加了注解 TypeCheck
                    TypeCheck fieldObjectType = cls.getAnnotation(TypeCheck.class);
                    if (null != fieldObjectType && !fieldObjectType.disable()) {
                        createObjectFieldMap(fClass);
                    }
                });
            }
        }
    }

    private void addObjectFieldMap(String objectClsName, String fieldName){
        objectFieldCheckMap.compute(objectClsName, (k,v)->{
            if(null == v){
                List<String> fieldList = new ArrayList<>();
                fieldList.add(fieldName);
                return fieldList;
            }else{
                v.add(fieldName);
                return v;
            }
        });
    }

    private void addValueMap(Map<String, Map<String, List<Object>>> fieldMap, String objectName, Field field, String[] values){
        if(values.length == 0){
            return;
        }

        fieldMap.compute(objectName, (k, v) -> {
            if (null == v) {
                return Maps.builder().add(field.getName(), new ArrayList<>(getObjectList(field, values))).build();
            } else {
                v.compute(field.getName(), (bk, bv) -> {
                    if (null == bv) {
                        return new ArrayList<>(getObjectList(field, values));
                    } else {
                        bv.add(getObjectList(field, values));
                        return bv;
                    }
                });
                return v;
            }
        });
    }

    private List<Object> getObjectList(Field field, String[] valueList){
        return Arrays.stream(valueList).map(i->{
            if(null != i && !"".equals(i)){
                return Objects.cast(field.getType(), i);
            }else{
                return null;
            }
        }).collect(Collectors.toList());
    }
}
