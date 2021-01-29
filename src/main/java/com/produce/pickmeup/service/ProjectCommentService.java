package com.produce.pickmeup.service;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectRepository;
import com.produce.pickmeup.domain.project.comment.*;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectCommentService {
	private final UserRepository userRepository;
	private final ProjectCommentRepository projectCommentRepository;
	private final ProjectRepository projectRepository;

	@Transactional
	public String addProjectComment(ProjectCommentRequestDto projectCommentRequestDto, Long projectId) {
		Optional<User> author = userRepository.findByEmail(projectCommentRequestDto.getEmail());
		Optional<Project> project = projectRepository.findById(projectId);
		if (!author.isPresent()) {
			return ErrorCase.NO_SUCH_USER_ERROR;
		}
		if (!project.isPresent()) {
			return ErrorCase.NO_SUCH_PROJECT_ERROR;
		}
		long result = projectCommentRepository.save(
			ProjectComment.builder()
				.author(author.get())
				.content(projectCommentRequestDto.getContent())
				.project(project.get())
				.build())
			.getId();
		project.get().upCommentsNum();
		return String.valueOf(result);
	}

	public Optional<ProjectComment> getProjectComment(Long projectCommentId) {
		return projectCommentRepository.findById(projectCommentId);
	}

	public ProjectCommentDetailResponseDto getCommentDetail(ProjectComment comment) {
		return comment.toDetailResponseDto();
	}

	public boolean isLinked(ProjectComment projectComment, Long projectId) {
		return projectComment.getProject().getId() == projectId;
	}

	public String isBadRequest(Long projectId, Long projectCommentId){
		Optional<Project> project = projectRepository.findById(projectId);
		if (!project.isPresent())
			return ErrorCase.NO_SUCH_PROJECT_ERROR;
		Optional<ProjectComment> projectComment = projectCommentRepository.findById(projectCommentId);
		if (!projectComment.isPresent())
			return ErrorCase.NO_SUCH_COMMENT_ERROR;
		if (!isLinked(projectComment.get(), projectId))
			return ErrorCase.BAD_REQUEST_ERROR;
		return "SUCCESS";
	}

	@Transactional
	public void deleteCommentDetail(Long projectCommentId) {
		projectCommentRepository.deleteById(projectCommentId);
	}

	@Transactional
	public String updateProjectComment(Long projectId, Long projectCommentId,
		ProjectCommentRequestDto projectCommentRequestDto) {
		Optional<Project> project = projectRepository.findById(projectId);
		if (!project.isPresent())
			return ErrorCase.NO_SUCH_PROJECT_ERROR;
		Optional<ProjectComment> projectComment = projectCommentRepository.findById(projectCommentId);
		if (!projectComment.isPresent())
			return ErrorCase.NO_SUCH_COMMENT_ERROR;
		if (!isLinked(projectComment.get(), projectId))
			return ErrorCase.BAD_REQUEST_ERROR;
		projectComment.get().updateContent(projectCommentRequestDto);
		return "SUCCESS";
	}
}
