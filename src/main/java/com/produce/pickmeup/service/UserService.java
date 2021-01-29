package com.produce.pickmeup.service;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.login.LoginRequestDto;
import com.produce.pickmeup.domain.login.LoginResponseDto;
import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.PortfolioDto;
import com.produce.pickmeup.domain.portfolio.PortfolioListResponseDto;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectDto;
import com.produce.pickmeup.domain.project.ProjectListResponseDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import com.produce.pickmeup.domain.user.UserUpdateDto;
import java.io.File;
import java.util.ArrayList;
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
	private final List<String> ERROR_LIST = ErrorCase.getAllErrorList();

	private final PortfolioService portfolioService;
	private final ProjectService projectService;
	private final UserRepository userRepository;
	private final S3Uploader s3Uploader;

	private User addUser(LoginRequestDto userRequestDto) {
		return userRepository.save(userRequestDto.toEntity());
	}

	@Transactional
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		User user = userRepository.findByEmail(loginRequestDto.getEmail())
			.orElseGet(() -> addUser(loginRequestDto));
		return user.toResponseDto();
	}

	@Transactional
	public String updateUserImage(File convertedFile, Long id, User user) {
		String result = s3Uploader.upload(convertedFile, PROFILE_IMAGE_PATH, id.toString());
		user.updateImage(result);
		return result;
	}

	@Transactional
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Transactional
	public void updateUserInfo(User user, UserUpdateDto userUpdateDto) {
		user.updateInfo(userUpdateDto);
	}

	@Transactional
	public ProjectListResponseDto getUserProjects(User user) {
		List<Project> projects = user.getProjectList();
		List<ProjectDto> projectDtoList = new ArrayList<>();
		for (Project project : projects) {
			projectDtoList.add(project.toProjectDto(
				projectService.getProjectTagNames(project)));
		}
		return ProjectListResponseDto.builder()
			.totalNum(projects.size())
			.projectList(projectDtoList)
			.build();
	}

	@Transactional
	public PortfolioListResponseDto getUserPortfolios(User user) {
		List<Portfolio> portfolios = user.getPortfolioList();
		List<PortfolioDto> portfolioDtoList = new ArrayList<>();
		for (Portfolio portfolio : portfolios) {
			portfolioDtoList.add(portfolio.toPortfolioDto(
				portfolioService.getPortfolioTagNames(portfolio)));
		}
		return PortfolioListResponseDto.builder()
			.totalNum(portfolios.size())
			.portfolioList(portfolioDtoList)
			.build();
	}
}
