package com.produce.pickmeup.domain.tag;

import com.produce.pickmeup.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectHasTagRepository extends JpaRepository<ProjectHasTag, Long> {
	List<ProjectHasTag> findByProject(Project project);
	void deleteByProjectAndProjectTag(Project project, Tag tag);
	boolean existsByProjectTag(Tag tag);
}
