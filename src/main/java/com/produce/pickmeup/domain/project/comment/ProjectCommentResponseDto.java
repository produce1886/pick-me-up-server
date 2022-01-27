package com.produce.pickmeup.domain.project.comment;

import com.produce.pickmeup.domain.login.LoginResponseDto;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectCommentResponseDto {
    private final Timestamp createdDate;
    private final Timestamp modifiedDate;
    private final Long id;
    private final String content;
    private final LoginResponseDto user;
}
