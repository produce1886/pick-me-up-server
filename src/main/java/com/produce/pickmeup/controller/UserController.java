package com.produce.pickmeup.controller;


import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.login.LoginRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserUpdateDto;
import com.produce.pickmeup.service.S3Uploader;
import com.produce.pickmeup.service.UserService;
import java.io.File;
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
	private final List<String> INTERNAL_ERROR_LIST = ErrorCase.getInternalErrorList();
	private final List<String> REQUEST_ERROR_LIST = ErrorCase.getRequestErrorList();
	private final UserService userService;
	private final S3Uploader uploaderService;

	@PostMapping("/login")
	public ResponseEntity<Object> loginController(@RequestBody LoginRequestDto loginRequestDto) {
		if (loginRequestDto.getEmail() == null || loginRequestDto.getUsername() == null) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.INVALID_FIELD_ERROR));
		}
		return ResponseEntity.ok().body(userService.login(loginRequestDto));
	}

	@PatchMapping("/users/{id}/image")
	public ResponseEntity<Object> updateUserImage(
		@RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {
		Optional<User> user = userService.findById(id);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER_ERROR));
		}
		File convertedFile = uploaderService.convert(multipartFile);
		if (convertedFile == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					ErrorCase.FAIL_FILE_CONVERT_ERROR));
		}
		if (!uploaderService.isValidExtension(convertedFile)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.INVALID_FILE_TYPE_ERROR));
		}
		String result = userService.updateUserImage(convertedFile, id, user.get());
		return ResponseEntity.created(URI.create(result)).build();
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getUser(@PathVariable Long id) {
		Optional<User> optionalUser = userService.findById(id);
		return optionalUser.<ResponseEntity<Object>>map(
			user -> ResponseEntity.ok().body(user.toUserInfoDto()))
			.orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_USER_ERROR)));
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@RequestBody UserUpdateDto userUpdateDto,
		@PathVariable Long id) {
		if (userUpdateDto.getUsername() == null) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.INVALID_FIELD_ERROR));
		}
		Optional<User> optionalUser = userService.findById(id);
		if (!optionalUser.isPresent()) {
			return ResponseEntity.badRequest()
				.body(
					new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER_ERROR));
		}
		userService.updateUserInfo(optionalUser.get(), userUpdateDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/users/{id}/projects")
	public ResponseEntity<Object> getUserProjects(@PathVariable Long id) {
		Optional<User> optionalUser = userService.findById(id);
		return optionalUser.<ResponseEntity<Object>>map(
			user -> ResponseEntity.ok().body(userService.getUserProjects(user)))
			.orElseGet(() -> ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_USER_ERROR)));
	}

	@GetMapping("/users/{id}/portfolios")
	public ResponseEntity<Object> getUserPortfolios(@PathVariable Long id) {
		Optional<User> optionalUser = userService.findById(id);
		return optionalUser.<ResponseEntity<Object>>map(
			user -> ResponseEntity.ok().body(userService.getUserPortfolios(user)))
			.orElseGet(() -> ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_USER_ERROR)));
	}
}
