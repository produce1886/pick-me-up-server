package com.produce.pickmeup.domain.project;

import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification {
	public static Specification<Project> ByCategory(final String category) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("category"), category));
	}

	public static Specification<Project> ByRecruitmentField(final String recruitmentField) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("recruitmentField"), recruitmentField));
	}

	public static Specification<Project> ByRegion(final String region) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("region"), region));
	}

	public static Specification<Project> ByProjectSection(final String projectSection) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.equal(root.get("projectSection"), projectSection));
	}

	public static Specification<Project> ByKeyword(final String keyword) {
		Specification<Project> specification = ByKeywordInContent(keyword);
		specification = specification.or(ByKeywordInTitle(keyword));
		return specification;
	}

	private static Specification<Project> ByKeywordInContent(final String keyword) {
		return (Specification<Project>) ((root, query, builder) -> builder
			.like(root.get("content"), "%" + keyword + "%"));
	}

	private static Specification<Project> ByKeywordInTitle(final String keyword) {
		return (Specification<Project>) ((root, query, builder) ->
			builder.like(root.get("title"), "%" + keyword + "%"));
	}
}
