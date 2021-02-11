package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.service.ProjectService;
import com.produce.pickmeup.service.S3Uploader;
import com.produce.pickmeup.service.UserService;
import java.io.File;
import java.net.URI;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
public class ProjectController {
	private final ProjectService projectService;
	private final UserService userService;
	private final S3Uploader uploaderService;

	@PostMapping("/projects")
	public ResponseEntity<Object> addProject(@RequestBody ProjectRequestDto projectRequestDto) {
		if (!isRequestBodyValid(projectRequestDto)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVALID_FIELD_ERROR)
			);
		}
		Optional<User> author = userService.findByEmail(projectRequestDto.getAuthorEmail());
		if (!author.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER_ERROR)
			);
		}
		String result = projectService.addProject(projectRequestDto, author.get());
		return ResponseEntity.created(URI.create("/projects/" + result)).build();
	}

	@GetMapping("/projects/{id}")
	public ResponseEntity<Object> getProjectDetail(@PathVariable Long id) {
		Optional<Project> project = projectService.getProject(id);
		return project.<ResponseEntity<Object>>map(
			value -> ResponseEntity.ok(projectService.getProjectDetail(value)))
			.orElseGet(() -> ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PROJECT_ERROR)));
	}

	@PatchMapping("/projects/{id}/image")
	public ResponseEntity<Object> updateProjectImage(
		@RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent()) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_PROJECT_ERROR));
		}
		if (multipartFile.isEmpty()) {
			projectService.deleteProjectImage(project.get());
			return ResponseEntity.noContent().build();
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
		String result = projectService.updateProjectImage(convertedFile, project.get());
		return ResponseEntity.created(URI.create(result)).build();
	}

	@PutMapping("/projects/{id}")
	public ResponseEntity<Object> updateProject(@PathVariable Long id,
		@RequestBody ProjectRequestDto projectRequestDto) {
		if (!isRequestBodyValid(projectRequestDto)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVALID_FIELD_ERROR)
			);
		}
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent()) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_PROJECT_ERROR));
		}
		if (!projectService.checkProjectAuthorEmail(
			project.get(), projectRequestDto.getAuthorEmail())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), ErrorCase.FORBIDDEN_ERROR));
		}
		projectService.updateProject(project.get(), projectRequestDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/projects/{id}")
	public ResponseEntity<Object> deleteProject(@PathVariable Long id) {
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent()) {
			return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
				ErrorCase.NO_SUCH_PROJECT_ERROR));
		}
		projectService.deleteProject(project.get());
		projectService.deleteProjectImage(project.get());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/projects/list")
	public ResponseEntity<Object> getProjectsList(final Pageable pageable,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) String recruitmentField,
		@RequestParam(required = false) String region,
		@RequestParam(required = false) String projectSection,
		@RequestParam(required = false) String keyword) {
		return ResponseEntity.ok(
			projectService.getProjectsList(pageable, category, recruitmentField, region,
				projectSection, keyword));
	}

	private boolean isRequestBodyValid(ProjectRequestDto projectRequestDto) {
		return projectRequestDto.getAuthorEmail() != null &&
			projectRequestDto.getTitle() != null &&
			projectRequestDto.getContent() != null &&
			projectRequestDto.getCategory() != null &&
			projectRequestDto.getRecruitmentField() != null &&
			projectRequestDto.getProjectSection() != null &&
			projectRequestDto.getRegion() != null &&
			projectRequestDto.getProjectTags() != null;
	}
}
