package com.produce.pickmeup.domain.portfolio.comment;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "portfolio_comments")
@NoArgsConstructor
public class PortfolioComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	private Portfolio portfolio;
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

	public PortfolioCommentResponseDto toResponseDto() {
		return PortfolioCommentResponseDto.builder()
			.id(id)
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.content(content)
			.user(author.toResponseDto())
			.build();
	}
}
