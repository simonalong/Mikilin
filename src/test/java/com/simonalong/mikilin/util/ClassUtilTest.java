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
        Assert.assertTrue(ClassUtil.isCheckedField(char.class));
        Assert.assertTrue(ClassUtil.isCheckedField(byte.class));
        Assert.assertTrue(ClassUtil.isCheckedField(short.class));
        Assert.assertTrue(ClassUtil.isCheckedField(int.class));
        Assert.assertTrue(ClassUtil.isCheckedField(long.class));
        Assert.assertTrue(ClassUtil.isCheckedField(float.class));
        Assert.assertTrue(ClassUtil.isCheckedField(double.class));
        Assert.assertTrue(ClassUtil.isCheckedField(boolean.class));
        Assert.assertFalse(ClassUtil.isCheckedField(void.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Character.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Byte.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Short.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Integer.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Long.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Float.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Double.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Boolean.class));
        Assert.assertFalse(ClassUtil.isCheckedField(Void.class));

        // 非基本类型
        Assert.assertTrue(ClassUtil.isCheckedField(String.class));
        Assert.assertTrue(ClassUtil.isCheckedField(Date.class));
    }
}
