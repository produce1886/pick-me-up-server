package com.produce.pickmeup.domain.login;

import com.produce.pickmeup.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequestDto {
	private final String email;
	private final String username;
	private final String image;

	public User toEntity() {
		return User.builder()
			.email(email)
			.username(username)
			.image(image)
			.build();
	}
}
