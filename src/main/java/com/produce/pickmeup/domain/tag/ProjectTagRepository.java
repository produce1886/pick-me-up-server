package com.produce.pickmeup.domain.tag;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, Long> {
	Optional<ProjectTag> findByTagName(String tagName);
}
