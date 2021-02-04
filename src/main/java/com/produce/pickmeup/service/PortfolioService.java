package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.PortfolioDetailResponseDto;
import com.produce.pickmeup.domain.portfolio.PortfolioRepository;
import com.produce.pickmeup.domain.portfolio.PortfolioRequestDto;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentResponseDto;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImage;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImageDto;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImageRepository;
import com.produce.pickmeup.domain.tag.PortfolioHasTag;
import com.produce.pickmeup.domain.tag.PortfolioHasTagRepository;
import com.produce.pickmeup.domain.tag.Tag;
import com.produce.pickmeup.domain.tag.TagDto;
import com.produce.pickmeup.domain.tag.TagRepository;
import com.produce.pickmeup.domain.user.User;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PortfolioService {
	private final String PORTFOLIO_IMAGE_PATH = "portfolio-image";
	private final PortfolioImageRepository imageRepository;
	private final TagRepository tagRepository;
	private final PortfolioRepository portfolioRepository;
	private final PortfolioHasTagRepository relationRepository;
	private final S3Uploader s3Uploader;

	@Transactional
	public List<TagDto> getPortfolioTagNames(Portfolio portfolio) {
		List<PortfolioHasTag> relations = relationRepository.findByPortfolio(portfolio);
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
		for (String tagName : portfolioTags) {
			Tag tag = tagRepository.findByTagName(tagName)
				.orElseGet(() -> addPortfolioTag(tagName));
			relationRepository.save(
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
		disconnectTagNames.forEach(value ->
			tagRepository.findByTagName(value).ifPresent(tag ->
				relationRepository.deleteByPortfolioAndPortfolioTag(portfolio, tag))
		);
	}

	@Transactional
	public void deletePortfolio(Portfolio portfolio) {
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
}
