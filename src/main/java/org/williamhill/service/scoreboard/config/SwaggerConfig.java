package org.williamhill.service.scoreboard.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SwaggerConfig {

  @Bean
  public OpenAPI scoreboardAPI() {
    return new OpenAPI()
        .info(new Info().title("Scoreboard API")
            .description("Scoreboard API application")
            .version("v0.0.1"));
  }

  @Bean
  public OpenApiCustomiser microTypeOpenApiCustomiser() {
    return openApi -> openApi.getComponents()
        .addSchemas("Event", new StringSchema());
  }
}
