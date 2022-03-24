package org.williamhill.service.scoreboard.repository.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.williamhill.service.scoreboard.repository.mongo.domain.Scoreboard;
import reactor.core.publisher.Mono;

public interface SpringDataMongoScoreboardRepository extends ReactiveMongoRepository<Scoreboard, String> {

  Mono<Scoreboard> findScoreboardByEvent(final String event);

  Mono<Void> deleteScoreboardByEvent(final String event);
}
