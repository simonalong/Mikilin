package com.simonalong.mikilin.log;

import lombok.Data;

import java.util.List;

/**
 * @author shizi
 * @since 2021-04-14 11:38:31
 */
@Data
public class OsHealthyEntity {

    private String deviceCode;
    private String score;
    private String detail;
    private Long timestamp;
}
