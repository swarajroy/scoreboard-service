package org.williamhill.service.scoreboard.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.williamhill.service.scoreboard.repository.mongo.domain.Scoreboard;
import reactor.test.StepVerifier;

@DataMongoTest(properties = {"spring.mongodb.embedded.version=3.5.5"})
@DirtiesContext
class SpringDataMongoScoreboardRepositoryTest {

  private static final String EVENT = "Man city v Man utd";
  private static final String NIL_NIL = "0-0";

  @Autowired
  private SpringDataMongoScoreboardRepository repository;

  @BeforeEach
  public void setUp() {
    var scoreboard = new Scoreboard(EVENT, NIL_NIL);
    repository.save(scoreboard).subscribe();
  }

  @Test
  void shouldFindScoreboardByEvent() {

    StepVerifier.create(repository.findScoreboardByEvent(EVENT))
        .consumeNextWith(result -> {
          assertThat(result).isNotNull();
          assertThat(result.getId()).isNotBlank();
          assertThat(result.getEvent()).isEqualTo(EVENT);
          assertThat(result.getScore()).isEqualTo(NIL_NIL);
        }).verifyComplete();
  }

  @Test
  void shouldGetDuplicateKeyException() {
    var scoreboard = new Scoreboard(EVENT, NIL_NIL);

    StepVerifier.create(repository.save(scoreboard))
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isNotNull();
          assertThat(throwable).isInstanceOf(DuplicateKeyException.class);
        }).verify();
  }

  @Test
  void shouldDeleteScoreboardByEvent() {

    StepVerifier.create(repository.findScoreboardByEvent(EVENT).flatMap(entity -> repository.delete(entity).then()))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldUpdateScoreboard() {
    StepVerifier.create(repository.findScoreboardByEvent(EVENT).flatMap(entity -> {
      entity.setScore("1-0");
      return repository.save(entity);
    })).consumeNextWith(result -> {
      assertThat(result).isNotNull();
      assertThat(result.getScore()).isNotEqualTo(NIL_NIL);
    }).verifyComplete();
  }
}