package com.produce.pickmeup.domain.portfolio;

import com.produce.pickmeup.domain.login.LoginResponseDto;
import com.produce.pickmeup.domain.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioDetailResponseDto {
	private final long id;
	private final String title;
	private final String content;
	private final String category;
	private final String recruitmentField;
	private final List<TagDto> portfolioTags;
	private final String image;
	private final Timestamp createdDate;
	private final Timestamp modifiedDate;
	private final LoginResponseDto user;
	private final long viewNum;
	private final int commentsNum;
	private final List<PortfolioCommentResponseDto> comments;
}
