package com.produce.pickmeup.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectDetailResponseDto;
import com.produce.pickmeup.domain.project.ProjectDto;
import com.produce.pickmeup.domain.project.ProjectListResponseDto;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class ProjectService {
	private final String PROJECT_IMAGE_PATH = "project-image";
	private final List<String> ERROR_LIST = ErrorCase.getAllErrorList();

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
	public String updateProjectImage(MultipartFile multipartFile, Project project) {
		String result = s3Uploader.upload(multipartFile, PROJECT_IMAGE_PATH,
			String.valueOf(project.getId()));
		if (ERROR_LIST.contains(result)) {
			return result;
		}
		project.updateImage(result);
		return result;
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

	public ProjectListResponseDto getProjectsList(Pageable pageable, String category, String recruitmentField, String region, String projectSection, String keyword) {
		if (category.isEmpty() && recruitmentField.isEmpty() && region.isEmpty() && projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAll(pageable));

		if (recruitmentField.isEmpty() && region.isEmpty() && projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategory(category, pageable));
		if (category.isEmpty() && region.isEmpty() && projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentField(recruitmentField, pageable));
		if (category.isEmpty() && recruitmentField.isEmpty() && projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRegion(region, pageable));
		if (category.isEmpty() && recruitmentField.isEmpty() && region.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByProjectSection(projectSection, pageable));
		if (category.isEmpty() && recruitmentField.isEmpty() && region.isEmpty() && projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByContentContaining(keyword, pageable));

		if (region.isEmpty() && projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentField(category, recruitmentField, pageable));
		if (recruitmentField.isEmpty() && projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRegion(category, region, pageable));
		if (recruitmentField.isEmpty() && region.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndProjectSection(category, projectSection, pageable));
		if (recruitmentField.isEmpty() && region.isEmpty() && projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndContentContaining(category, keyword, pageable));
		if (category.isEmpty() && projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentFieldAndRegion(recruitmentField, region, pageable));
		if (category.isEmpty() && region.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentFieldAndProjectSection(recruitmentField, projectSection, pageable));
		if (category.isEmpty() && region.isEmpty() && projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentFieldAndContentContaining(recruitmentField, keyword, pageable));
		if (category.isEmpty() && recruitmentField.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRegionAndProjectSection(region, projectSection, pageable));
		if (category.isEmpty() && recruitmentField.isEmpty() && projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRegionAndContentContaining(region, keyword, pageable));
		if (category.isEmpty() && recruitmentField.isEmpty() && region.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByProjectSectionAndContentContaining(projectSection, keyword, pageable));

		if (projectSection.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentFieldAndRegion(category, recruitmentField, region, pageable));
		if (region.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentFieldAndProjectSection(category, recruitmentField, projectSection, pageable));
		if (region.isEmpty() && projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentFieldAndContentContaining(category, recruitmentField, keyword, pageable));
		if (recruitmentField.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRegionAndProjectSection(category, region, projectSection, pageable));
		if (recruitmentField.isEmpty() && projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRegionAndContentContaining(category, region, keyword, pageable));
		if (recruitmentField.isEmpty() && region.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndProjectSectionAndContentContaining(category, projectSection, keyword, pageable));
		if (category.isEmpty() && keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentFieldAndRegionAndProjectSection(recruitmentField, region, projectSection, pageable));
		if (category.isEmpty() && projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentFieldAndRegionAndContentContaining(recruitmentField, region, keyword, pageable));
		if (category.isEmpty() && region.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentFieldAndProjectSectionAndContentContaining(recruitmentField, projectSection, keyword, pageable));
		if (category.isEmpty() && recruitmentField.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRegionAndProjectSectionAndContentContaining(region, projectSection, keyword, pageable));

		if (keyword.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentFieldAndRegionAndProjectSection(category, recruitmentField, region, projectSection, pageable));
		if (projectSection.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentFieldAndRegionAndContentContaining(category, recruitmentField, region, keyword, pageable));
		if (region.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentFieldAndProjectSectionAndContentContaining(category, recruitmentField, projectSection, keyword, pageable));
		if (recruitmentField.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByCategoryAndRegionAndProjectSectionAndContentContaining(category, region, projectSection, keyword, pageable));
		if (category.isEmpty())
			return pageToListResponseDto(projectRepository.findAllByRecruitmentFieldAndRegionAndProjectSectionAndContentContaining(recruitmentField, region, projectSection, keyword, pageable));

		return pageToListResponseDto(projectRepository.findAllByCategoryAndRecruitmentFieldAndRegionAndProjectSectionAndContentContaining(category, recruitmentField, region, projectSection, keyword, pageable));
	}

	private ProjectListResponseDto pageToListResponseDto(Page<Project> pages) {
		List<ProjectDto> projectDtoList = new ArrayList<>();
		for (Project project : pages) {
			projectDtoList.add(project.toProjectDto(getProjectTagNames(project)));
		}
		return ProjectListResponseDto.builder()
			.totalNum(projectDtoList.size())
			.projectList(projectDtoList)
			.build();
	}
}
