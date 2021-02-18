package com.produce.pickmeup.domain.tag.history;

import java.sql.Timestamp;
import java.util.stream.Stream;

public interface TagHistoryCustomRepository {
	Stream<TagHistoryGroupByDto> findAllGroupBySumScore();
	void deleteOldScore(Timestamp standardTime);
}
