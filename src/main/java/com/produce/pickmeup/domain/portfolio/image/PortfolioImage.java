package com.produce.pickmeup.domain.portfolio.image;

import com.produce.pickmeup.domain.portfolio.Portfolio;
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

@Getter
@Entity
@Table(name = "portfolio_images")
@NoArgsConstructor
public class PortfolioImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	private Portfolio portfolio;
	@Column
	private String imageUrl;

	@Builder
	public PortfolioImage(String imageUrl, Portfolio portfolio) {
		this.imageUrl = imageUrl;
		this.portfolio = portfolio;
	}

	public PortfolioImageDto toDto() {
		return PortfolioImageDto.builder()
			.id(id).image(imageUrl).build();
	}
}
