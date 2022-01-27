package com.produce.pickmeup.domain.portfolio;

import org.springframework.data.jpa.domain.Specification;

public class PortfolioSpecification {
    public static Specification<Portfolio> byCategory(final String category) {
        return (Specification<Portfolio>) ((root, query, builder) ->
            builder.equal(root.get("category"), category));
    }

    public static Specification<Portfolio> byRecruitmentField(final String recruitmentField) {
        return (Specification<Portfolio>) ((root, query, builder) ->
            builder.equal(root.get("recruitmentField"), recruitmentField));
    }

    public static Specification<Portfolio> byKeyword(final String keyword) {
        Specification<Portfolio> specification = byKeywordInContent(keyword);
        specification = specification.or(byKeywordInTitle(keyword));
        return specification;

    }

    private static Specification<Portfolio> byKeywordInContent(final String keyword) {
        return (Specification<Portfolio>) ((root, query, builder) -> builder
            .like(root.get("content"), "%" + keyword + "%"));
    }

    private static Specification<Portfolio> byKeywordInTitle(final String keyword) {
        return (Specification<Portfolio>) ((root, query, builder) -> builder
            .like(root.get("title"), "%" + keyword + "%"));
    }
}
