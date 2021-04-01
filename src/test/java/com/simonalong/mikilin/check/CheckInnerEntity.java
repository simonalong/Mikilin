package com.simonalong.mikilin.check;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.*;

/**
 * @author shizi
 * @since 2021-03-31 16:18:49
 */
@Getter
@Setter
@EqualsAndHashCode(of = "name")
@AllArgsConstructor
public class CheckInnerEntity {

    @Matcher(range = "[0, 12]")
    private Integer age;
    private String name;
}
