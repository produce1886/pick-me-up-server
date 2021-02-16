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
@Table(name = "tags")
@NoArgsConstructor
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false)
	private String tagName;
	@OneToMany(mappedBy = "projectTag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private final List<ProjectHasTag> projects = new ArrayList<>();
	@OneToMany(mappedBy = "portfolioTag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private final List<PortfolioHasTag> portfolios = new ArrayList<>();
	@Column
	private long currentScore;

	@Builder
	public Tag(String tagName) {
		this.tagName = tagName;
		this.currentScore = 0;
	}

	public TagDto toTagDto() {
		return TagDto.builder()
			.id(id)
			.tagName(tagName)
			.build();
	}
}
