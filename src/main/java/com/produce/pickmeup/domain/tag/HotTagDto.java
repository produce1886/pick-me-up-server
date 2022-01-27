package com.produce.pickmeup.domain.tag;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HotTagDto {
    private final List<TagDto> tags;
}
