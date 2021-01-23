package com.produce.pickmeup.domain.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "portfolio_tags")
@NoArgsConstructor
public class PortfolioTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String tagName;
    @OneToMany(mappedBy = "projectTag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PortfolioHasTag> portfolios = new ArrayList<>();
    @Column
    private long score;
}
