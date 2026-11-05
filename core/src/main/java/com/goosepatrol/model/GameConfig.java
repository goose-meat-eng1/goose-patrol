package com.goosepatrol.model;

/**
 * Named numbers for tuning the game, each with the requirement it serves.
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

  /** How many pieces of bread fill a goose (FR_FULLNESS). */
  public static final int GOOSE_FULLNESS = 3;

  /** How fast a full goose flies away, in map pixels per second (FR_FULLNESS). */
  public static final float GOOSE_FLY_SPEED = 220f;

  /** How long a full goose flies before it is gone, in seconds (FR_FULLNESS). */
  public static final float GOOSE_FLY_SECONDS = 2f;

  /** Seconds with no geese at the start of a run (FR_SPAWN). */
  public static final float CALM_START_SECONDS = 10f;

  /** How many geese the first wave brings (FR_SPAWN). */
  public static final int FIRST_WAVE_SIZE = 3;

  /** How many more geese each wave brings (FR_SPAWN). */
  public static final int WAVE_GROWTH = 1;

  /** Seconds between the first wave and the second (FR_SPAWN). */
  public static final float FIRST_WAVE_GAP = 10f;

  /** How many seconds shorter each gap between waves gets (FR_SPAWN). */
  public static final float WAVE_GAP_SHRINK = 0.5f;

  /** The shortest gap between waves, in seconds (FR_SPAWN). */
  public static final float MIN_WAVE_GAP = 5f;

  /** The most geese on the map at once (FR_SPAWN, NFR_PERFORMANCE). */
  public static final int MAX_GEESE = 100;

  /** How far from the student a goose arrives, in map pixels, so off screen (FR_SPAWN). */
  public static final float SPAWN_DISTANCE = 360f;

  /** Seconds between two pieces of bread (FR_BREAD). */
  public static final float BREAD_GAP_SECONDS = 0.5f;

  /** How fast bread flies, in map pixels per second (FR_BREAD). */
  public static final float BREAD_SPEED = 240f;

  /** How close a goose must be for the dispenser to throw at it, in map pixels (FR_BREAD). */
  public static final float BREAD_RANGE = 200f;

  /** Scale for the map rendering. */
  public static final float MAP_SCALE = 8f;

  private GameConfig() {
  }
}
