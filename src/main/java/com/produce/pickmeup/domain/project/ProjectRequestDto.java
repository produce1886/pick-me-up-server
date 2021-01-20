package com.produce.pickmeup.domain.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectRequestDto {
	private final long id;
	private final String title;
	private final String authorEmail;
	private final String content;
	private final String category;
	private final String recruitmentField;
	private final String region;
	private final String projectSection;
}
