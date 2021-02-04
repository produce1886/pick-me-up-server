package com.produce.pickmeup.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectDetailResponseDto;
import com.produce.pickmeup.domain.project.ProjectDto;
import com.produce.pickmeup.domain.project.ProjectListResponseDto;
import com.produce.pickmeup.domain.project.ProjectRepository;
import com.produce.pickmeup.domain.project.ProjectRequestDto;
import com.produce.pickmeup.domain.project.ProjectSpecification;
import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.project.comment.ProjectCommentResponseDto;
import com.produce.pickmeup.domain.tag.ProjectHasTag;
import com.produce.pickmeup.domain.tag.ProjectHasTagRepository;
import com.produce.pickmeup.domain.tag.Tag;
import com.produce.pickmeup.domain.tag.TagDto;
import com.produce.pickmeup.domain.tag.TagRepository;
import com.produce.pickmeup.domain.user.User;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class ProjectService {
	private final String PROJECT_IMAGE_PATH = "project-image";

	private final ProjectRepository projectRepository;
	private final TagRepository tagRepository;
	private final ProjectHasTagRepository relationRepository;
	private final S3Uploader s3Uploader;


	@Transactional
	public String addProject(ProjectRequestDto projectRequestDto, User author) {
		Project project = projectRepository.save(
			Project.builder()
				.author(author)
				.title(projectRequestDto.getTitle())
				.content(projectRequestDto.getContent())
				.category(projectRequestDto.getCategory())
				.recruitmentField(projectRequestDto.getRecruitmentField())
				.region(projectRequestDto.getRegion())
				.projectSection(projectRequestDto.getProjectSection())
				.image(projectRequestDto.getImage())
				.build());
		projectConnectTags(projectRequestDto.getProjectTags(), project);
		return String.valueOf(project.getId());
	}

	@Transactional
	public void projectConnectTags(List<String> projectTags, Project savedProject) {
		for (String tagName : projectTags) {
			Tag tag = tagRepository.findByTagName(tagName)
				.orElseGet(() -> addProjectTag(tagName));
			relationRepository.save(
				ProjectHasTag.builder()
					.project(savedProject)
					.tag(tag).build()
			);
		}
	}

	@Transactional
	public Tag addProjectTag(String tagName) {
		return tagRepository.save(
			Tag.builder().tagName(tagName).build());
	}

	public List<TagDto> getProjectTagNames(Project project) {
		List<ProjectHasTag> relations = relationRepository.findByProject(project);
		if (relations.isEmpty()) {
			return Collections.emptyList();
		}
		return relations.stream().map(ProjectHasTag::getProjectTag)
			.map(Tag::toTagDto)
			.collect(Collectors.toList());
	}

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

	@Transactional
	public String updateProjectImage(File convertedFile, Project project) {
		String result = s3Uploader
			.upload(convertedFile, PROJECT_IMAGE_PATH, String.valueOf(project.getId()));
		project.updateImage(result);
		return result;
	}

	@Transactional
	public void deleteProjectImage(Project project) {
		s3Uploader.delete(PROJECT_IMAGE_PATH, String.valueOf(project.getId()));
		project.updateImage("");
	}

	public boolean checkProjectAuthorEmail(Project project, String authorEmail) {
		return project.getAuthorEmail().equals(authorEmail);
	}

	@Transactional
	public void deleteProjectTagRelations(Project project, List<String> disconnectTagNames) {
		disconnectTagNames.forEach(value ->
			tagRepository.findByTagName(value).ifPresent(tag ->
				relationRepository.deleteByProjectAndProjectTag(project, tag))
		);
	}

	@Transactional
	public void updateProject(Project project, ProjectRequestDto requestDto) {
		project.updateExceptTags(requestDto);

		List<String> originalTagNames = getProjectTagNames(project).stream().map(TagDto::getTagName)
			.collect(Collectors.toList());
		List<String> newTagNames = requestDto.getProjectTags();
		List<String> disconnectTagNames = new ArrayList<>();
		for (String tagName : originalTagNames) {
			if (!newTagNames.contains(tagName)) {
				disconnectTagNames.add(tagName);
			}
		}
		newTagNames.removeIf(originalTagNames::contains); //new
		deleteProjectTagRelations(project, disconnectTagNames);
		projectConnectTags(newTagNames, project);
	}

	@Transactional
	public void deleteProject(Project project) {
		projectRepository.delete(project);
	}

	@Transactional
	public ProjectListResponseDto getProjectsList(Pageable pageable, String category,
		String recruitmentField, String region, String projectSection, String keyword) {
		Specification<Project> specification = Specification.where(null);
		if (category != null && !category.isEmpty()) {
			specification = specification
				.and(Specification.where(ProjectSpecification.byCategory(category)));
		}
		if (recruitmentField != null && !recruitmentField.isEmpty()) {
			specification = specification.and(
				Specification.where(ProjectSpecification.byRecruitmentField(recruitmentField)));
		}
		if (region != null && !region.isEmpty()) {
			specification = specification
				.and(Specification.where(ProjectSpecification.byRegion(region)));
		}
		if (projectSection != null && !projectSection.isEmpty()) {
			specification = specification
				.and(Specification.where(ProjectSpecification.byProjectSection(projectSection)));
		}
		if (keyword != null && !keyword.isEmpty()) {
			specification = specification
				.and(Specification.where(ProjectSpecification.byKeyword(keyword)));
		}
		return pageToListResponseDto(projectRepository.findAll(specification, pageable));
	}

	private ProjectListResponseDto pageToListResponseDto(Page<Project> pages) {
		List<ProjectDto> projectDtoList = new ArrayList<>();
		for (Project project : pages) {
			projectDtoList.add(project.toProjectDto(getProjectTagNames(project)));
		}
		return ProjectListResponseDto.builder()
			.totalNum((int) pages.getTotalElements())
			.projectList(projectDtoList)
			.build();
	}
}
