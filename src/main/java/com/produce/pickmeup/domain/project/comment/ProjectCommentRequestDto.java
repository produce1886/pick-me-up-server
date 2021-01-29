package com.produce.pickmeup.domain.project.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectCommentRequestDto {
	private final String authorEmail;
	private final String content;
}
