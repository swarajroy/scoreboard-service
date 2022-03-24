package org.williamhill.service.scoreboard.repository.model;

import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.williamhill.service.scoreboard.repository.mongo.domain.Scoreboard;

@Value
@NonFinal
@ToString(exclude = {"id"})
public class ScoreboardDto {
  String id;
  String event;
  String score;

  public static ScoreboardDto from(final Scoreboard scoreboard) {
    return new ScoreboardDto(scoreboard.getId(), scoreboard.getEvent(), scoreboard.getScore());
  }

  public boolean isPresent() {
    return true;
  }
}
