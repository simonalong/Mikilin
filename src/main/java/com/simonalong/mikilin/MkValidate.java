package com.simonalong.mikilin;

/**
 * @author shizi
 * @since 2021-07-24 15:49:58
 */
public interface MkValidate {

    default boolean match() {
        return true;
    }

    default boolean accept() {
        return true;
    }
}
