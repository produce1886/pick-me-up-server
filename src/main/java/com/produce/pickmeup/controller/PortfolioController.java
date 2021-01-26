package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.PortfolioRequestDto;
import com.produce.pickmeup.service.PortfolioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PortfolioController {

	private final List<String> REQUEST_ERROR_LIST = ErrorCase.getRequestErrorList();
	private final List<String> INTERNAL_ERROR_LIST = ErrorCase.getInternalErrorList();
	private final PortfolioService portfolioService;

	private boolean isRequestBodyValid(PortfolioRequestDto portfolioRequestDto) {
		return portfolioRequestDto.getAuthorEmail() != null &&
			portfolioRequestDto.getTitle() != null &&
			portfolioRequestDto.getContent() != null &&
			portfolioRequestDto.getCategory() != null &&
			portfolioRequestDto.getRecruitmentField() != null &&
			portfolioRequestDto.getPortfolioTags() != null;
	}

	@PostMapping("/portfolios")
	public ResponseEntity<Object> addPortfolio(@RequestBody PortfolioRequestDto portfolioRequestDto){
		if (!isRequestBodyValid(portfolioRequestDto)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVALID_FIELD_ERROR)
			);
		}
		String result = portfolioService.addPortfolio(portfolioRequestDto);
		if (INTERNAL_ERROR_LIST.contains(result)) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), result));
		}
		if (REQUEST_ERROR_LIST.contains(result)) {
			return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), result));
		}
		return ResponseEntity.created(URI.create("/portfolios/" + result)).build();
	}

	@GetMapping("/portfolios/{id}")
	public ResponseEntity<Object> getPortfolioDetail(@PathVariable Long id) {
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		return portfolio.<ResponseEntity<Object>>map(
			value -> ResponseEntity.ok(portfolioService.getPortfolioDetail(value)))
			.orElseGet(() -> ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_PORTFOLIO)));
	}
}
