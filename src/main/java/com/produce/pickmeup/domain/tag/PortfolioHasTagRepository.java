package com.produce.pickmeup.domain.tag;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioHasTagRepository extends JpaRepository<PortfolioHasTag, Long> {
    List<PortfolioHasTag> findByPortfolio(Portfolio portfolio);
}
