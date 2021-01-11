package com.produce.pickmeup.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequestDto {
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
