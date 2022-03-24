package org.williamhill.service.scoreboard.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.williamhill.service.scoreboard.repository.exception.EventExistsException;
import org.williamhill.service.scoreboard.repository.exception.EventNotFoundException;
import org.williamhill.service.scoreboard.repository.model.ScoreboardDto;
import org.williamhill.service.scoreboard.repository.mongo.SpringDataMongoScoreboardRepository;
import org.williamhill.service.scoreboard.repository.mongo.domain.Scoreboard;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
public class ScoreboardRepository {

  private final SpringDataMongoScoreboardRepository mongoScoreboardRepository;

  public Mono<ScoreboardDto> create(final String event, final String score) {
    log.info("enter create scoreboard event = {}", event);
    return mongoScoreboardRepository.save(new Scoreboard(event, score))
        .map(ScoreboardDto::from)
        .onErrorMap(DuplicateKeyException.class, e -> new EventExistsException())
        .doOnSuccess(result -> log.info("exit create result = {}", result));
  }

  public Mono<ScoreboardDto> searchBy(final String event) {
    log.info("enter searchBy event = {}", event);
    return mongoScoreboardRepository.findScoreboardByEvent(event)
        .map(ScoreboardDto::from)
        .switchIfEmpty(Mono.error(new EventNotFoundException()))
        .doOnSuccess(result -> log.info("exit searchBy scoreboardDto = {}", result));
  }

  public Mono<ScoreboardDto> updateScoreOn(final String event, final String score) {
    log.info("enter updateScoreOn event = {} score = {}", event, score);
    return searchBy(event)
        .map(Scoreboard::from)
        .flatMap(scoreboard -> {
          scoreboard.setScore(score);
          return mongoScoreboardRepository.save(scoreboard);
        })
        .map(ScoreboardDto::from)
        .onErrorMap(EventNotFoundException.class, e -> e)
        .doOnSuccess(scoreboardDto -> log.info("exit updateScoreOn scoreboardDto = {}", scoreboardDto));
  }

  public Mono<Void> deleteBy(final String event) {
    log.info("enter deleteBy event = {}", event);
    return searchBy(event)
        .map(Scoreboard::from)
        .flatMap(mongoScoreboardRepository::delete)
        .onErrorMap(EventNotFoundException.class, e -> e)
        .doOnSuccess(rs -> log.info("exit deleteBy"));
  }

}
