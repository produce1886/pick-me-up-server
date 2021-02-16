package com.produce.pickmeup.domain.tag.history;

import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class TagHistoryCustomRepositoryImpl implements TagHistoryCustomRepository {
	private final EntityManager entityManager;
	private final int LIMIT_TAG_COUNT = 10;

	@Override
	public Stream<TagHistoryGroupByDto> findAllGroupBySumScore() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagHistoryGroupByDto> query = builder.createQuery(TagHistoryGroupByDto.class);
		Root<TagHistory> root = query.from(TagHistory.class);

		query.groupBy(root.get("tag"));
		query.multiselect(root.get("tag"), builder.sum(root.get("score")));
		query.orderBy(builder.desc(root.get("score")));

		return entityManager.createQuery(query).getResultStream().limit(LIMIT_TAG_COUNT);
	}
}
