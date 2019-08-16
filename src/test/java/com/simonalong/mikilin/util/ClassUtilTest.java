package com.simonalong.mikilin.util;

import com.simonalong.mikilin.BaseTest;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/8/4 上午11:33
 */
public class ClassUtilTest extends BaseTest {

    @Test
    public void isCheckedFieldTest(){
        Assert.assertTrue(ClassUtil.isCheckedType(char.class));
        Assert.assertTrue(ClassUtil.isCheckedType(byte.class));
        Assert.assertTrue(ClassUtil.isCheckedType(short.class));
        Assert.assertTrue(ClassUtil.isCheckedType(int.class));
        Assert.assertTrue(ClassUtil.isCheckedType(long.class));
        Assert.assertTrue(ClassUtil.isCheckedType(float.class));
        Assert.assertTrue(ClassUtil.isCheckedType(double.class));
        Assert.assertTrue(ClassUtil.isCheckedType(boolean.class));
        Assert.assertFalse(ClassUtil.isCheckedType(void.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Character.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Byte.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Short.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Integer.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Long.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Float.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Double.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Boolean.class));
        Assert.assertFalse(ClassUtil.isCheckedType(Void.class));

        // 非基本类型
        Assert.assertTrue(ClassUtil.isCheckedType(String.class));
        Assert.assertTrue(ClassUtil.isCheckedType(Date.class));
    }
}
