package com.produce.pickmeup.controller;


import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.user.UserRequestDto;
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
	public ResponseEntity<Object> loginController(@RequestBody UserRequestDto userRequestDto) {
		if (userRequestDto.getEmail() == null) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST, ErrorCase.INVALID_FIELD_ERROR));
		}
		return ResponseEntity.ok().body(userService.login(userRequestDto));
	}
}