package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentDetailResponseDto;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentRepository;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentRequestDto;
import com.produce.pickmeup.domain.tag.PortfolioHasTag;
import com.produce.pickmeup.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PortfolioCommentService {
	private final int COMMENT_SCORE = 2;
	private final PortfolioCommentRepository portfolioCommentRepository;

	@Transactional
	public String addPortfolioComment(User author, Portfolio portfolio,
		PortfolioCommentRequestDto responseDto) {
		long result = portfolioCommentRepository.save(
			PortfolioComment.builder()
				.author(author)
				.content(responseDto.getContent())
				.portfolio(portfolio)
				.build())
			.getId();
		portfolio.upCommentsNum();
		portfolio.getPortfolioTags().stream().map(PortfolioHasTag::getPortfolioTag)
			.forEach((tag) -> tag.upCurrentScore(COMMENT_SCORE));
		return String.valueOf(result);
	}

	public Optional<PortfolioComment> getPortfolioComment(Long portfolioCommentId) {
		return portfolioCommentRepository.findById(portfolioCommentId);
	}

	public PortfolioCommentDetailResponseDto getCommentDetail(PortfolioComment comment) {
		return comment.toDetailResponseDto();
	}

	@Transactional
	public void deleteCommentDetail(Long portfolioCommentId) {
		portfolioCommentRepository.deleteById(portfolioCommentId);
	}

	@Transactional
	public void updatePortfolioComment(PortfolioComment portfolioComment,
		PortfolioCommentRequestDto portfolioCommentRequestDto) {
		portfolioComment.updateContent(portfolioCommentRequestDto);
	}
}
