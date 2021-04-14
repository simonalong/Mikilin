package com.simonalong.mikilin.log;

import lombok.Data;

/**
 * @author shizi
 * @since 2021-04-14 11:00:17
 */
@Data
public class AuthEntity {

    private String deviceCode;
    private String os;
    private String availableDays;
}
