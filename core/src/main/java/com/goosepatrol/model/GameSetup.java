package com.goosepatrol.model;

/**
 * Builds each new run with everything it starts with (D2). In slice 1 that is only the 5:00 clock. Later slices
 * add the map, the student, the items and the events here, so adding one changes only this class (QR_EXTENSIBLE).
 */
public final class GameSetup {

  private GameSetup() {
  }

  /**
   * Builds a new run that waits for the start key (FR_START_PAUSED).
   *
   * @return the new run
   */
  public static GameSession newSession() {
    return new GameSession(new GameClock(GameConfig.RUN_SECONDS));
  }
}
