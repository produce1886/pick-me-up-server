package com.produce.pickmeup.controller;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioComment;
import com.produce.pickmeup.domain.portfolio.comment.PortfolioCommentRequestDto;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.error.exception.InvalidAccessException;
import com.produce.pickmeup.error.exception.NoCommentException;
import com.produce.pickmeup.error.exception.NoPortfolioException;
import com.produce.pickmeup.error.exception.NoUserException;
import com.produce.pickmeup.service.PortfolioCommentService;
import com.produce.pickmeup.service.PortfolioService;
import com.produce.pickmeup.service.UserService;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class PortfolioCommentController {
    private final PortfolioService portfolioService;
    private final PortfolioCommentService commentService;
    private final UserService userService;

    @PostMapping("/portfolios/{id}/comments")
    public ResponseEntity<URI> addPortfolioComment(@PathVariable Long id, @RequestBody PortfolioCommentRequestDto portfolioCommentRequestDto) {

        User author = userService.findByEmail(portfolioCommentRequestDto.getAuthorEmail())
            .orElseThrow(NoUserException::new);
        Portfolio portfolio = portfolioService.getPortfolio(id)
            .orElseThrow(NoPortfolioException::new);

        String result = commentService.addPortfolioComment(author, portfolio, portfolioCommentRequestDto);
        return ResponseEntity.created(URI.create("/portfolios/" + id + "/comments/" + result)).build();
    }

    @GetMapping("/portfolios/{id}/comments/{commentId}")
    public ResponseEntity<Object> getPortfolioComment(@PathVariable Long id, @PathVariable @NonNull Long commentId) {

        Portfolio portfolio = portfolioService.getPortfolio(id)
            .orElseThrow(NoPortfolioException::new);
        PortfolioComment comment = commentService.getPortfolioComment(commentId)
            .orElseThrow(NoCommentException::new);
        if (!comment.included(portfolio.getId())) {
            throw new InvalidAccessException();
        }

        return ResponseEntity.ok(commentService.getCommentDetail(comment));
    }

    @PutMapping("/portfolios/{id}/comments/{commentId}")
    public ResponseEntity<Object> updatePortfolioComment(
        @PathVariable Long id, @PathVariable Long commentId,
        @RequestBody PortfolioCommentRequestDto portfolioCommentRequestDto) {

        Portfolio portfolio = portfolioService.getPortfolio(id)
            .orElseThrow(NoPortfolioException::new);
        PortfolioComment comment = commentService.getPortfolioComment(commentId)
            .orElseThrow(NoCommentException::new);
        if (!comment.included(portfolio.getId()) |
            !comment.authorCheck(portfolioCommentRequestDto.getAuthorEmail())) {
            throw new InvalidAccessException();
        }

        commentService.updatePortfolioComment(comment, portfolioCommentRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/portfolios/{id}/comments/{commentId}")
    public ResponseEntity<Object> deletePortfolioComment(@PathVariable Long id, @PathVariable Long commentId) {

        Portfolio portfolio = portfolioService.getPortfolio(id)
            .orElseThrow(NoPortfolioException::new);
        PortfolioComment comment = commentService.getPortfolioComment(commentId)
            .orElseThrow(NoCommentException::new);
        if (!comment.included(portfolio.getId())) {
            throw new InvalidAccessException();
        }

        commentService.deleteCommentDetail(commentId);
        // TODO remove comment num col in portfolio entity
        portfolio.downCommentsNum();
        return ResponseEntity.noContent().build();
    }
}
