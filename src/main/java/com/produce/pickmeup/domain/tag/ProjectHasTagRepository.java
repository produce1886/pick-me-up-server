package com.produce.pickmeup.domain.tag;

import com.produce.pickmeup.domain.project.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectHasTagRepository extends JpaRepository<ProjectHasTag, Long> {
	List<ProjectHasTag> findByProject(Project project);
	Optional<ProjectHasTag> deleteByProjectAndProjectTag(Project project, Tag tag);
}
