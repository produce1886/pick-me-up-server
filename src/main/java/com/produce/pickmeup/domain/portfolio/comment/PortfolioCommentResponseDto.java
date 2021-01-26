package com.produce.pickmeup.domain.portfolio.comment;

import com.produce.pickmeup.domain.login.LoginResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioCommentResponseDto {
	private final Long id;
	private final String content;
	private final Timestamp createdDate;
	private final Timestamp modifiedDate;
	private final LoginResponseDto user;
}
