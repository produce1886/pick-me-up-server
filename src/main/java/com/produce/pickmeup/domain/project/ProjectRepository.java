package com.produce.pickmeup.domain.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	Page<Project> findAll(Pageable pageable);
	Page<Project> findAllByCategory(String category, Pageable pageable);
	Page<Project> findAllByRecruitmentField(String recruitmentField, Pageable pageable);
	Page<Project> findAllByRegion(String region, Pageable pageable);
	Page<Project> findAllByProjectSection(String projectSection, Pageable pageable);
	Page<Project> findAllByContentContaining(String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentField(String category, String recruitmentField, Pageable pageable);
	Page<Project> findAllByCategoryAndRegion(String category, String region, Pageable pageable);
	Page<Project> findAllByCategoryAndProjectSection(String category, String projectSection, Pageable pageable);
	Page<Project> findAllByCategoryAndContentContaining(String category, String keyword, Pageable pageable);
	Page<Project> findAllByRecruitmentFieldAndRegion(String recruitmentField, String region, Pageable pageable);
	Page<Project> findAllByRecruitmentFieldAndProjectSection(String recruitmentField, String projectSection, Pageable pageable);
	Page<Project> findAllByRecruitmentFieldAndContentContaining(String recruitmentField, String keyword, Pageable pageable);
	Page<Project> findAllByRegionAndProjectSection(String region, String projectSection, Pageable pageable);
	Page<Project> findAllByRegionAndContentContaining(String region, String keyword, Pageable pageable);
	Page<Project> findAllByProjectSectionAndContentContaining(String projectSection, String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentFieldAndRegion(String category, String recruitmentField, String region, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentFieldAndProjectSection(String category, String recruitmentField, String projectSection, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentFieldAndContentContaining(String category, String recruitmentField, String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndRegionAndProjectSection(String category, String region, String projectSection, Pageable pageable);
	Page<Project> findAllByCategoryAndRegionAndContentContaining(String category, String region, String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndProjectSectionAndContentContaining(String category, String projectSection, String keyword, Pageable pageable);
	Page<Project> findAllByRecruitmentFieldAndRegionAndProjectSection(String recruitmentField, String region, String projectSection, Pageable pageable);
	Page<Project> findAllByRecruitmentFieldAndRegionAndContentContaining(String recruitmentField, String region, String keyword, Pageable pageable);
	Page<Project> findAllByRecruitmentFieldAndProjectSectionAndContentContaining(String recruitmentField, String projectSection, String keyword, Pageable pageable);
	Page<Project> findAllByRegionAndProjectSectionAndContentContaining(String region, String projectSection, String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentFieldAndRegionAndProjectSection(String category, String recruitmentField, String region, String projectSection, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentFieldAndRegionAndContentContaining(String category, String recruitmentField, String region, String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentFieldAndProjectSectionAndContentContaining(String category, String recruitmentField, String projectSection, String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndRegionAndProjectSectionAndContentContaining(String category, String region, String projectSection, String keyword, Pageable pageable);
	Page<Project> findAllByRecruitmentFieldAndRegionAndProjectSectionAndContentContaining(String recruitmentField, String region, String projectSection, String keyword, Pageable pageable);
	Page<Project> findAllByCategoryAndRecruitmentFieldAndRegionAndProjectSectionAndContentContaining(String category, String recruitmentField, String region, String projectSection, String keyword, Pageable pageable);
}
