package com.goosepatrol.model;

/**
 * One run of the game, from the start prompt to the end screen (D2, D3).
 *
 * <p>It holds the run's {@link RunState}, its {@link GameClock}, the student and the map. {@link #update(float)}
 * changes them only while the run is PLAYING, so nothing moves while the run waits to start, is paused or has ended.
 * Later slices add the counters, the items, the events and the geese here.
 */
public class GameSession {

  private final GameClock clock;
  private final CampusMap map;
  private final Player player;

  // The direction the movement keys point, as the key handler last set it (FR_MOVE).
  private int moveX;
  private int moveY;

  // FR_PAUSE: every run opens waiting for the start key.
  private RunState state = RunState.READY;

  /**
   * Makes a run that waits for the start key. The game builds runs with {@link GameSetup#newSession(CampusMap)};
   * tests can pass in parts of their own.
   *
   * @param clock the clock for this run
   * @param map the campus to play on
   * @param player the student, at their starting place
   */
  public GameSession(GameClock clock, CampusMap map, Player player) {
    this.clock = clock;
    this.map = map;
    this.player = player;
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
   * Sets the direction the movement keys point, for the next frames (FR_MOVE). The key handler calls this every
   * frame.
   *
   * @param x the left-right direction: -1 for left, 0, or 1 for right
   * @param y the down-up direction: -1 for down, 0, or 1 for up
   */
  public void setMoveInput(int x, int y) {
    moveX = x;
    moveY = y;
  }

  /**
   * Moves the run on by one frame (D4). Only a PLAYING run changes: the clock counts down, the run ends when the
   * time is up (FR_TIMER), and otherwise the student moves (FR_MOVE). A frame longer than
   * {@link GameConfig#MAX_FRAME_SECONDS} counts as that much.
   *
   * @param delta the frame's length in seconds, as libGDX gives it
   */
  public void update(float delta) {
    if (state != RunState.PLAYING) {
      return; // FR_PAUSE: nothing changes while paused, before the start or after the end
    }
    float frame = Math.min(delta, GameConfig.MAX_FRAME_SECONDS);
    clock.tick(frame);
    if (clock.isUp()) {
      state = RunState.ENDED; // D3: when(timeLeft = 0)
      return;
    }
    player.update(moveX, moveY, frame, map);
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

  /**
   * Returns the student, for drawing and for tests.
   *
   * @return the student
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns the campus this run is played on.
   *
   * @return the map
   */
  public CampusMap getMap() {
    return map;
  }
}
