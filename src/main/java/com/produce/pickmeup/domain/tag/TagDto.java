package com.produce.pickmeup.domain.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TagDto {
	private final long id;
	private final String tagName;
}