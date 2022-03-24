package org.williamhill.service.scoreboard.controller.model;

import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class Event {

  @NotBlank
  String event;
}
