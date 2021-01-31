package com.produce.pickmeup.domain.project.comment;

import com.produce.pickmeup.domain.project.Project;
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
@Table(name = "project_comments")
@NoArgsConstructor
public class ProjectComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;
	@Column(updatable = false)
	@CreatedDate
	private Timestamp createdDate;
	@Column
	@LastModifiedDate
	private Timestamp modifiedDate;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User author;
	@Column(nullable = false)
	private String authorEmail;
	@Column(nullable = false)
	private String content;

	@Builder
	public ProjectComment(String content, User author, Project project) {
		this.content = content;
		this.author = author;
		this.authorEmail = author.getEmail();
		this.project = project;
		this.createdDate = new Timestamp(System.currentTimeMillis());
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}

	public ProjectCommentResponseDto toResponseDto() {
		return ProjectCommentResponseDto.builder()
			.id(id)
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.content(content)
			.user(author.toResponseDto())
			.build();
	}

	public ProjectCommentDetailResponseDto toDetailResponseDto() {
		return ProjectCommentDetailResponseDto.builder()
			.id(id)
			.content(content)
			.authorEmail(authorEmail)
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.build();
	}

	public void updateContent(ProjectCommentRequestDto projectCommentUpdateDto) {
		this.content = projectCommentUpdateDto.getContent();
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}
}
