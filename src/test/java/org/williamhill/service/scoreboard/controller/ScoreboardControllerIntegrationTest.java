package org.williamhill.service.scoreboard.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.williamhill.service.scoreboard.controller.viewmodel.ScoreboardViewModel;
import org.williamhill.service.scoreboard.repository.ScoreboardRepository;
import org.williamhill.service.scoreboard.repository.exception.EventNotFoundException;
import org.williamhill.service.scoreboard.repository.model.ScoreboardDto;
import reactor.core.publisher.Mono;

@WebFluxTest(ScoreboardController.class)
class ScoreboardControllerIntegrationTest {

  private static final String EVENT = "A vs B";

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private ScoreboardRepository repository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldGet200OkWhenGettingScoreboardByEvent() throws JsonProcessingException {
    var expected = new ScoreboardDto("1", EVENT, "0-0");
    given(repository.searchBy(anyString())).willReturn(Mono.just(expected));

    this.webTestClient.get()
        .uri("/api/1/scoreboard-events?event={event}", EVENT)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody().json(objectMapper.writeValueAsString(ScoreboardViewModel.from(expected)));

    verify(repository, times(1)).searchBy(anyString());
  }

  @Test
  void shouldGet404NotFoundWhenGettingScoreboardByEvent() {
    given(repository.searchBy(anyString())).willReturn(Mono.error(new EventNotFoundException()));

    this.webTestClient.get()
        .uri("/api/1/scoreboard-events?event={event}", EVENT)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.timestamp").isNotEmpty()
        .jsonPath("$.error").isEqualTo("Not Found")
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.message").isEqualTo("event with the name ".concat(EVENT).concat(" not found"))
        .jsonPath("$.path").isEqualTo("/api/1/scoreboard-events");

    verify(repository, times(1)).searchBy(anyString());
  }
}