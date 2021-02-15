package com.produce.pickmeup.service;

import com.produce.pickmeup.domain.tag.HotTagDto;
import com.produce.pickmeup.domain.tag.Tag;
import com.produce.pickmeup.domain.tag.TagRepository;
import com.produce.pickmeup.domain.tag.history.TagHistory;
import com.produce.pickmeup.domain.tag.history.TagHistoryGroupByDto;
import com.produce.pickmeup.domain.tag.history.TagHistoryRepository;
import java.sql.Timestamp;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TagService {
	private final TagRepository tagRepository;
	private final TagHistoryRepository tagHistoryRepository;

	//@Scheduled(cron = "0 0 0/1 * * ?") // save score history every 1 hour
	@Scheduled(cron = "0 0 0/6 * * ?") // save score history every 6 hours
	public void updateScore() {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		System.out.println("[*] " + currentTime + ": update score START");
		tagRepository.findAll().forEach(tag -> addTagHistory(tag, currentTime));
		System.out.println("[*] " + currentTime + ": update score END");
	}

	//@Scheduled(cron = "0/5 * * * * ?") // for testing
	//@Scheduled(cron = "0 0 6 * * ?") // everyday at 6AM
	public void removeOldHistory() {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		System.out.println("[*] remove history START");
		System.out.println("[*] remove history END");
	}

	@Transactional
	public void addTagHistory(Tag tag, Timestamp timestamp) {
		tagHistoryRepository.save(TagHistory.builder()
			.score(tag.getCurrentScore())
			.tag(tag)
			.timestamp(timestamp)
			.build());
		tag.resetScore();
	}

	public HotTagDto getHotTagsList() {
		return new HotTagDto(tagHistoryRepository.findAllGroupBySumScore()
			.map(TagHistoryGroupByDto::getTag)
			.map(Tag::toTagDto)
			.collect(Collectors.toList()));
	}
}
