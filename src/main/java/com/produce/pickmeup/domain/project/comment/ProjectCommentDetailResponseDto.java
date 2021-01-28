package com.produce.pickmeup.domain.project.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
public class ProjectCommentDetailResponseDto {
	private final Long id;
	private final String content;
	private final String authorEmail;
	private final Timestamp createdDate;
	private final Timestamp modifiedDate;
}