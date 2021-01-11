package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import com.produce.pickmeup.domain.user.UserRequestDto;
import com.produce.pickmeup.domain.user.UserResponseDto;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	private User addUser(UserRequestDto userRequestDto) {
		return userRepository.save(userRequestDto.toEntity());
	}

	@Transactional
	public UserResponseDto login(UserRequestDto userRequestDto) {
		User user = userRepository.findByEmail(userRequestDto.getEmail())
			.orElseGet(() -> addUser(userRequestDto));
		return user.toResponseDto();
	}
}
