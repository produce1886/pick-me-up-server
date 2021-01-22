package com.produce.pickmeup.domain.project.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectCommentRequestDto {
	private final String email;
	private final String content;
}
