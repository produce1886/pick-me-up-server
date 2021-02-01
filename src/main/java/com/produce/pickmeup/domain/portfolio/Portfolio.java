package com.produce.pickmeup.domain.portfolio;

import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentResponseDto;
import com.produce.pickmeup.domain.tag.PortfolioHasTag;
import com.produce.pickmeup.domain.tag.TagDto;
import com.produce.pickmeup.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "portfolios")
@NoArgsConstructor
public class Portfolio {
	@OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private final List<PortfolioHasTag> portfolioTags = new ArrayList<>();
	@OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private final List<PortfolioComment> portfolioComments = new ArrayList<>();
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
	private String image;
	@Column
	private int commentsNum;
	@Column
	private long viewNum;
	@Column(nullable = false)
	private String category;
	@Column(nullable = false)
	private String recruitmentField;

	@Builder
	public Portfolio(String title, String content, User author, String category,
				   String recruitmentField, String image) {
		this.authorEmail = author.getEmail();
		this.title = title;
		this.content = content;
		this.author = author;
		this.category = category;
		this.recruitmentField = recruitmentField;
		this.image = image;
		this.commentsNum = 0;
		this.viewNum = 0;
		this.createdDate = new Timestamp(System.currentTimeMillis());
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}

	public PortfolioDto toPortfolioDto(List<TagDto> TagDtoList) {
		return PortfolioDto.builder()
			.id(id)
			.user(author.toResponseDto())
			.title(title)
			.content(content)
			.category(category)
			.recruitmentField(recruitmentField)
			.viewNum(viewNum)
			.commentsNum(commentsNum)
			.portfolioTags(TagDtoList)
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.build();
	}

	public PortfolioDetailResponseDto toDetailResponseDto
		(List<TagDto> portfolioTags, List<PortfolioCommentResponseDto> comments) {
		return PortfolioDetailResponseDto.builder()
			.image(image)
			.user(author.toResponseDto())
			.id(id)
			.title(title)
			.content(content)
			.category(category)
			.recruitmentField(recruitmentField)
			.portfolioTags(portfolioTags)
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.viewNum(viewNum)
			.commentsNum(commentsNum)
			.comments(comments)
			.build();
	}

	public void upViewNum() {
		this.viewNum++;
	}

	public void upCommentsNum() {
		this.commentsNum++;
	}

	public void updateExceptTags(PortfolioRequestDto portfolioRequestDto) {
		this.title = portfolioRequestDto.getTitle();
		this.content = portfolioRequestDto.getContent();
		this.category = portfolioRequestDto.getCategory();
		this.recruitmentField = portfolioRequestDto.getRecruitmentField();
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}
}
