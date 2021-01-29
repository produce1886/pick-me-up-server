package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectRepository;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentDetailResponseDto;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRepository;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRequestDto;
import com.produce.pickmeup.domain.user.User;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProjectCommentService {
	private final ProjectCommentRepository projectCommentRepository;
	private final ProjectRepository projectRepository;

	@Transactional
	public String addProjectComment(User author, Project project,
		ProjectCommentRequestDto responseDto) {
		long result = projectCommentRepository.save(
			ProjectComment.builder()
				.author(author)
				.content(responseDto.getContent())
				.project(project)
				.build())
			.getId();
		project.upCommentsNum();
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

	@Transactional
	public void deleteCommentDetail(Long projectCommentId) {
		projectCommentRepository.deleteById(projectCommentId);
	}

	@Transactional
	public void updateProjectComment(ProjectComment projectComment,
		ProjectCommentRequestDto projectCommentRequestDto) {
		projectComment.updateContent(projectCommentRequestDto);
	}
}
