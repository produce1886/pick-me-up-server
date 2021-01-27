package com.produce.pickmeup.domain.tag;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioHasTagRepository extends JpaRepository<PortfolioHasTag, Long> {
	List<PortfolioHasTag> findByPortfolio(Portfolio portfolio);
	void deleteByPortfolioAndPortfolioTag(Portfolio portfolio, Tag tag);
}
