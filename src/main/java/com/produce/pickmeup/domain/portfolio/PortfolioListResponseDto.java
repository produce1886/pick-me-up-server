package com.produce.pickmeup.domain.portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioListResponseDto {
    private int totalNum;
    private List<PortfolioDto> portfolioList;
}