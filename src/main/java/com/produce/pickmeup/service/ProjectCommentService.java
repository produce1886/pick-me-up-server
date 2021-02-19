package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentDetailResponseDto;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRepository;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRequestDto;
import com.produce.pickmeup.domain.tag.ProjectHasTag;
import com.produce.pickmeup.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectCommentService {
	private final int COMMENT_SCORE = 2;
	private final ProjectCommentRepository projectCommentRepository;

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
		project.getProjectTags().stream().map(ProjectHasTag::getProjectTag)
			.forEach((tag) -> tag.upCurrentScore(COMMENT_SCORE));
		return String.valueOf(result);
	}

	public Optional<ProjectComment> getProjectComment(Long projectCommentId) {
		return projectCommentRepository.findById(projectCommentId);
	}

	public ProjectCommentDetailResponseDto getCommentDetail(ProjectComment comment) {
		return comment.toDetailResponseDto();
	}

	public boolean checkProjectCommentAuthorEmail(ProjectComment comment, String authorEmail) {
		return comment.getAuthorEmail().equals(authorEmail);
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
