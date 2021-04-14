package com.simonalong.mikilin.log;

import lombok.Data;

/**
 * @author shizi
 * @since 2021-04-14 11:41:27
 */
@Data
public class OsHealthyFinalEntity {

    private String deviceCode;
    private String score;
    private String timestamp;
    private String name;
    private String percent;
    private String scoreInner;
}
