package com.simonalong.mikilin.express;

import com.simonalong.mikilin.util.Maps;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:26
 */
public class ExpressTest {

    @Test
    public void testScript(){
        Date begin = getDate(2019, 2, 3, 12, 00, 32);
        Date end = getDate(2019, 9, 3, 12, 00, 32);
        ExpressParser express = new ExpressParser(Maps.of("begin", begin, "end", end));
        express.addBinding(Maps.of("o", new Date()));
        Assert.assertTrue(express.parse("import static java.lang.Math.*",
            "begin <= o && o <= end"));
    }

    private Date getDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second){
        return Date.from(LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.systemDefault()).toInstant());
    }
}
