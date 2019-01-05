package com.simon.mikilin.core;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
public class MikilinTest {

    @Test
    public void test1(){
        CEntity cEntity = new CEntity();
        if(!Checks.check(cEntity)){
            System.out.println(Checks.getErrMsg());
        }else{
            System.out.println("数据正常");
        }
    }

    @Test
    public void test2(){
        List<Object> objectList = new ArrayList<>();
        objectList.add("a");
        objectList.add("b");

        System.out.println(objectList.contains("a"));
    }

}
