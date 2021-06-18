package com.produce.pickmeup.domain.tag.history;

import java.sql.Timestamp;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
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

	@Override
	@Transactional
	public void deleteOldScore(Timestamp standardTime) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaDelete<TagHistory> deleteQuery = builder.createCriteriaDelete(TagHistory.class);
		Root<TagHistory> root = deleteQuery.from(TagHistory.class);
		Predicate deleteCondition = builder
			.lessThanOrEqualTo(root.get("createdDate"), standardTime);
		deleteQuery.where(deleteCondition);
		entityManager.createQuery(deleteQuery).executeUpdate();
	}
}
