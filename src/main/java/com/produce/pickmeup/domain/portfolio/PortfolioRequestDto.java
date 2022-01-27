package com.produce.pickmeup.domain.portfolio;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
    private final List<String> images;
}
