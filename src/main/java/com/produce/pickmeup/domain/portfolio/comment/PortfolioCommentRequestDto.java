package com.produce.pickmeup.domain.portfolio.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioCommentRequestDto {
    private final String authorEmail;
    private final String content;
}
