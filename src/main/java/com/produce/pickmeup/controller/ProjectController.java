package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.project.ProjectRequestDto;
import com.produce.pickmeup.service.ProjectService;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class ProjectController {
	private final List<String> INTERNAL_ERROR_LIST = ErrorCase.getInternalErrorList();
	private final List<String> REQUEST_ERROR_LIST = ErrorCase.getRequestErrorList();
	private final ProjectService projectService;

	@PostMapping("/project")
	public ResponseEntity<Object> addProject(@RequestBody ProjectRequestDto projectRequestDto) {
		if (isRequestBodyValid(projectRequestDto)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVALID_FIELD_ERROR)
			);
		}
		String result = projectService.addProject(projectRequestDto);
		if (INTERNAL_ERROR_LIST.contains(result)) {
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), result));
		}
		if (REQUEST_ERROR_LIST.contains(result)) {
			return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), result));
		}
		return ResponseEntity.created(URI.create("/project/" + result)).build();
	}

	private boolean isRequestBodyValid(ProjectRequestDto projectRequestDto) {
		return projectRequestDto.getAuthorEmail() != null &&
			projectRequestDto.getTitle() != null &&
			projectRequestDto.getContent() != null &&
			projectRequestDto.getCategory() != null &&
			projectRequestDto.getRecruitmentField() != null &&
			projectRequestDto.getProjectSection() != null &&
			projectRequestDto.getRegion() != null &&
			projectRequestDto.getTags() != null;
	}
}
