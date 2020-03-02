package com.simonalong.mikilin.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/12/25 下午5:17
 */
public final class ClassUtil {

    /**
     * 获取一个对象类的所有属性，包括继承的
     *
     * @param cls 待获取的类
     * @return 类的所有属性
     */
    public static Set<Field> allFieldsOfClass(Class<?> cls) {
        Set<Field> fieldSet = new HashSet<>();
        while (cls != null) {
            fieldSet.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        return fieldSet;
    }

    /**
     * 获取一个对象类的所有属性（包括继承的）的类
     *
     * @param cls 待获取的类
     * @return 类的属性对应的类
     */
    public Set<Class<?>> allFieldsClassOfClass(Class<?> cls) {
        return allFieldsOfClass(cls).stream().map(Field::getType).collect(Collectors.toSet());
    }

    /**
     * 判断一个类型是否我们需要核查的类型
     * 1.是基本类型或者基本类型的包装类型 Boolean Byte Character Short Integer Long Double Float 2.String 类型
     * 2.java.util.Date 类型
     *
     * 注意: 其中void.class.isPrimitive() 返回true，我们这里不需要这种
     *
     * @param cls 待校验的类
     * @return true=是基类，false=非基类
     */
    public static boolean isCheckedType(Class<?> cls) {
        boolean baseFlag = (cls.isPrimitive() && !cls.equals(void.class));
        if (baseFlag) {
            return true;
        } else {
            if (Void.class.isAssignableFrom(cls)) {
                return false;
            }

            if (Number.class.isAssignableFrom(cls)){
                return true;
            }

            if (String.class.isAssignableFrom(cls)) {
                return true;
            }

            if (Date.class.isAssignableFrom(cls)) {
                return true;
            }
            try {
                return ((Class<?>) cls.getField("TYPE").get(null)).isPrimitive();
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }
            return false;
        }
    }

    /**
     * 将集合或者Map的Class文件进行拆解开，获取对应的值的类
     *
     * 注意： 当前集合泛型只处理基本的 ParameterizedType，其他暂时不支持（TypeVariable, WildcardType, GenericArrayType）
     *
     * @param type 待拆分的类
     * @return 拆分之后类的类型
     */
    public static Class<?> peel(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
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
     * 将对象集合或者Map对象（只关心value）拆解开，获取对应的值的类 例如：{@code Map<String, AEntity>} 到 {@code Class<AEntity>}，{@code List<BEntity>}
     * 到 {@code Class<BEntity>}
     *
     * @param object 待拆解的类
     * @return 拆解后类的类型
     */
    public static Class<?> peel(Object object) {
        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            if (!collection.isEmpty()) {
                return peel(collection.stream().findFirst().get());
            }
            return null;
        } else if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            return peel(map.values());
        } else {
            return object.getClass();
        }
    }
}
