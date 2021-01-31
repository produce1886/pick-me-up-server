package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.comment.*;
import com.produce.pickmeup.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PortfolioCommentService {
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
		return String.valueOf(result);
	}
}
