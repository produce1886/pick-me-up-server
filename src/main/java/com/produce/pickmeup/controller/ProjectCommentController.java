package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRequestDto;
import com.produce.pickmeup.service.ProjectCommentService;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class ProjectCommentController {
	private final List<String> REQUEST_ERROR_LIST = ErrorCase.getRequestErrorList();
	private final ProjectCommentService projectCommentService;

	@PostMapping("/projects/{id}/comments")
	public ResponseEntity<Object> addProjectComment(@PathVariable Long id,
		@RequestBody ProjectCommentRequestDto projectCommentRequestDto) {
		String result = projectCommentService.addProjectComment(projectCommentRequestDto, id);
		if (REQUEST_ERROR_LIST.contains(result)) {
			return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), result));
		}
		return ResponseEntity.created(URI.create("/projects/" + id + "/comments" + result)).build();
	}
}
