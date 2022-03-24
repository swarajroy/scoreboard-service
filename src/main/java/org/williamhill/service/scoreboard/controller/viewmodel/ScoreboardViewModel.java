package org.williamhill.service.scoreboard.controller.viewmodel;

import lombok.Value;
import org.williamhill.service.scoreboard.repository.model.ScoreboardDto;

@Value
public class ScoreboardViewModel {
  String event;
  String score;

  public static ScoreboardViewModel from(final ScoreboardDto scoreboardDto) {
    return new ScoreboardViewModel(scoreboardDto.getEvent(), scoreboardDto.getScore());
  }
}
