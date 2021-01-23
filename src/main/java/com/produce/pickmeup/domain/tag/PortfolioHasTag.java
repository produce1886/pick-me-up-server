package com.produce.pickmeup.domain.tag;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private PortfolioTag portfolioTag;
}
