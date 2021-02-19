package com.produce.pickmeup.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.produce.pickmeup.domain.portfolio.*;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentResponseDto;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImage;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImageDto;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImageRepository;
import com.produce.pickmeup.domain.tag.*;
import com.produce.pickmeup.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class PortfolioService {
	private final String PORTFOLIO_IMAGE_PATH = "portfolio-image";
	private final PortfolioImageRepository imageRepository;
	private final TagRepository tagRepository;
	private final PortfolioRepository portfolioRepository;
	private final PortfolioHasTagRepository portfolioRelationRepository;
	private final ProjectHasTagRepository projectRelationRepository;
	private final S3Uploader s3Uploader;

	@Transactional
	public List<TagDto> getPortfolioTagNames(Portfolio portfolio) {
		List<PortfolioHasTag> relations = portfolioRelationRepository.findByPortfolio(portfolio);
		if (relations.isEmpty()) {
			return Collections.emptyList();
		}
		return relations.stream().map(PortfolioHasTag::getPortfolioTag)
			.map(Tag::toTagDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public String addPortfolio(PortfolioRequestDto portfolioRequestDto, User author) {
		Portfolio portfolio = portfolioRepository.save(
			Portfolio.builder()
				.author(author)
				.title(portfolioRequestDto.getTitle())
				.content(portfolioRequestDto.getContent())
				.category(portfolioRequestDto.getCategory())
				.recruitmentField(portfolioRequestDto.getRecruitmentField())
				.build());
		portfolioConnectTags(portfolioRequestDto.getPortfolioTags(), portfolio);
		addPortfolioImageList(portfolioRequestDto.getImages(), portfolio);
		return String.valueOf(portfolio.getId());
	}

	@Transactional
	public void portfolioConnectTags(List<String> portfolioTags, Portfolio savedPortfolio) {
		HashSet<String> portfolioTagSet = new HashSet<>(portfolioTags);
		for (String tagName : portfolioTagSet) {
			Tag tag = tagRepository.findByTagName(tagName)
				.orElseGet(() -> addPortfolioTag(tagName));
			tag.upFiveCurrentScore();
			portfolioRelationRepository.save(
				PortfolioHasTag.builder()
					.portfolio(savedPortfolio)
					.tag(tag).build()
			);
		}
	}

	@Transactional
	public Tag addPortfolioTag(String tagName) {
		return tagRepository.save(
			Tag.builder().tagName(tagName).build());
	}

	@Transactional
	public void addPortfolioImageList(List<String> imageUrls, Portfolio portfolio) {
		for (String imageUrl : imageUrls) {
			imageRepository.save(PortfolioImage.builder()
				.image(imageUrl).portfolio(portfolio).build());
		}
	}

	public Optional<PortfolioImage> getPortfolioImage(Long portfolioImageId) {
		return imageRepository.findById(portfolioImageId);
	}

	public Optional<Portfolio> getPortfolio(Long portfolioId) {
		return portfolioRepository.findById(portfolioId);
	}

	@Transactional
	public PortfolioDetailResponseDto getPortfolioDetail(Portfolio portfolio) {
		portfolio.upViewNum();
		List<PortfolioHasTag> relations = portfolio.getPortfolioTags();
		List<PortfolioCommentResponseDto> comments = portfolio.getPortfolioComments()
			.stream().map(PortfolioComment::toResponseDto).collect(Collectors.toList());
		List<PortfolioImageDto> images = portfolio.getPortfolioImages()
			.stream().map(PortfolioImage::toDto).collect(Collectors.toList());
		if (relations.isEmpty()) {
			return portfolio.toDetailResponseDto(Collections.emptyList(), comments, images);
		}
		relations.forEach((tag) -> tag.getPortfolioTag().upCurrentScore());
		List<TagDto> PortfolioTags = relations.stream()
			.map(PortfolioHasTag::getPortfolioTag)
			.map(Tag::toTagDto).collect(Collectors.toList());
		return portfolio.toDetailResponseDto(PortfolioTags, comments, images);
	}

	public boolean checkPortfolioAuthorEmail(Portfolio portfolio, String authorEmail) {
		return portfolio.getAuthorEmail().equals(authorEmail);
	}

	@Transactional
	public void updatePortfolio(Portfolio portfolio, PortfolioRequestDto portfolioRequestDto) {
		portfolio.updateExceptTags(portfolioRequestDto);

		List<String> originalTagNames
			= getPortfolioTagNames(portfolio).stream().map(TagDto::getTagName)
			.collect(Collectors.toList());
		List<String> newTagNames = portfolioRequestDto.getPortfolioTags();
		List<String> disconnectTagNames = new ArrayList<>();
		for (String tagName : originalTagNames) {
			if (!newTagNames.contains(tagName)) {
				disconnectTagNames.add(tagName);
			}
		}
		newTagNames.removeIf(originalTagNames::contains);
		deletePortfolioTagRelations(portfolio, disconnectTagNames);
		portfolioConnectTags(newTagNames, portfolio);
	}

	@Transactional
	public String addPortfolioImage(File convertedFile, Portfolio portfolio) {
		PortfolioImage portfolioImage = imageRepository.save(PortfolioImage.builder()
			.portfolio(portfolio).build());
		String result = s3Uploader
			.upload(convertedFile, PORTFOLIO_IMAGE_PATH, String.valueOf(portfolioImage.getId()));
		portfolioImage.updateImage(result);
		return result;
	}

	@Transactional
	public String updatePortfolioImage(File convertedFile, PortfolioImage portfolioImage) {
		String result = s3Uploader
			.upload(convertedFile, PORTFOLIO_IMAGE_PATH, String.valueOf(portfolioImage.getId()));
		portfolioImage.updateImage(result);
		return result;
	}

	@Transactional
	public void deletePortfolioTagRelations(Portfolio portfolio, List<String> disconnectTagNames) {
		for (String tagName: disconnectTagNames) {
			Optional<Tag> tag = tagRepository.findByTagName(tagName);
			if (tag.isPresent()) {
				portfolioRelationRepository.deleteByPortfolioAndPortfolioTag(portfolio, tag.get());
				if (!(projectRelationRepository.existsByProjectTag(tag.get())) &&
					!(portfolioRelationRepository.existsByPortfolioTag(tag.get())))
					tagRepository.delete(tag.get());
			}
		}
	}

	@Transactional
	public void deletePortfolio(Portfolio portfolio) {
		List<String> portfolioTagNames
			= getPortfolioTagNames(portfolio).stream().map(TagDto::getTagName)
			.collect(Collectors.toList());
		deletePortfolioTagRelations(portfolio, portfolioTagNames);
		portfolioRepository.delete(portfolio);
	}

	@Transactional
	public void deletePortfolioImageFromDB(PortfolioImage portfolioImage) {
		imageRepository.delete(portfolioImage);
	}

	@Transactional
	public void deletePortfolioImageFromS3(Long portfolioImageId) {
		s3Uploader.delete(PORTFOLIO_IMAGE_PATH, String.valueOf(portfolioImageId));
	}

	@Transactional
	public PortfolioListResponseDto getPortfoliosList(Pageable pageable, String category,
		String recruitmentField, String keyword) {
		Specification<Portfolio> specification = Specification.where(null);
		if (category != null && !category.isEmpty()) {
			specification = specification
				.and(Specification.where(PortfolioSpecification.byCategory(category)));
		}
		if (recruitmentField != null && !recruitmentField.isEmpty()) {
			specification = specification.and(
				Specification.where(PortfolioSpecification.byRecruitmentField(recruitmentField)));
		}
		if (keyword != null && !keyword.isEmpty()) {
			specification = specification
				.and(Specification.where(PortfolioSpecification.byKeyword(keyword)));
		}
		return pageToListResponseDto(portfolioRepository.findAll(specification, pageable));
	}

	private PortfolioListResponseDto pageToListResponseDto(Page<Portfolio> pages) {
		List<PortfolioDto> portfolioDtoList = new ArrayList<>();
		for (Portfolio portfolio : pages) {
			portfolioDtoList.add(portfolio.toPortfolioDto(getPortfolioTagNames(portfolio)));
		}
		return PortfolioListResponseDto.builder()
			.totalNum((int) pages.getTotalElements())
			.portfolioList(portfolioDtoList)
			.build();
	}
}
