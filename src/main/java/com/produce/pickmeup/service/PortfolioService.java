package com.produce.pickmeup.service;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.portfolio.*;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentResponseDto;
import com.produce.pickmeup.domain.tag.*;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.domain.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
	public String addPortfolio(PortfolioRequestDto portfolioRequestDto) {

		Optional<User> author = userRepository.findByEmail(portfolioRequestDto.getAuthorEmail());
		if (!author.isPresent()) {
			return ErrorCase.NO_SUCH_USER;
		}
		long result = portfolioRepository.save(
			Portfolio.builder()
				.author(author.get())
				.title(portfolioRequestDto.getTitle())
				.content(portfolioRequestDto.getContent())
				.category(portfolioRequestDto.getCategory())
				.recruitmentField(portfolioRequestDto.getRecruitmentField())
				.image(portfolioRequestDto.getImage())
				.build())
			.getId();
		if (!portfolioConnectTags(portfolioRequestDto.getPortfolioTags(), result)) {
			return ErrorCase.FAIL_TAG_SAVE_ERROR;
		}
		return String.valueOf(result);
	}

	@Transactional
	public boolean portfolioConnectTags(List<String> portfolioTags, long portfolioId) {
		Optional<Portfolio> savePortfolio = portfolioRepository.findById(portfolioId);
		if (!savePortfolio.isPresent()) {
			return false;
		}
		for (String tagName : portfolioTags) {
			Tag tag = tagRepository.findByTagName(tagName)
				.orElseGet(() -> addPortfolioTag(tagName));
			relationRepository.save(
				PortfolioHasTag.builder()
					.portfolio(savePortfolio.get())
					.tag(tag)
					.build()
			);
		}
		return true;
	}

	private Tag addPortfolioTag(String tagName) {
		return tagRepository.save(
			Tag.builder().tagName(tagName).build());
	}

	public Optional<Portfolio> getPortfolio(Long portfolioId) {
		return portfolioRepository.findById(portfolioId);
	}

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
}
