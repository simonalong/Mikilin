package com.simonalong.mikilin.check;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shizi
 * @since 2021-03-31 16:20:19
 */
public class ValidateCheck {

    public boolean haveRepeat(List<CheckInnerEntity> innerEntityList) {
        Map<String, List<CheckInnerEntity>> innerEntityListCopy = innerEntityList.stream().collect(Collectors.groupingBy(CheckInnerEntity::getName));
        return innerEntityListCopy.size() != innerEntityList.size();
    }
}
