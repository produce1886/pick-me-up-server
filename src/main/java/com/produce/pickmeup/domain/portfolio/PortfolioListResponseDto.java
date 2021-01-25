package com.produce.pickmeup.domain.portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioListResponseDto {
	private final int totalNum;
	private final List<PortfolioDto> portfolioList;
}