package com.produce.pickmeup.domain.portfolio.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PortfolioImageDto {
	private final long id;
	private final String image;
}
