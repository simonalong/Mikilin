package com.simonalong.mikilin;

import com.simonalong.mikilin.judge.JudgeEntity;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author zhouzhenyong
 * @since 2019/8/4 上午11:34
 */
@RunWith(JUnit4.class)
public class BaseTest {

    protected void show(Object ...obj){
        if(null == obj){
            show("obj is null");
        }else{
            System.out.println(obj.toString());
        }
    }
}
