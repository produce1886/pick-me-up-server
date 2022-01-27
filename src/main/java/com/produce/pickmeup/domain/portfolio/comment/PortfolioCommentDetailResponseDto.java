package com.produce.pickmeup.domain.portfolio.comment;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioCommentDetailResponseDto {
    private final long id;
    private final String content;
    private final String authorEmail;
    private final Timestamp createdDate;
    private final Timestamp modifiedDate;
}
