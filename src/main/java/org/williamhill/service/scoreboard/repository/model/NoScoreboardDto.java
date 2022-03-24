package org.williamhill.service.scoreboard.repository.model;

public final class NoScoreboardDto extends ScoreboardDto {
  private static final NoScoreboardDto INSTANCE = new NoScoreboardDto();

  public NoScoreboardDto() {
    super(null, "", "");
  }


  public static NoScoreboardDto create() {
    return INSTANCE;
  }

  @Override
  public boolean isPresent() {
    return false;
  }
}
