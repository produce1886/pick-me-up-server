package com.produce.pickmeup.domain.project;

import com.produce.pickmeup.domain.user.User;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	@Builder
	public Project(String title, String content, String authorEmail, User author, String category,
		String recruitmentField, String region, String projectSection) {
		this.authorEmail = authorEmail;
		this.title = title;
		this.content = content;
		this.author = author;
		this.category = category;
		this.recruitmentField = recruitmentField;
		this.region = region;
		this.projectSection = projectSection;
		this.commentsNum = 0;
		this.viewNum = 0;
		this.createdDate = new Timestamp(System.currentTimeMillis());
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}

	public ProjectDto toProjectDto() {
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
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.build();
	}
}