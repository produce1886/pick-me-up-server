package com.produce.pickmeup.domain.portfolio;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioListResponseDto {
    private final int totalNum;
    private final List<PortfolioDto> portfolioList;
}
