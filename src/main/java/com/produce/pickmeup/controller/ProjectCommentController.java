package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRequestDto;
import com.produce.pickmeup.service.ProjectCommentService;
import com.produce.pickmeup.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class ProjectCommentController {
	private final List<String> REQUEST_ERROR_LIST = ErrorCase.getRequestErrorList();
	private final ProjectService projectService;
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

	@GetMapping("/projects/{id}/comments/{commentId}")
	public ResponseEntity<Object> getProjectComment(@PathVariable Long id, @PathVariable Long commentId) {
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent())
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PROJECT_ERROR));
		Optional<ProjectComment> projectComment = projectCommentService.getProjectComment(commentId);
		if (!projectComment.isPresent())
			return ResponseEntity.badRequest().body(
			new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
		boolean isLinked = projectCommentService.isLinked(projectComment.get(), id);
		if (!isLinked)
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
		return ResponseEntity.ok(projectCommentService.getCommentDetail(projectComment.get()));
	}
}
