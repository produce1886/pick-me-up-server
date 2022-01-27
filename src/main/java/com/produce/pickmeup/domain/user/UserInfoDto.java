package com.produce.pickmeup.domain.user;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoDto {
    private final String email;
    private final String username;
    private final String image;
    private final String introduce;
    private final Date birth;
    private final String university;
    private final String major;
    private final String region;
    private final String interests;
    private final boolean isBirthPublic;
    private final boolean isUniversityPublic;
    private final boolean isRegionPublic;
    private final boolean isInterestsPublic;
}

