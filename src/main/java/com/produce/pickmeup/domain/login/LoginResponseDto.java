package com.produce.pickmeup.domain.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
	private final long id;
	private final String username;
	private final String email;
	private final String image;
}
