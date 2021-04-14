package com.simonalong.mikilin.log;

import lombok.Data;

/**
 * @author shizi
 * @since 2021-04-14 11:32:35
 */
@Data
public class OsExceptionEntity {

    private String deviceCode;
    private String exception;
    private String minRt;
    private String appName;
    private String success;
    private String avgRt;
    private String resourceName;
    private String maxRt;
}
