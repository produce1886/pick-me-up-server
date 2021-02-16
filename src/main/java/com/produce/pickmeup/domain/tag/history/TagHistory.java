package com.produce.pickmeup.domain.tag.history;

import com.produce.pickmeup.domain.tag.Tag;
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

@Entity
@Getter
@Table(name = "tag_history")
@NoArgsConstructor
public class TagHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne
	@JoinColumn(name = "tag_id")
	private Tag tag;
	@Column(updatable = false)
	@CreatedDate
	private Timestamp createdDate;
	@Column(nullable = false)
	private long score;

	@Builder
	public TagHistory(Tag tag, long score, Timestamp timestamp) {
		this.tag = tag;
		this.score = score;
		this.createdDate = timestamp;
	}
}
