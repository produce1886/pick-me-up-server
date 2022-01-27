package com.produce.pickmeup.domain.tag;

import com.produce.pickmeup.domain.project.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectHasTagRepository extends JpaRepository<ProjectHasTag, Long> {
    List<ProjectHasTag> findByProject(Project project);

    void deleteByProjectAndProjectTag(Project project, Tag tag);

    boolean existsByProjectTag(Tag tag);
}
