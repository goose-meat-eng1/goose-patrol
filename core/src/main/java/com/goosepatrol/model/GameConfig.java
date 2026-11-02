package com.goosepatrol.model;

/**
 * Named numbers for tuning the game, each with the requirement it serves. Later slices add the calm start and the
 * gap between geese here.
 */
public final class GameConfig {

  /** The length of one run in seconds: 5:00 (FR_TIMER). */
  public static final float RUN_SECONDS = 5 * 60f;

  /**
   * The longest frame the clock counts, in seconds. libGDX doesn't cap its frame time, so a frame stalled while
   * the window is dragged could take several seconds off the timer. Such a frame counts as this much instead
   * (FR_TIMER). A normal frame at 60 fps is about 0.017 s, so it is never cut.
   */
  public static final float MAX_FRAME_SECONDS = 0.1f;

  /** The student's top speed, in map pixels per second (FR_MOVE). */
  public static final float STUDENT_MAX_SPEED = 120f;

  /**
   * How long the student takes to go from standing still to full speed, in seconds (FR_MOVE). The speed rises
   * evenly, so it is half of full speed after half this time, in any direction and at any frame rate.
   */
  public static final float STUDENT_SPEED_UP_SECONDS = 0.5f;

  /**
   * How long the student takes to stop from full speed, in seconds (FR_MOVE). Turning back sheds speed at the same
   * rate. At 0.5 s the student slides about 30 map pixels after the keys are let go.
   */
  public static final float STUDENT_STOP_SECONDS = 0.5f;

  /** The constant velocity the geese will approach the player. (FR_GOOSE_MOVE)*/
  public static final float GOOSE_VELOCITY = 100f;

  /** Scale for the map rendering. */
  public static final float MAP_SCALE = 8f;

  private GameConfig() {
  }
}
