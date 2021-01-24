package com.produce.pickmeup.domain.portfolio;

import com.produce.pickmeup.domain.tag.PortfolioHasTag;
import com.produce.pickmeup.domain.tag.TagDto;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Entity
@Table(name = "portfolios")
@NoArgsConstructor
public class Portfolio {
	@OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private final List<PortfolioHasTag> portfolioTags = new ArrayList<>();
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
			.portfolioTag(TagDtoList)
			.createdDate(createdDate)
			.modifiedDate(modifiedDate)
			.build();
	}
}
