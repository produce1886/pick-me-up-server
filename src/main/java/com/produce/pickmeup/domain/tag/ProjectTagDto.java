package com.produce.pickmeup.domain.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectTagDto {
	private final long id;
	private final String tagName;
}