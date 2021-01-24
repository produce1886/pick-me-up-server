package com.produce.pickmeup.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectDetailResponseDto;
import com.produce.pickmeup.domain.project.ProjectRepository;
import com.produce.pickmeup.domain.project.ProjectRequestDto;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentResponseDto;
import com.produce.pickmeup.domain.tag.ProjectHasTag;
import com.produce.pickmeup.domain.tag.ProjectHasTagRepository;
import com.produce.pickmeup.domain.tag.Tag;
import com.produce.pickmeup.domain.tag.TagDto;
import com.produce.pickmeup.domain.tag.TagRepository;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class ProjectService {
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;
	private final TagRepository tagRepository;
	private final ProjectHasTagRepository relationRepository;

	@Transactional
	public String addProject(ProjectRequestDto projectRequestDto) {
		Optional<User> author = userRepository.findByEmail(projectRequestDto.getAuthorEmail());
		if (!author.isPresent()) {
			return ErrorCase.NO_SUCH_USER;
		}
		long result = projectRepository.save(
			Project.builder()
				.author(author.get())
				.title(projectRequestDto.getTitle())
				.content(projectRequestDto.getContent())
				.category(projectRequestDto.getCategory())
				.recruitmentField(projectRequestDto.getRecruitmentField())
				.region(projectRequestDto.getRegion())
				.projectSection(projectRequestDto.getProjectSection())
				.image(projectRequestDto.getImage())
				.build())
			.getId();
		if (!projectConnectTags(projectRequestDto.getProjectTags(), result)) {
			return ErrorCase.FAIL_TAG_SAVE_ERROR;
		}
		return String.valueOf(result);
	}

	@Transactional
	public boolean projectConnectTags(List<String> projectTags, long projectId) {
		Optional<Project> savedProject = projectRepository.findById(projectId);
		if (!savedProject.isPresent()) {
			return false;
		}
		for (String tagName : projectTags) {
			Tag tag = tagRepository.findByTagName(tagName)
				.orElseGet(() -> addProjectTag(tagName));
			relationRepository.save(
				ProjectHasTag.builder()
					.project(savedProject.get())
					.tag(tag).build()
			);
		}
		return true;
	}

	@Transactional
	public Tag addProjectTag(String tagName) {
		return tagRepository.save(
			Tag.builder().tagName(tagName).build());
	}

	@Transactional
	public List<TagDto> getProjectTagNames(Project project) {
		List<ProjectHasTag> relations = relationRepository.findByProject(project);
		if (relations.isEmpty()) {
			return Collections.emptyList();
		}
		return relations.stream().map(ProjectHasTag::getProjectTag)
			.map(Tag::toTagDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public Optional<Project> getProject(Long projectId) {
		return projectRepository.findById(projectId);
	}

	@Transactional
	public ProjectDetailResponseDto getProjectDetail(Project project) {
		project.upViewNum();
		List<ProjectHasTag> relations = project.getProjectTags();
		List<ProjectCommentResponseDto> comments = project.getProjectComments()
			.stream().map(ProjectComment::toResponseDto).collect(Collectors.toList());
		if (relations.isEmpty()) {
			return project.toDetailResponseDto(Collections.emptyList(), comments);
		}
		List<TagDto> projectTags = relations.stream()
			.map(ProjectHasTag::getProjectTag)
			.map(Tag::toTagDto).collect(Collectors.toList());
		return project.toDetailResponseDto(projectTags, comments);
	}
}
