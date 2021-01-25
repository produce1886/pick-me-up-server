package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.service.ProjectService;
import com.produce.pickmeup.service.UserService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class ProjectController {
	private final List<String> INTERNAL_ERROR_LIST = ErrorCase.getInternalErrorList();
	private final List<String> REQUEST_ERROR_LIST = ErrorCase.getRequestErrorList();
	private final ProjectService projectService;
	private final UserService userService;

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
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER)
			);
		}
		String result = projectService.addProject(projectRequestDto, author.get());
		if (INTERNAL_ERROR_LIST.contains(result)) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), result));
		}
		return ResponseEntity.created(URI.create("/projects/" + result)).build();
	}

	@GetMapping("/projects/{id}")
	public ResponseEntity<Object> getProjectDetail(@PathVariable Long id) {
		Optional<Project> project = projectService.getProject(id);
		return project.<ResponseEntity<Object>>map(
			value -> ResponseEntity.ok(projectService.getProjectDetail(value)))
			.orElseGet(() -> ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PROJECT)));
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
