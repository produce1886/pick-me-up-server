package com.produce.pickmeup.domain.project;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectRequestDto {
    private final String title;
    private final String authorEmail;
    private final String content;
    private final String category;
    private final String recruitmentField;
    private final String region;
    private final String projectSection;
    private final String image;
    private final List<String> projectTags;
}
