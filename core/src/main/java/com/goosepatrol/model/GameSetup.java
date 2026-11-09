package com.goosepatrol.model;

import java.util.Random;

/**
 * Builds each new run with everything it starts wit the 5:00 clock, the student at the map's starting place,
 * and the geese's waves (FR_SPAWN).
 */
public final class GameSetup {

  private GameSetup() {
  }

  /**
   * Builds a new run on a map, waiting for the start key (FR_PAUSE).
   */
  public static GameSession newSession(CampusMap map) {
    return new GameSession(new GameClock(GameConfig.RUN_SECONDS), map, new Player(map.getStudentStart()),
        new GooseSpawner(new Random()));
  }
}
