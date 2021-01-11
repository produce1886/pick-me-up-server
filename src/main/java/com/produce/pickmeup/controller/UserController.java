package com.produce.pickmeup.controller;


import com.produce.pickmeup.domain.user.UserRequestDto;
import com.produce.pickmeup.domain.user.UserResponseDto;
import com.produce.pickmeup.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<UserResponseDto> loginController(@RequestBody UserRequestDto userRequestDto) {
		return new ResponseEntity<>(userService.login(userRequestDto), HttpStatus.OK);
	}
}
