package com.produce.pickmeup.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectRepository;
import com.produce.pickmeup.domain.project.ProjectRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class ProjectService {
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;

	@Transactional
	public String addProject(ProjectRequestDto projectRequestDto) {
		//TODO: field check required
		Optional<User> author = userRepository.findByEmail(projectRequestDto.getAuthorEmail());
		if (!author.isPresent()) {
			return ErrorCase.NO_SUCH_USER;
		}
		long result = projectRepository.save(
			Project.builder()
				.authorEmail(projectRequestDto.getAuthorEmail())
				.author(author.get())
				.title(projectRequestDto.getTitle())
				.content(projectRequestDto.getContent())
				.category(projectRequestDto.getCategory())
				.recruitmentField(projectRequestDto.getRecruitmentField())
				.region(projectRequestDto.getRegion())
				.projectSection(projectRequestDto.getProjectSection())
				.build())
			.getId();
		return String.valueOf(result);
	}
}
