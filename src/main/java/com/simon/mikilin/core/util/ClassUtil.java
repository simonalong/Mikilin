package com.simon.mikilin.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/12/25 下午5:17
 */
@UtilityClass
public class ClassUtil {

    /**
     * 获取一个对象类的所有属性，包括继承的
     * @param cls 待获取的类
     * @return 类的所有属性
     */
    public Set<Field> allFieldsOfClass(Class<?> cls){
        Set<Field> fieldSet = new HashSet<>();
        while (cls != null){
            fieldSet.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        return fieldSet;
    }

    /**
     * 获取一个对象类的所有属性（包括继承的）的类
     * @param cls 待获取的类
     * @return 类的属性对应的类
     */
    public Set<Class<?>> allFieldsClassOfClass(Class<?> cls){
        return allFieldsOfClass(cls).stream().map(Field::getType).collect(Collectors.toSet());
    }

    /**
     * 判断一个类型是否我们常用的底层对象
     * 1.是基本类型或者基本类型的包装类型 Boolean Byte Character Short Integer Long Double Float
     * 2.String 类型
     *
     * 注意:
     * 其中void.class.isPrimitive() 返回true，我们这里不需要这种
     *
     * @param cls 待校验的类
     * @return true=是基类，false=非基类
     */
    public boolean isBaseField(Class<?> cls) {
        boolean baseFlag = (cls.isPrimitive() && !cls.equals(void.class)) || cls.equals(String.class);
        if (baseFlag) {
            return true;
        } else {
            try{
                if(!cls.equals(Void.class)) {
                    return ((Class) cls.getField("TYPE").get(null)).isPrimitive();
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                return false;
            }
            return false;
        }
    }

    /**
     * 将集合或者Map的Class文件进行拆解开，获取对应的值的类
     *
     * 注意：
     * 当前集合泛型只处理基本的 ParameterizedType，其他暂时不支持（TypeVariable, WildcardType, GenericArrayType）
     *
     * @param type 待拆分的类
     * @return 拆分之后类的类型
     */
    public Class<?> peel(Type type) {
        if (type instanceof Class<?>) {
            return Class.class.cast(type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType p = ParameterizedType.class.cast(type);
            Type[] types = p.getActualTypeArguments();
            if (types.length == 1) {
                return peel(types[0]);
            } else if (types.length == 2) {
                return peel(types[1]);
            }
        }
        return null;
    }

    /**
     * 将对象集合或者Map对象（只关心value）拆解开，获取对应的值的类
     * 例如：{@code Map<String, AEntity>} 到 {@code Class<AEntity>}，{@code List<BEntity>} 到 {@code Class<BEntity>}
     *
     * @param object 待拆解的类
     * @return 拆解后类的类型
     */
    public Class<?> peel(Object object) {
        if (object instanceof Collection) {
            Collection collection = Collection.class.cast(object);
            if (!collection.isEmpty()) {
                return peel(collection.stream().findFirst().get());
            }
            return null;
        } else if (object instanceof Map) {
            Map map = Map.class.cast(object);
            return peel(map.values());
        } else {
            return object.getClass();
        }
    }
}
