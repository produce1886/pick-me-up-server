package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.tag.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfolioService {

    private final PortfolioHasTagRepository relationRepository;

    public List<PortfolioTagDto> getPortfolioTagNames(Portfolio portfolio) {
        List<PortfolioHasTag> relations = relationRepository.findByPortfolio(portfolio);
        if (relations.isEmpty()) {
            return Collections.emptyList();
        }
        return relations.stream().map(PortfolioHasTag::getPortfolioTag)
                .map(PortfolioTag::toPortfolioTagDto)
                .collect(Collectors.toList());
    }
}
