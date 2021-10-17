package com.produce.pickmeup.controller;


import com.produce.pickmeup.domain.login.LoginRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserUpdateDto;
import com.produce.pickmeup.error.exception.FileConvertException;
import com.produce.pickmeup.error.exception.InvalidFieldException;
import com.produce.pickmeup.error.exception.InvalidFileException;
import com.produce.pickmeup.error.exception.NoUserException;
import com.produce.pickmeup.service.S3Uploader;
import com.produce.pickmeup.service.UserService;
import java.io.File;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private final UserService userService;
  private final S3Uploader uploaderService;

  @PostMapping("/login")
  public ResponseEntity<?> loginController(@RequestBody LoginRequestDto loginRequestDto) {
    if (loginRequestDto.getEmail() == null || loginRequestDto.getUsername() == null) {
      throw new InvalidFieldException();
    }
    return ResponseEntity.ok().body(userService.login(loginRequestDto));
  }

  @PatchMapping("/users/{id}/image")
  public ResponseEntity<?> updateUserImage(
      @RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {
    User user = userService.findById(id)
        .orElseThrow(NoUserException::new);
    if (multipartFile.isEmpty()) {
      throw new InvalidFieldException();
    }

    File convertedFile = uploaderService.convert(multipartFile);
    if (convertedFile == null) {
      throw new FileConvertException();
    }
    if (!uploaderService.isValidExtension(convertedFile)) {
      throw new InvalidFileException();
    }

    String result = userService.updateUserImage(convertedFile, user);
    return ResponseEntity.created(URI.create(result)).build();
  }

  @DeleteMapping("/users/{id}/image")
  public ResponseEntity<?> deleteUserImage(@PathVariable Long id) {
    User user = userService.findById(id)
        .orElseThrow(NoUserException::new);

    userService.deleteUserImage(user);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<?> getUser(@PathVariable Long id) {
    User user = userService.findById(id)
        .orElseThrow(NoUserException::new);
    return ResponseEntity.ok(user.toUserInfoDto());
  }

  @PutMapping("/users/{id}")
  public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto,
      @PathVariable Long id) {
    if (userUpdateDto.getUsername() == null) {
      throw new InvalidFieldException();
    }
    User user = userService.findById(id)
        .orElseThrow(NoUserException::new);

    userService.updateUserInfo(user, userUpdateDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/users/{id}/projects")
  public ResponseEntity<?> getUserProjects(@PathVariable Long id) {
    User user = userService.findById(id)
        .orElseThrow(NoUserException::new);
    return ResponseEntity.ok(userService.getUserProjects(user));
  }

  @GetMapping("/users/{id}/portfolios")
  public ResponseEntity<?> getUserPortfolios(@PathVariable Long id) {
    User user = userService.findById(id)
        .orElseThrow(NoUserException::new);
    return ResponseEntity.ok(userService.getUserPortfolios(user));
  }
}
