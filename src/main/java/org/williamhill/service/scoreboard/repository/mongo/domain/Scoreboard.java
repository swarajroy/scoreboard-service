package org.williamhill.service.scoreboard.repository.mongo.domain;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.williamhill.service.scoreboard.repository.model.ScoreboardDto;

@Data
@Document(collection = "scoreboard")
@ToString(exclude = {"id"})
public class Scoreboard {

  @Id
  String id;

  @Indexed(unique = true)
  String event;

  String score;

  public Scoreboard(final String event, final String score) {
    this.event = event;
    this.score = score;
  }

  public static Scoreboard from(final ScoreboardDto scoreboardDto) {
    return new Scoreboard(scoreboardDto.getEvent(), scoreboardDto.getScore());
  }
}
