package com.produce.pickmeup.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.sql.Date;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoDto {
    @Column(nullable = false)
    private final String username;
    private final String introduce;
    private final Date birth;
    private final String university;
    private final String major;
    private final String region;
    private final String interests;
    @Column(nullable = false)
    private final boolean isBirthPublic;
    @Column(nullable = false)
    private final boolean isUniversityPublic;
    @Column(nullable = false)
    private final boolean isRegionPublic;
    @Column(nullable = false)
    private final boolean isInterestsPublic;
}

