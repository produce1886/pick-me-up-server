package com.produce.pickmeup.controller;

import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.error.exception.InvalidAccessException;
import com.produce.pickmeup.error.exception.NoCommentException;
import com.produce.pickmeup.error.exception.NoProjectException;
import com.produce.pickmeup.error.exception.NoUserException;
import com.produce.pickmeup.service.ProjectCommentService;
import com.produce.pickmeup.service.ProjectService;
import com.produce.pickmeup.service.UserService;
import java.net.URI;
import lombok.AllArgsConstructor;
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

    User author = userService.findByEmail(projectCommentRequestDto.getAuthorEmail())
        .orElseThrow(NoUserException::new);
    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);

    String result = projectCommentService.addProjectComment(author, project, projectCommentRequestDto);
    return ResponseEntity.created(URI.create("/projects/" + id + "/comments/" + result)).build();
  }

  @GetMapping("/projects/{id}/comments/{commentId}")
  public ResponseEntity<Object> getProjectComment(@PathVariable Long id, @PathVariable Long commentId) {

    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);
    ProjectComment comment = projectCommentService.getProjectComment(commentId)
        .orElseThrow(NoCommentException::new);
    if (!comment.included(project.getId())) {
      throw new InvalidAccessException();
    }

    return ResponseEntity.ok(projectCommentService.getCommentDetail(comment));
  }

  @PutMapping("/projects/{id}/comments/{commentId}")
  public ResponseEntity<Object> updateProjectComment(
      @PathVariable Long id, @PathVariable Long commentId,
      @RequestBody ProjectCommentRequestDto projectCommentRequestDto) {

    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);
    ProjectComment comment = projectCommentService.getProjectComment(commentId)
        .orElseThrow(NoCommentException::new);
    if (!comment.included(project.getId()) |
        !comment.authorCheck(projectCommentRequestDto.getAuthorEmail())) {
      throw new InvalidAccessException();
    }

    projectCommentService.updateProjectComment(comment, projectCommentRequestDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/projects/{id}/comments/{commentId}")
  public ResponseEntity<Object> deleteProjectComment(@PathVariable Long id, @PathVariable Long commentId) {

    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);
    ProjectComment comment = projectCommentService.getProjectComment(commentId)
        .orElseThrow(NoCommentException::new);
    if (!comment.included(project.getId())) {
      throw new InvalidAccessException();
    }

    projectCommentService.deleteCommentDetail(commentId);
    project.downCommentsNum();
    return ResponseEntity.noContent().build();
  }
}
