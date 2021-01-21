package com.produce.pickmeup.domain.tag;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "project_tags")
@NoArgsConstructor
public class ProjectTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false)
	private String tagName;
	@OneToMany(mappedBy = "projectTag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ProjectHasTag> projects = new ArrayList<>();
	@Column
	private long score;

	@Builder
	public ProjectTag(String tagName) {
		this.tagName = tagName;
		this.score = 0;
	}

	public ProjectTagDto toProjectTagDto() {
		return ProjectTagDto.builder()
			.id(id)
			.tagName(tagName)
			.build();
	}
}
