package com.produce.pickmeup.domain.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioTagDto {
    private final long id;
    private final String tag;
}
