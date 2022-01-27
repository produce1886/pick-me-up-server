package com.produce.pickmeup.domain.tag.history;

import com.produce.pickmeup.domain.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TagHistoryGroupByDto {
    private final Tag tag;
    private final Long score;
}
