package com.produce.pickmeup.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.sql.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDto {
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String username;
    private String image;
    private String introduce;
    private Date birth;
    private String university;
    private String major;
    private String region;
    private String interests;
    private boolean isBirthPublic;
    private boolean isUniversityPublic;
    private boolean isRegionPublic;
    private boolean isInterestsPublic;
}

