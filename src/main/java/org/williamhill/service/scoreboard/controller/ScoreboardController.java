package org.williamhill.service.scoreboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.williamhill.service.scoreboard.controller.model.Event;
import org.williamhill.service.scoreboard.controller.viewmodel.ScoreboardViewModel;
import org.williamhill.service.scoreboard.repository.ScoreboardRepository;
import org.williamhill.service.scoreboard.repository.exception.EventNotFoundException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/1")
@AllArgsConstructor
@Tag(name = "scoreboard")
@Slf4j
public class ScoreboardController {

  private final ScoreboardRepository repository;

  @GetMapping(value = "/scoreboard-events", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(operationId = "getScoreboardEventByName_v1", description = "get highest price")
  @ApiResponses(
      @ApiResponse(responseCode = "200", description = "get scoreboard by event")
  )
  public Mono<ResponseEntity<ScoreboardViewModel>> getScoreboardByEvent(@RequestParam final Event event) {
    log.info("enter getScoreboardByEvent event = {}", event);
    return repository.searchBy(event.getEvent())
        .map(ScoreboardViewModel::from)
        .map(ResponseEntity::ok)
        .onErrorResume(EventNotFoundException.class, e -> {
          var errorMessage = "event with the name ".concat(event.getEvent()).concat(" not found");
          return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage));
        })
        .doOnSuccess(rs -> log.info("exit getScoreboardByEvent rs = {}", rs));
  }
}
