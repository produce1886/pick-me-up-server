package com.produce.pickmeup.domain.user;


import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
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

	@Builder
	public User(String email, String username, String image) {
		this.email = email;
		this.username = username;
		this.image = image;
	}

	public UserResponseDto toResponseDto() {
		return UserResponseDto.builder()
			.id(id)
			.email(email)
			.username(username)
			.image(image)
			.build();
	}

	public void updateImage(String image) {
		this.image = image;
	}
}
