package com.produce.pickmeup.controller;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.portfolio.PortfolioRequestDto;
import com.produce.pickmeup.domain.portfolio.image.PortfolioImage;
import com.produce.pickmeup.domain.user.User;
import com.produce.pickmeup.error.exception.EmptyFileException;
import com.produce.pickmeup.error.exception.FileConvertException;
import com.produce.pickmeup.error.exception.InvalidAccessException;
import com.produce.pickmeup.error.exception.InvalidFieldException;
import com.produce.pickmeup.error.exception.InvalidFileException;
import com.produce.pickmeup.error.exception.NoImageException;
import com.produce.pickmeup.error.exception.NoPortfolioException;
import com.produce.pickmeup.error.exception.NoUserException;
import com.produce.pickmeup.service.PortfolioService;
import com.produce.pickmeup.service.S3Uploader;
import com.produce.pickmeup.service.UserService;
import java.io.File;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
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

  // TODO replcae to @Valid
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
      throw new InvalidFieldException();
    }
    User author = userService.findByEmail(portfolioRequestDto.getAuthorEmail())
        .orElseThrow(NoUserException::new);

    String result = portfolioService.addPortfolio(portfolioRequestDto, author);
    return ResponseEntity.created(URI.create("/portfolios/" + result)).build();
  }

  @GetMapping("/portfolios/{id}")
  public ResponseEntity<Object> getPortfolioDetail(@PathVariable Long id) {
    Portfolio portfolio = portfolioService.getPortfolio(id)
        .orElseThrow(NoPortfolioException::new);

    return ResponseEntity.ok(portfolioService.getPortfolioDetail(portfolio));
  }

  @PutMapping("/portfolios/{id}")
  public ResponseEntity<Object> updatePortfolio
      (@PathVariable Long id, @RequestBody PortfolioRequestDto portfolioRequestDto) {

    if (!isRequestBodyValid(portfolioRequestDto)) {
      throw new InvalidFieldException();
    }
    Portfolio portfolio = portfolioService.getPortfolio(id)
        .orElseThrow(NoPortfolioException::new);
    if (!portfolio.authorCheck(portfolioRequestDto.getAuthorEmail())) {
      throw new InvalidAccessException();
    }

    portfolioService.updatePortfolio(portfolio, portfolioRequestDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/portfolios/{id}")
  public ResponseEntity<Object> deletePortfolio(@PathVariable Long id) {
    Portfolio portfolio = portfolioService.getPortfolio(id)
        .orElseThrow(NoPortfolioException::new);

    portfolio.getPortfolioImages()
        .stream().map(PortfolioImage::getId)
        .forEach(portfolioService::deletePortfolioImageFromS3);
    portfolioService.deletePortfolio(portfolio);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/portfolios/image/{id}")
  public ResponseEntity<Object> updatePortfolioImage(
      @RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {

    PortfolioImage portfolioImage = portfolioService.getPortfolioImage(id)
        .orElseThrow(NoImageException::new);
    if (multipartFile.isEmpty()) {
      throw new InvalidFileException();
    }

    File convertedFile = uploaderService.convert(multipartFile);
    if (convertedFile == null) {
      throw new FileConvertException();
    }
    if (!uploaderService.isValidExtension(convertedFile)) {
      throw new InvalidFileException();
    }

    String result = portfolioService.updatePortfolioImage(convertedFile, portfolioImage);
    return ResponseEntity.created(URI.create(result)).build();
  }

  @DeleteMapping("/portfolios/image/{id}")
  public ResponseEntity<Object> deletePortfolioImage(@PathVariable Long id) {
    PortfolioImage portfolioImage = portfolioService.getPortfolioImage(id)
        .orElseThrow(NoImageException::new);

    portfolioService.deletePortfolioImageFromDB(portfolioImage);
    portfolioService.deletePortfolioImageFromS3(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/portfolios/{id}/image")
  public ResponseEntity<Object> addPortfolioImage(
      @RequestParam("image") MultipartFile multipartFile, @PathVariable Long id) {

    Portfolio portfolio = portfolioService.getPortfolio(id)
        .orElseThrow(NoPortfolioException::new);

    if (multipartFile.isEmpty()) {
      throw new EmptyFileException();
    }

    File convertedFile = uploaderService.convert(multipartFile);
    if (convertedFile == null) {
      throw new FileConvertException();
    }
    if (!uploaderService.isValidExtension(convertedFile)) {
      throw new InvalidFileException();
    }

    String result = portfolioService.addPortfolioImage(convertedFile, portfolio);
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
