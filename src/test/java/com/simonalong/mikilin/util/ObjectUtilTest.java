package com.simonalong.mikilin.util;

import com.simonalong.mikilin.typeClass.DataEntity;
import com.simonalong.mikilin.typeClass.GenericArrayTypeEntity;
import com.simonalong.mikilin.typeClass.TypeVariableEntity;
import com.simonalong.mikilin.typeClass.WildcardTypeEntity;
import lombok.SneakyThrows;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shizi
 * @since 2020/3/24 下午10:45
 */
public class ObjectUtilTest {

    /**
     * 测试基本类型
     */
    @Test
    public void parseObjectTest1() {
        // 基本类型
        DataEntity dataEntity = new DataEntity();
        dataEntity.setName("a");
        // DataEntity(name=a)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(dataEntity));
    }

    /**
     * 测试：泛型类型
     */
    @Test
    @SneakyThrows
    public void parseObjectTest2() {
        // List<String>
        List<String> strList = new ArrayList<>();
        strList.add("a");
        // null
        System.out.println(ObjectUtil.parseObject(strList));

        System.out.println("=======");
        // List<DataEntity>
        List<DataEntity> dataEntities = new ArrayList<>();
        dataEntities.add(new DataEntity().setName("b"));
        // DataEntity(name=b)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(dataEntities));

        System.out.println("=======");
        // Map<String, DataEntity>
        Map<String, DataEntity> entityMap = new HashMap<>();
        entityMap.put("c", new DataEntity().setName("c2"));
        // DataEntity(name=c2)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(entityMap));

        System.out.println("=======");
        // Map<String, ? extends DataEntity> dataMap
        WildcardTypeEntity wildcardTypeEntity = new WildcardTypeEntity();
        Map<String, DataEntity> dataEntityMap = new HashMap<>();
        dataEntityMap.put("d_key", new DataEntity().setName("d1"));
        wildcardTypeEntity.setDataMap(dataEntityMap);
        Field field = wildcardTypeEntity.getClass().getDeclaredField("dataMap");
        field.setAccessible(true);
        // DataEntity(name=d1)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(field.get(wildcardTypeEntity)));
    }

    /**
     * 测试：泛型类型中的字符类型
     */
    @Test
    @SneakyThrows
    public void parseObjectTest3() {
        // T
        TypeVariableEntity<DataEntity> typeEntity = new TypeVariableEntity<>();
        typeEntity.setData(new DataEntity().setName("a"));
        Field field = typeEntity.getClass().getDeclaredField("data");
        field.setAccessible(true);
        // DataEntity(name=a)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(field.get(typeEntity)));

        // List<T>
        List<DataEntity> dataEntityList = new ArrayList<>();
        dataEntityList.add(new DataEntity().setName("a"));
        dataEntityList.add(new DataEntity().setName("b"));
        TypeVariableEntity<DataEntity> typeEntity2 = new TypeVariableEntity<>();
        typeEntity2.setDataList(dataEntityList);
        Field field2 = typeEntity2.getClass().getDeclaredField("dataList");
        field2.setAccessible(true);
        // DataEntity(name=a)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(field2.get(typeEntity2)));
    }

    @Test
    @SneakyThrows
    public void parseObjectTest4() {
        // T[]
        DataEntity[] dataEntities1 = new DataEntity[3];
        dataEntities1[0] = new DataEntity().setName("a");
        dataEntities1[1] = new DataEntity().setName("b");
        GenericArrayTypeEntity<DataEntity> genericEntity1 = new GenericArrayTypeEntity<>();
        genericEntity1.setDataArray(dataEntities1);
        Field field1 = genericEntity1.getClass().getDeclaredField("dataArray");
        field1.setAccessible(true);
        // DataEntity(name=a)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(field1.get(genericEntity1)));

        // T[] 数据为空
        GenericArrayTypeEntity<DataEntity> genericEntity2 = new GenericArrayTypeEntity<>();
        Field field2 = genericEntity2.getClass().getDeclaredField("dataArray");
        field2.setAccessible(true);
        // null
        System.out.println(ObjectUtil.parseObject(field2.get(genericEntity2)));
    }

    @Test
    @SneakyThrows
    public void parseObjectTest5() {

        // T[][]
        DataEntity[][] dataEntities1 = new DataEntity[3][3];
        dataEntities1[0][0] = new DataEntity().setName("a");
        dataEntities1[0][1] = new DataEntity().setName("b");
        GenericArrayTypeEntity<DataEntity> genericEntity1 = new GenericArrayTypeEntity<>();
        genericEntity1.setDataArrays(dataEntities1);
        Field field1 = genericEntity1.getClass().getDeclaredField("dataArrays");
        field1.setAccessible(true);
        // DataEntity(name=a)=class com.simonalong.mikilin.typeClass.DataEntity
        System.out.println(ObjectUtil.parseObject(field1.get(genericEntity1)));

        // T[][] 但是数据为空
        GenericArrayTypeEntity<DataEntity> genericEntity2 = new GenericArrayTypeEntity<>();
        Field field2 = genericEntity2.getClass().getDeclaredField("dataArrays");
        field2.setAccessible(true);
        // null
        System.out.println(ObjectUtil.parseObject(field2.get(genericEntity2)));
    }
}
