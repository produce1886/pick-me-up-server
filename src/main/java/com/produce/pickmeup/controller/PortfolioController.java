package com.produce.pickmeup.controller;

import com.produce.pickmeup.common.ErrorCase;
import com.produce.pickmeup.domain.ErrorMessage;
import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.PortfolioRequestDto;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImage;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.service.PortfolioService;
import com.produce.pickmeup.service.S3Uploader;
import com.produce.pickmeup.service.UserService;
import java.io.File;
import java.net.URI;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
public class PortfolioController {
	private final UserService userService;
	private final PortfolioService portfolioService;
	private final S3Uploader uploaderService;

	private boolean isRequestBodyValid(PortfolioRequestDto portfolioRequestDto) {
		return portfolioRequestDto.getAuthorEmail() != null &&
			portfolioRequestDto.getTitle() != null &&
			portfolioRequestDto.getContent() != null &&
			portfolioRequestDto.getCategory() != null &&
			portfolioRequestDto.getRecruitmentField() != null &&
			portfolioRequestDto.getPortfolioTags() != null;
	}

	@PostMapping("/portfolios")
	public ResponseEntity<Object> addPortfolio(
		@RequestBody PortfolioRequestDto portfolioRequestDto) {
		if (!isRequestBodyValid(portfolioRequestDto)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVALID_FIELD_ERROR)
			);
		}
		Optional<User> author = userService.findByEmail(portfolioRequestDto.getAuthorEmail());
		if (!author.isPresent()) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.NO_SUCH_USER_ERROR)
			);
		}
		String result = portfolioService.addPortfolio(portfolioRequestDto, author.get());
		return ResponseEntity.created(URI.create("/portfolios/" + result)).build();
	}

	@GetMapping("/portfolios/{id}")
	public ResponseEntity<Object> getPortfolioDetail(@PathVariable Long id) {
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		return portfolio.<ResponseEntity<Object>>map(
			value -> ResponseEntity.ok(portfolioService.getPortfolioDetail(value)))
			.orElseGet(() -> ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_PORTFOLIO_ERROR)));
	}

	@PutMapping("/portfolios/{id}")
	public ResponseEntity<Object> updatePortfolio
		(@PathVariable Long id, @RequestBody PortfolioRequestDto portfolioRequestDto) {
		if (!isRequestBodyValid(portfolioRequestDto)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.INVALID_FIELD_ERROR)
			);
		}
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		if (!portfolio.isPresent()) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_PORTFOLIO_ERROR));
		}
		if (!portfolioService.checkPortfolioAuthorEmail(
			portfolio.get(), portfolioRequestDto.getAuthorEmail())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErrorMessage(HttpStatus.FORBIDDEN.value(), ErrorCase.FORBIDDEN_ERROR));
		}
		portfolioService.updatePortfolio(portfolio.get(), portfolioRequestDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/portfolios/{id}")
	public ResponseEntity<Object> deletePortfolio(@PathVariable Long id) {
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		if (!portfolio.isPresent()) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_PORTFOLIO_ERROR));
		}
		portfolio.get().getPortfolioImages()
			.stream().map(PortfolioImage::getId)
			.forEach(portfolioService::deletePortfolioImageFromS3);
		portfolioService.deletePortfolio(portfolio.get());

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/portfolios/image/{id}")
	public ResponseEntity<Object> updatePortfolioImage(
		@RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {
		Optional<PortfolioImage> portfolioImage = portfolioService.getPortfolioImage(id);
		if (!portfolioImage.isPresent()) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_IMAGE_ERROR));
		}
		if (multipartFile.isEmpty()) {
			portfolioService.deletePortfolioImageFromDB(portfolioImage.get());
			portfolioService.deletePortfolioImageFromS3(id);
			return ResponseEntity.noContent().build();
		}
		File convertedFile = uploaderService.convert(multipartFile);
		if (convertedFile == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					ErrorCase.FAIL_FILE_CONVERT_ERROR));
		}
		if (!uploaderService.isValidExtension(convertedFile)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.INVALID_FILE_TYPE_ERROR));
		}
		String result = portfolioService.updatePortfolioImage(convertedFile, portfolioImage.get());
		return ResponseEntity.created(URI.create(result)).build();
	}

	@PostMapping("/portfolios/{id}/image")
	public ResponseEntity<Object> addPortfolioImage(
		@RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {
		Optional<Portfolio> portfolio = portfolioService.getPortfolio(id);
		if (!portfolio.isPresent()) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.NO_SUCH_PORTFOLIO_ERROR));
		}
		if (multipartFile.isEmpty()) {
			return ResponseEntity.badRequest()
				.body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ErrorCase.EMPTY_FILE_ERROR));
		}
		File convertedFile = uploaderService.convert(multipartFile);
		if (convertedFile == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					ErrorCase.FAIL_FILE_CONVERT_ERROR));
		}
		if (!uploaderService.isValidExtension(convertedFile)) {
			return ResponseEntity.badRequest().body(
				new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
					ErrorCase.INVALID_FILE_TYPE_ERROR));
		}
		String result = portfolioService.addPortfolioImage(convertedFile, portfolio.get());
		return ResponseEntity.created(URI.create(result)).build();
	}

	@GetMapping("/portfolios/list")
	public ResponseEntity<Object> getPortfoliosList(final Pageable pageable,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) String recruitmentField,
		@RequestParam(required = false) String keyword) {
		return ResponseEntity.ok(
			portfolioService.getPortfoliosList(pageable, category, recruitmentField, keyword));
	}
}
