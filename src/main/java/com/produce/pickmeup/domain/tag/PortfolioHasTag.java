package com.produce.pickmeup.domain.tag;


import com.produce.pickmeup.domain.portfolio.Portfolio;
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

@Getter
@Entity
@Table(name = "portfolio_has_tag")
@NoArgsConstructor
public class PortfolioHasTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	private Portfolio portfolio;
	@ManyToOne
	@JoinColumn(name = "tag_id")
	private Tag portfolioTag;

	@Builder
	public PortfolioHasTag(Portfolio portfolio, Tag tag) {
		this.portfolio = portfolio;
		this.portfolioTag = tag;
	}
}
