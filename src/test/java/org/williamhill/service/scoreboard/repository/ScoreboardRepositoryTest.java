package org.williamhill.service.scoreboard.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.williamhill.service.scoreboard.repository.exception.EventExistsException;
import org.williamhill.service.scoreboard.repository.exception.EventNotFoundException;
import org.williamhill.service.scoreboard.repository.mongo.SpringDataMongoScoreboardRepository;
import org.williamhill.service.scoreboard.repository.mongo.domain.Scoreboard;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ScoreboardRepositoryTest {

  private static final String EVENT = "A vs B";
  private static final String NIL_NIL = "0-0";
  private static final String ONE_ZERO = "1-0";

  private ScoreboardRepository sut;

  @Mock
  private SpringDataMongoScoreboardRepository mongoScoreboardRepository;

  @BeforeEach
  public void setUp() {
    this.sut = new ScoreboardRepository(mongoScoreboardRepository);
  }

  @Test
  void createReturnsScoreboardDto() {
    when(mongoScoreboardRepository.save(any(Scoreboard.class))).thenReturn(Mono.just(new Scoreboard(EVENT, NIL_NIL)));

    StepVerifier.create(this.sut.create(EVENT, NIL_NIL))
        .consumeNextWith(scoreboardDto -> {
          assertThat(scoreboardDto).isNotNull();
          assertThat(scoreboardDto.isPresent()).isTrue();
          assertThat(scoreboardDto.getEvent()).isEqualTo(EVENT);
          assertThat(scoreboardDto.getScore()).isEqualTo("0-0");

          verify(mongoScoreboardRepository, times(1)).save(any(Scoreboard.class));
        }).verifyComplete();
  }

  @Test
  void createReturnsEventExistsException() {
    when(mongoScoreboardRepository.save(any(Scoreboard.class))).thenReturn(Mono.error(new DuplicateKeyException(EVENT)));

    StepVerifier.create(this.sut.create(EVENT, NIL_NIL))
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(EventExistsException.class);
          verify(mongoScoreboardRepository, times(1)).save(any(Scoreboard.class));
        }).verify();
  }

  @Test
  void searchByReturnsScoreboardDto() {
    when(mongoScoreboardRepository.findScoreboardByEvent(anyString())).thenReturn(Mono.just(new Scoreboard(EVENT, NIL_NIL)));

    StepVerifier.create(this.sut.searchBy(EVENT))
        .consumeNextWith(scoreboardDto -> {
          assertThat(scoreboardDto).isNotNull();
          assertThat(scoreboardDto.isPresent()).isTrue();
          assertThat(scoreboardDto.getEvent()).isEqualTo(EVENT);
          verify(mongoScoreboardRepository, times(1)).findScoreboardByEvent(anyString());
        }).verifyComplete();
  }

  @Test
  void searchByReturnsEventNotFoundException() {
    when(mongoScoreboardRepository.findScoreboardByEvent(anyString())).thenReturn(Mono.empty());

    StepVerifier.create(this.sut.searchBy(EVENT))
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(EventNotFoundException.class);
          verify(mongoScoreboardRepository, times(1)).findScoreboardByEvent(anyString());
        }).verify();
  }

  @Test
  void updateScoreOnReturnsScoreboardDto() {
    when(mongoScoreboardRepository.findScoreboardByEvent(anyString())).thenReturn(Mono.just(new Scoreboard(EVENT, NIL_NIL)));
    when(mongoScoreboardRepository.save(any(Scoreboard.class))).thenReturn(Mono.just(new Scoreboard(EVENT, ONE_ZERO)));

    StepVerifier.create(this.sut.updateScoreOn(EVENT, ONE_ZERO))
        .consumeNextWith(scoreboardDto -> {
          assertThat(scoreboardDto).isNotNull();
          assertThat(scoreboardDto.isPresent()).isTrue();
          assertThat(scoreboardDto.getEvent()).isEqualTo(EVENT);
          assertThat(scoreboardDto.getScore()).isEqualTo(ONE_ZERO);
          verify(mongoScoreboardRepository, times(1)).findScoreboardByEvent(anyString());
          verify(mongoScoreboardRepository, times(1)).save(any(Scoreboard.class));
        }).verifyComplete();
  }

  @Test
  void updateScoreOnReturnsEventNotFoundException() {
    when(mongoScoreboardRepository.findScoreboardByEvent(anyString())).thenReturn(Mono.empty());

    StepVerifier.create(this.sut.updateScoreOn(EVENT, ONE_ZERO))
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(EventNotFoundException.class);
          verify(mongoScoreboardRepository, times(1)).findScoreboardByEvent(anyString());
          verifyNoMoreInteractions(mongoScoreboardRepository);
        }).verify();
  }

  @Test
  void deleteBySucceeds() {
    when(mongoScoreboardRepository.findScoreboardByEvent(anyString())).thenReturn(Mono.just(new Scoreboard(EVENT, NIL_NIL)));
    when(mongoScoreboardRepository.delete(any(Scoreboard.class))).thenReturn(Mono.empty());

    StepVerifier.create(this.sut.deleteBy(EVENT))
        .expectComplete()
        .verify();

    verify(mongoScoreboardRepository, times(1)).findScoreboardByEvent(anyString());
    verify(mongoScoreboardRepository, times(1)).delete(any(Scoreboard.class));
  }

  @Test
  void deleteByReturnsEventNotFoundException() {
    when(mongoScoreboardRepository.findScoreboardByEvent(anyString())).thenReturn(Mono.empty());

    StepVerifier.create(this.sut.updateScoreOn(EVENT, ONE_ZERO))
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(EventNotFoundException.class);
          verify(mongoScoreboardRepository, times(1)).findScoreboardByEvent(anyString());
          verifyNoMoreInteractions(mongoScoreboardRepository);
        }).verify();
  }
}
