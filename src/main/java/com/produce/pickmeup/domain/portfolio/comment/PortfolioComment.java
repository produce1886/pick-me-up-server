package com.produce.pickmeup.domain.portfolio.comment;

import com.produce.pickmeup.domain.portfolio.Portfolio;
import com.produce.pickmeup.domain.user.User;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Entity
@Table(name = "portfolio_comments")
@NoArgsConstructor
public class PortfolioComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
    @Column(updatable = false)
    @CreatedDate
    private Timestamp createdDate;
    @Column
    @LastModifiedDate
    private Timestamp modifiedDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    @Column(nullable = false)
    private String authorEmail;
    @Column(nullable = false)
    private String content;

    @Builder
    public PortfolioComment(User author, String content, Portfolio portfolio) {
        this.author = author;
        this.authorEmail = author.getEmail();
        this.content = content;
        this.portfolio = portfolio;
        this.createdDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = new Timestamp(System.currentTimeMillis());
    }

    public PortfolioCommentResponseDto toResponseDto() {
        return PortfolioCommentResponseDto.builder()
            .id(id)
            .createdDate(createdDate)
            .modifiedDate(modifiedDate)
            .content(content)
            .user(author.toResponseDto())
            .build();
    }

    public PortfolioCommentDetailResponseDto toDetailResponseDto() {
        return PortfolioCommentDetailResponseDto.builder()
            .id(id)
            .authorEmail(authorEmail)
            .content(content)
            .createdDate(createdDate)
            .modifiedDate(modifiedDate)
            .build();
    }

    public void updateContent(PortfolioCommentRequestDto portfolioCommentUpdateDto) {
        this.content = portfolioCommentUpdateDto.getContent();
        this.modifiedDate = new Timestamp(System.currentTimeMillis());
    }

    public boolean included(Long portfolioId) {
        return this.portfolio.getId() == portfolioId;
    }

    public boolean authorCheck(String authorEmail) {
        return this.authorEmail.equals(authorEmail);
    }
}
