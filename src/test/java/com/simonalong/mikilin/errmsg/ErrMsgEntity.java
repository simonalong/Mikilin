package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/17 下午11:46
 */
@Data
@Accessors(chain = true)
public class ErrMsgEntity {

    private String name;
    @Matcher(range = "[0, 100]", errMsg = "年龄只能为0~100的数字")
    private Integer age;
}
