package com.goosepatrol.model;

/**
 * Builds each new run with everything it starts with (D2): the 5:00 clock, and the student at the map's starting
 * place. Later slices add the items and the events here, so adding one changes only this class (NFR_MAINTAIN).
 */
public final class GameSetup {

  private GameSetup() {
  }

  /**
   * Builds a new run on a map, waiting for the start key (FR_PAUSE).
   *
   * @param map the campus to play on
   * @return the new run
   */
  public static GameSession newSession(CampusMap map) {
    return new GameSession(new GameClock(GameConfig.RUN_SECONDS), map, new Player(map.getStudentStart()));
  }
}
