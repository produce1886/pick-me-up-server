package com.produce.pickmeup.domain.tag.history;

import org.springframework.data.repository.CrudRepository;

public interface TagHistoryRepository extends CrudRepository<TagHistory, Long>, TagHistoryCustomRepository {
}
