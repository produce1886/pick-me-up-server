package com.produce.pickmeup.domain.portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioRequestDto {
	private final String title;
	private final String authorEmail;
	private final String content;
	private final String category;
	private final String recruitmentField;
	private final List<String> portfolioTags;
	private final String image;
}
