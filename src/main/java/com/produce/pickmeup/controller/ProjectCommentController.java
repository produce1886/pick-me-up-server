package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.service.ProjectCommentService;
import com.produce.pickmeup.service.ProjectService;
import com.produce.pickmeup.service.UserService;
import java.net.URI;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class ProjectCommentController {
	private final ProjectService projectService;
	private final ProjectCommentService projectCommentService;
	private final UserService userService;

	@PostMapping("/projects/{id}/comments")
	public ResponseEntity<Object> addProjectComment(@PathVariable Long id,
		@RequestBody ProjectCommentRequestDto projectCommentRequestDto) {
		Optional<User> author = userService.findByEmail(projectCommentRequestDto.getEmail());
		if (!author.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER_ERROR));
		}
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PROJECT_ERROR));
		}
		String result = projectCommentService
			.addProjectComment(author.get(), project.get(), projectCommentRequestDto);
		return ResponseEntity.created(URI.create("/projects/" + id + "/comments" + result)).build();
	}

	@GetMapping("/projects/{id}/comments/{commentId}")
	public ResponseEntity<Object> getProjectComment(@PathVariable Long id,
		@PathVariable Long commentId) {
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PROJECT_ERROR));
		}
		Optional<ProjectComment> projectComment = projectCommentService
			.getProjectComment(commentId);
		if (!projectComment.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
		}
		if (!projectCommentService.isLinked(projectComment.get(), id)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
		}
		return ResponseEntity.ok(projectCommentService.getCommentDetail(projectComment.get()));
	}

	@PutMapping("/projects/{id}/comments/{commentId}")
	public ResponseEntity<Object> updateProjectComment(@PathVariable Long id,
		@PathVariable Long commentId,
		@RequestBody ProjectCommentRequestDto projectCommentRequestDto) {
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PROJECT_ERROR));
		}
		Optional<ProjectComment> projectComment = projectCommentService
			.getProjectComment(commentId);
		if (!projectComment.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
		}
		if (!projectCommentService.isLinked(projectComment.get(), project.get().getId())) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
		}
		projectCommentService.updateProjectComment(projectComment.get(), projectCommentRequestDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/projects/{id}/comments/{commentId}")
	public ResponseEntity<Object> deleteProjectComment(@PathVariable Long id,
		@PathVariable Long commentId) {
		Optional<Project> project = projectService.getProject(id);
		if (!project.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PROJECT_ERROR));
		}
		Optional<ProjectComment> projectComment = projectCommentService
			.getProjectComment(commentId);
		if (!projectComment.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
		}
		if (!projectCommentService.isLinked(projectComment.get(), project.get().getId())) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
		}
		projectCommentService.deleteCommentDetail(commentId);
		return ResponseEntity.noContent().build();
	}
}
