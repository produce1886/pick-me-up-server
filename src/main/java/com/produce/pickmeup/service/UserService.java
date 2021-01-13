package com.produce.pickmeup.service;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.user.*;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UserService {
	private final String PROFILE_IMAGE_PATH = "profile-image";
	private final List<String> ERROR_LIST = ErrorCase.getErrorList();

	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;

	private User addUser(UserRequestDto userRequestDto) {
		return userRepository.save(userRequestDto.toEntity());
	}

	@Transactional
	public UserResponseDto login(UserRequestDto userRequestDto) {
		User user = userRepository.findByEmail(userRequestDto.getEmail())
			.orElseGet(() -> addUser(userRequestDto));
		return user.toResponseDto();
	}

	@Transactional
	public String updateUserImage(MultipartFile multipartFile, Long id) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			return ErrorCase.NO_SUCH_USER;
		}
		String result = s3Uploader.upload(multipartFile, PROFILE_IMAGE_PATH, id.toString());
		if (ERROR_LIST.contains(result)) {
			return result;
		}
		user.get().updateImage(result);
		return result;
	}

	@Transactional
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional
	public void updateUserInfo(User user, UserInfoDto userInfo) {
		user.updateInfo(userInfo);
	}
}
