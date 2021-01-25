package com.produce.pickmeup.service;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectRepository;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRepository;
import com.produce.pickmeup.domain.project.comment.ProjectCommentRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
			return ErrorCase.NO_SUCH_USER;
		}
		if (!project.isPresent()) {
			return ErrorCase.NO_SUCH_PROJECT;
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
}
