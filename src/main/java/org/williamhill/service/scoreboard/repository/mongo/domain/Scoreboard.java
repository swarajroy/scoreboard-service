package org.williamhill.service.scoreboard.repository.mongo.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "scoreboard")
public class Scoreboard {

  @Id
  String id;

  @Indexed(unique = true)
  String event;

  String score;

  public Scoreboard(final String event) {
    this.event = event;
    this.score = "0-0";
  }
}
