package com.produce.pickmeup.domain.project;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectListResponseDto {
	private final int totalNum;
	private final List<ProjectDto> projectList;
}
