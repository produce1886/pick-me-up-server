package com.produce.pickmeup.controller;


import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.login.LoginRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserInfoDto;
import com.produce.pickmeup.service.UserService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
public class UserController {
	private final List<String> ERROR_LIST = ErrorCase.getErrorList();
	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<Object> loginController(@RequestBody LoginRequestDto loginRequestDto) {
		if (loginRequestDto.getEmail() == null) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST, ErrorCase.INVALID_FIELD_ERROR));
		}
		return ResponseEntity.ok().body(userService.login(loginRequestDto));
	}

	@PatchMapping("/user/{id}/image")
	public ResponseEntity<Object> updateUserImage(
		@RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {

		String result = userService.updateUserImage(multipartFile, id);
		if (ERROR_LIST.contains(result)) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, result));
		}
		return ResponseEntity.created(URI.create(result)).build();
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<Object> getUser(@PathVariable Long id) {
		Optional<User> optionalUser = userService.findById(id);
		return optionalUser.<ResponseEntity<Object>>map(
			user -> ResponseEntity.ok().body(user.toUserInfoDto()))
			.orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST, ErrorCase.NO_SUCH_USER)));
	}

	@PutMapping("/user/{id}")
	public ResponseEntity<Object> updateUser(@RequestBody UserInfoDto userInfo,
		@PathVariable Long id) {
		Optional<User> optionalUser = userService.findById(id);
		if (!optionalUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST, ErrorCase.NO_SUCH_USER));
		}
		userService.updateUserInfo(optionalUser.get(), userInfo);
		return ResponseEntity.ok().build();
	}
}
