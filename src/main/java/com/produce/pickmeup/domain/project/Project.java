package com.produce.pickmeup.domain.project;

import com.produce.pickmeup.domain.project.comment.ProjectComment;
import com.produce.pickmeup.domain.tag.ProjectHasTag;
import com.produce.pickmeup.domain.tag.ProjectTagDto;
import com.produce.pickmeup.domain.user.User;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Entity
@Table(name = "projects")
@NoArgsConstructor
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false)
	private String title;
	@Column(updatable = false)
	@CreatedDate
	private Timestamp createdDate;
	@Column
	@LastModifiedDate
	private Timestamp modifiedDate;
	@Column(nullable = false)
	private String authorEmail;
	@ManyToOne
	@JoinColumn(name = "users_id")
	private User author;
	@Column
	private String image;
	@Column(nullable = false)
	private String content;
	@Column
	private int commentsNum;
	@Column
	private long viewNum;
	@Column(nullable = false)
	private String category;
	@Column(nullable = false)
	private String recruitmentField;
	@Column(nullable = false)
	private String region;
	@Column(nullable = false)
	private String projectSection;
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ProjectHasTag> projectTags = new ArrayList<>();
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ProjectComment> projectComments = new ArrayList<>();

	@Builder
	public Project(String title, String content, String authorEmail, User author, String category,
		String recruitmentField, String region, String projectSection, String image) {
		this.authorEmail = authorEmail;
		this.title = title;
		this.content = content;
		this.author = author;
		this.category = category;
		this.recruitmentField = recruitmentField;
		this.region = region;
		this.projectSection = projectSection;
		this.image = image;
		this.commentsNum = 0;
		this.viewNum = 0;
		this.createdDate = new Timestamp(System.currentTimeMillis());
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}

	public ProjectDto toProjectDto(List<ProjectTagDto> tagDtoList) {
		return ProjectDto.builder()
			.id(id)
			.user(author.toResponseDto())
			.title(title)
			.content(content)
			.category(category)
			.region(region)
			.recruitmentField(recruitmentField)
			.projectSection(projectSection)
			.viewNum(viewNum)
			.commentsNum(commentsNum)
			.tags(tagDtoList)
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.build();
	}
}
