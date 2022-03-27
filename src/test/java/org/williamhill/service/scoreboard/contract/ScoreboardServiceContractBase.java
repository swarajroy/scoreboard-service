package org.williamhill.service.scoreboard.contract;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.williamhill.service.scoreboard.ScoreboardServiceApplication;
import org.williamhill.service.scoreboard.repository.mongo.domain.Scoreboard;

@SpringBootTest(classes = ScoreboardServiceApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"spring.mongodb.embedded.version=3.5.5", "server.servlet.context-path=/scoreboard-service"})
@ExtendWith(SpringExtension.class)
@Slf4j
public class ScoreboardServiceContractBase {

  @LocalServerPort
  private int port;

  @Autowired
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @BeforeEach
  public void setup() {
    RestAssured.baseURI = "http://localhost:" + port;
    reactiveMongoTemplate.save(new Scoreboard("A vs B", "0-0")).subscribe();
  }

  @AfterEach
  public void tearDown() {
    reactiveMongoTemplate.dropCollection(Scoreboard.class).subscribe();
  }

}
