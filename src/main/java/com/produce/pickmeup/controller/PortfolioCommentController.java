package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.service.PortfolioCommentService;
import com.produce.pickmeup.service.PortfolioService;
import com.produce.pickmeup.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PortfolioCommentController {
	private final PortfolioService portfolioService;
	private final PortfolioCommentService portfolioCommentService;
	private final UserService userService;

	@PostMapping("/portfolios/{id}/comments")
	public ResponseEntity<Object> addPortfolioComment(@PathVariable Long id,
		@RequestBody PortfolioCommentRequestDto portfolioCommentRequestDto) {
		Optional<User> author = userService.findByEmail(portfolioCommentRequestDto.getAuthorEmail());
		if (!author.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER_ERROR));
		}
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		if (!portfolio.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PORTFOLIO_ERROR));
		}
		String result = portfolioCommentService
			.addPortfolioComment(author.get(), portfolio.get(), portfolioCommentRequestDto);
		return ResponseEntity.created(URI.create("/portfolios/" + id + "/comments/" + result)).build();
	}

	@GetMapping("/portfolios/{id}/comments/{commentId}")
	public ResponseEntity<Object> getPortfolioComment(@PathVariable Long id,
		@PathVariable Long commentId) {
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		if (!portfolio.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PORTFOLIO_ERROR));
		}
		Optional<PortfolioComment> portfolioComment = portfolioCommentService
			.getPortfolioComment(commentId);
		if (!portfolioComment.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
		}
		if (!portfolioCommentService.isLinked(portfolioComment.get(), id)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
		}
		return ResponseEntity.ok(portfolioCommentService.getCommentDetail(portfolioComment.get()));
	}

	@PutMapping("/portfolios/{id}/comments/{commentId}")
	public ResponseEntity<Object> updatePortfolioComment(@PathVariable Long id,
		@PathVariable Long commentId,
		@RequestBody PortfolioCommentRequestDto portfolioCommentRequestDto) {
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		if (!portfolio.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PORTFOLIO_ERROR));
		}
		Optional<PortfolioComment> portfolioComment = portfolioCommentService
			.getPortfolioComment(commentId);
		if (!portfolioComment.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
		}
		if (!portfolioCommentService.isLinked(portfolioComment.get(), portfolio.get().getId())) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
		}
		if (!portfolioCommentService.checkPortfolioCommentAuthorEmail(portfolioComment.get(),
			portfolioCommentRequestDto.getAuthorEmail())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), ErrorCase.FORBIDDEN_ERROR));
		}
		portfolioCommentService.updatePortfolioComment(portfolioComment.get(), portfolioCommentRequestDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/portfolios/{id}/comments/{commentId}")
	public ResponseEntity<Object> deletePortfolioComment(@PathVariable Long id,
		@PathVariable Long commentId) {
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		if (!portfolio.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PORTFOLIO_ERROR));
		}
		Optional<PortfolioComment> portfolioComment = portfolioCommentService
			.getPortfolioComment(commentId);
		if (!portfolioComment.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_COMMENT_ERROR));
		}
		if (!portfolioCommentService.isLinked(portfolioComment.get(), portfolio.get().getId())) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.BAD_REQUEST_ERROR));
		}
		portfolioCommentService.deleteCommentDetail(commentId);
		portfolio.get().downCommentsNum();
		return ResponseEntity.noContent().build();
	}
}
