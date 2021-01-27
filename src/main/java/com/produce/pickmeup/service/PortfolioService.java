package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.PortfolioDetailResponseDto;
import com.produce.pickmeup.domain.portfolio.PortfolioRepository;
import com.produce.pickmeup.domain.portfolio.PortfolioRequestDto;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentResponseDto;
import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.tag.*;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfolioService {

	private final TagRepository tagRepository;
	private final UserRepository userRepository;
	private final PortfolioRepository portfolioRepository;
	private final PortfolioHasTagRepository relationRepository;

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
	private Tag addPortfolioTag(String tagName) {
		return tagRepository.save(
			Tag.builder().tagName(tagName).build());
	}

	@Transactional
	public Optional<Portfolio> getPortfolio(Long portfolioId) {
		return portfolioRepository.findById(portfolioId);
	}

	@Transactional
	public PortfolioDetailResponseDto getPortfolioDetail(Portfolio portfolio) {
		portfolio.upViewNum();
		List<PortfolioHasTag> relations = portfolio.getPortfolioTags();
		List<PortfolioCommentResponseDto> comments = portfolio.getPortfolioComments()
			.stream().map(PortfolioComment::toResponseDto).collect(Collectors.toList());
		if (relations.isEmpty()) {
			return portfolio.toDetailResponseDto(Collections.emptyList(), comments);
		}
		List<TagDto> PortfolioTags = relations.stream()
			.map(PortfolioHasTag::getPortfolioTag)
			.map(Tag::toTagDto).collect(Collectors.toList());
		return portfolio.toDetailResponseDto(PortfolioTags, comments);
	}

	public boolean checkPortfolioAuthorEmail(Portfolio portfolio, String authorEmail) {
		return portfolio.getAuthorEmail().equals(authorEmail);
	}

	@Transactional
	public void updatePortfolio(Portfolio portfolio, PortfolioRequestDto portfolioRequestDto) {
		portfolio.updateExceptTags(portfolioRequestDto);

		List<String> originalTagNames
			= getPortfolioTagNames(portfolio).stream().map(TagDto::getTagName).collect(Collectors.toList());
		List<String> newTagNames = portfolioRequestDto.getPortfolioTags();
		List<String> disconnectTagNames = new ArrayList<>(); // 없앨 친구들 고름
		for (String tagName : originalTagNames) {
			if (!newTagNames.contains(tagName)) {
				disconnectTagNames.add(tagName);
			}
		}
		newTagNames.removeIf(originalTagNames::contains); //new
		deletePortfolioTagRelations(portfolio, disconnectTagNames);
		portfolioConnectTags(newTagNames, portfolio);
	}

	@Transactional
	public void deletePortfolioTagRelations(Portfolio portfolio, List<String> disconnectTagNames) {
		disconnectTagNames.forEach(value ->
			tagRepository.findByTagName(value).ifPresent(tag ->
				relationRepository.deleteByPortfolioAndPortfolioTag(portfolio, tag))
		);
	}
}
