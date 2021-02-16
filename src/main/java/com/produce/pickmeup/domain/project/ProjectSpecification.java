package com.produce.pickmeup.domain.project;

import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification {
	public static Specification<Project> byCategory(final String category) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("category"), category));
	}

	public static Specification<Project> byRecruitmentField(final String recruitmentField) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("recruitmentField"), recruitmentField));
	}

	public static Specification<Project> byRegion(final String region) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("region"), region));
	}

	public static Specification<Project> byProjectSection(final String projectSection) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("projectSection"), projectSection));
	}

	public static Specification<Project> byKeyword(final String keyword) {
		Specification<Project> specification = byKeywordInContent(keyword);
		specification = specification.or(byKeywordInTitle(keyword));
		return specification;
	}

	private static Specification<Project> byKeywordInContent(final String keyword) {
		return (Specification<Project>) ((root, query, builder) -> builder
			.like(root.get("content"), "%" + keyword + "%"));
	}

	private static Specification<Project> byKeywordInTitle(final String keyword) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.like(root.get("title"), "%" + keyword + "%"));
	}
}
