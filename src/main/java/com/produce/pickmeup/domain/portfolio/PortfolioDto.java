package com.produce.pickmeup.domain.portfolio;

import com.produce.pickmeup.domain.login.LoginResponseDto;
import com.produce.pickmeup.domain.tag.PortfolioTagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PortfolioDto {
    private final long id;
    private final String title;
    private final Timestamp createdDate;
    private final Timestamp modifiedDate;
    private final String authorEmail;
    private final LoginResponseDto user;
    private final String content;
    private final int commentsNum;
    private final long viewNum;
    private final String category;
    private final String recruitmentField;
    private final List<PortfolioTagDto> portfolioTags;
}