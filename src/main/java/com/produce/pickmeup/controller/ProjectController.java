package com.produce.pickmeup.controller;

import com.produce.pickmeup.domain.project.Project;
import com.produce.pickmeup.domain.project.ProjectRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.error.exception.EmptyFileException;
import com.produce.pickmeup.error.exception.FileConvertException;
import com.produce.pickmeup.error.exception.InvalidAccessException;
import com.produce.pickmeup.error.exception.InvalidFieldException;
import com.produce.pickmeup.error.exception.InvalidFileException;
import com.produce.pickmeup.error.exception.NoProjectException;
import com.produce.pickmeup.error.exception.NoUserException;
import com.produce.pickmeup.service.ProjectService;
import com.produce.pickmeup.service.S3Uploader;
import com.produce.pickmeup.service.UserService;
import java.io.File;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
public class ProjectController {
  private final ProjectService projectService;
  private final UserService userService;
  private final S3Uploader uploaderService;

  @PostMapping("/projects")
  public ResponseEntity<Object> addProject(@RequestBody ProjectRequestDto projectRequestDto) {

    if (!isRequestBodyValid(projectRequestDto)) {
      throw new InvalidFieldException();
    }
    User author = userService.findByEmail(projectRequestDto.getAuthorEmail())
        .orElseThrow(NoUserException::new);

    String result = projectService.addProject(projectRequestDto, author);
    return ResponseEntity.created(URI.create("/projects/" + result)).build();
  }

  @GetMapping("/projects/{id}")
  public ResponseEntity<Object> getProjectDetail(@PathVariable Long id) {
    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);

    return ResponseEntity.ok(projectService.getProjectDetail(project));
  }

  @PatchMapping("/projects/{id}/image")
  public ResponseEntity<Object> updateProjectImage(
      @RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {
    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);
    if (multipartFile.isEmpty()) {
      throw new EmptyFileException();
    }

    File convertedFile = uploaderService.convert(multipartFile);
    if (convertedFile == null) {
      throw new FileConvertException();
    }
    if (!uploaderService.isValidExtension(convertedFile)) {
      throw new InvalidFileException();
    }

    String result = projectService.updateProjectImage(convertedFile, project);
    return ResponseEntity.created(URI.create(result)).build();
  }

  @DeleteMapping("/projects/{id}/image")
  public ResponseEntity<Object> deleteProjectImage(@PathVariable Long id) {
    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);

    projectService.deleteProjectImage(project);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/projects/{id}")
  public ResponseEntity<Object> updateProject(@PathVariable Long id,
      @RequestBody ProjectRequestDto projectRequestDto) {
    if (!isRequestBodyValid(projectRequestDto)) {
      throw new InvalidFieldException();
    }
    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);
    if (!project.authorCheck(projectRequestDto.getAuthorEmail())) {
      throw new InvalidAccessException();
    }

    projectService.updateProject(project, projectRequestDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/projects/{id}")
  public ResponseEntity<Object> deleteProject(@PathVariable Long id) {
    Project project = projectService.getProject(id)
        .orElseThrow(NoProjectException::new);

    projectService.deleteProject(project);
    projectService.deleteProjectImage(project);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/projects/list")
  public ResponseEntity<Object> getProjectsList(final Pageable pageable,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String recruitmentField,
      @RequestParam(required = false) String region,
      @RequestParam(required = false) String projectSection,
      @RequestParam(required = false) String keyword) {
    return ResponseEntity.ok(
        projectService.getProjectsList(pageable, category, recruitmentField, region,
            projectSection, keyword));
  }

  private boolean isRequestBodyValid(ProjectRequestDto projectRequestDto) {
    return projectRequestDto.getAuthorEmail() != null &&
        projectRequestDto.getTitle() != null &&
        projectRequestDto.getContent() != null &&
        projectRequestDto.getCategory() != null &&
        projectRequestDto.getRecruitmentField() != null &&
        projectRequestDto.getProjectSection() != null &&
        projectRequestDto.getRegion() != null &&
        projectRequestDto.getProjectTags() != null;
  }
}
