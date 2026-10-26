package com.goosepatrol.model;

/**
 * One run of the game, from the start prompt to the end screen (D2, D3).
 *
 * <p>It holds the run's {@link RunState} and its {@link GameClock}. {@link #update(float)} changes them only while
 * the run is PLAYING, so nothing moves while the run waits to start, is paused or has ended. Later slices add the
 * student, the map, the counters, the items, the events and the geese here.
 */
public class GameSession {

  private final GameClock clock;

  // FR_PAUSE: every run opens waiting for the start key.
  private RunState state = RunState.READY;

  /**
   * Makes a run that waits for the start key. The game builds runs with {@link GameSetup#newSession()}; tests
   * can pass in a clock of their own.
   *
   * @param clock the clock for this run
   */
  public GameSession(GameClock clock) {
    this.clock = clock;
  }

  /**
   * Starts the run: READY becomes PLAYING (FR_PAUSE, D3). In any other state it does nothing.
   */
  public void start() {
    if (state == RunState.READY) {
      state = RunState.PLAYING;
    }
  }

  /**
   * Pauses or resumes the run: PLAYING becomes PAUSED, and PAUSED becomes PLAYING (FR_PAUSE, D3). The next call
   * to {@link #update(float)} already sees the new state, so a pause takes effect in the same frame
   * (FR_PAUSE). Before the start and after the end it does nothing.
   */
  public void togglePause() {
    if (state == RunState.PLAYING) {
      state = RunState.PAUSED;
    } else if (state == RunState.PAUSED) {
      state = RunState.PLAYING;
    }
  }

  /**
   * Moves the run on by one frame (D4). Only a PLAYING run changes: the clock counts down, and the run ends when
   * the time is up (FR_TIMER). A frame longer than {@link GameConfig#MAX_FRAME_SECONDS} counts as
   * that much.
   *
   * @param delta the frame's length in seconds, as libGDX gives it
   */
  public void update(float delta) {
    if (state != RunState.PLAYING) {
      return; // FR_PAUSE: nothing changes while paused, before the start or after the end
    }
    clock.tick(Math.min(delta, GameConfig.MAX_FRAME_SECONDS));
    if (clock.isUp()) {
      state = RunState.ENDED; // D3: when(timeLeft = 0)
    }
  }

  /**
   * Returns the state the run is in.
   *
   * @return READY, PLAYING, PAUSED or ENDED
   */
  public RunState getState() {
    return state;
  }

  /**
   * Returns the time left in the run, for the HUD (FR_TIMER).
   *
   * @return the time left in seconds, from 300 down to 0
   */
  public double getTimeLeft() {
    return clock.getTimeLeft();
  }
}
