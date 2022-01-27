package com.produce.pickmeup.domain.user;


import com.produce.pickmeup.domain.login.LoginResponseDto;
import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.project.Project;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<Project> projectList = new ArrayList<>();
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<Portfolio> portfolioList = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
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
    @Column(nullable = false)
    private boolean isBirthPublic;
    @Column(nullable = false)
    private boolean isUniversityPublic;
    @Column(nullable = false)
    private boolean isRegionPublic;
    @Column(nullable = false)
    private boolean isInterestsPublic;

    @Builder
    public User(String email, String username, String image) {
        this.email = email;
        this.username = username;
        this.image = image;
    }

    public LoginResponseDto toResponseDto() {
        return LoginResponseDto.builder()
            .id(id)
            .email(email)
            .username(username)
            .image(image)
            .build();
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public UserInfoDto toUserInfoDto() {
        return UserInfoDto.builder()
            .email(email)
            .username(username)
            .image(image)
            .introduce(introduce)
            .birth(birth)
            .university(university)
            .major(major)
            .region(region)
            .interests(interests)
            .isBirthPublic(isBirthPublic)
            .isUniversityPublic(isUniversityPublic)
            .isRegionPublic(isRegionPublic)
            .isInterestsPublic(isInterestsPublic)
            .build();
    }

    public void updateInfo(UserUpdateDto user) {
        this.username = user.getUsername();
        this.introduce = user.getIntroduce();
        this.birth = user.getBirth();
        this.university = user.getUniversity();
        this.major = user.getMajor();
        this.region = user.getRegion();
        this.interests = user.getInterests();
        this.isBirthPublic = user.isBirthPublic();
        this.isRegionPublic = user.isRegionPublic();
        this.isUniversityPublic = user.isUniversityPublic();
        this.isInterestsPublic = user.isInterestsPublic();
    }
}
