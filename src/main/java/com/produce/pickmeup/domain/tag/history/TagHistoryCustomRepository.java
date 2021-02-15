package com.produce.pickmeup.domain.tag.history;

import java.util.stream.Stream;

public interface TagHistoryCustomRepository {
	Stream<TagHistoryGroupByDto> findAllGroupBySumScore();
}
