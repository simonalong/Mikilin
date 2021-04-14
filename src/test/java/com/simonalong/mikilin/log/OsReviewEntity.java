package com.simonalong.mikilin.log;

import lombok.Data;

/**
 * @author shizi
 * @since 2021-04-14 11:17:12
 */
@Data
public class OsReviewEntity {

    private String deviceCode;
    private String exception;
    private String success;
    private String namespace;
    private String avgRt;
}
