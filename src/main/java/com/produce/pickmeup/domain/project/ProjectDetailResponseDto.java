package com.produce.pickmeup.domain.project;

import com.produce.pickmeup.domain.login.LoginResponseDto;
import com.produce.pickmeup.domain.project.comment.ProjectCommentResponseDto;
import com.produce.pickmeup.domain.tag.TagDto;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectDetailResponseDto {
    private final long id;
    private final String title;
    private final String content;
    private final String category;
    private final String recruitmentField;
    private final String region;
    private final String projectSection;
    private final List<TagDto> projectTags;
    private final String image;
    private final Timestamp createdDate;
    private final Timestamp modifiedDate;
    private final LoginResponseDto user;
    private final long viewNum;
    private final int commentsNum;
    private final List<ProjectCommentResponseDto> comments;
}
