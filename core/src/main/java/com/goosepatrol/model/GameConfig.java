package com.goosepatrol.model;

/**
 * Named numbers for tuning the game, each with the requirement it serves.
 */
public final class GameConfig {

  /** The length of one run in seconds, 300 for 5:00 (FR_TIMER). */
  public static final float RUN_SECONDS = 5 * 60f;

  /**
   * The longest frame the clock counts, in seconds. libGDX doesn't cap its frame time, so a frame stalled while
   * the window is dragged could take several seconds off the timer. Such a frame counts as this much instead
   * (FR_TIMER). A normal frame at 60 fps is about 0.017 s, so it is never cut.
   */
  public static final float MAX_FRAME_SECONDS = 0.1f;

  /** The student's top speed, per second (FR_MOVE). */
  public static final float STUDENT_MAX_SPEED = 140f;

  /**
   * How long the student takes to go from standing still to full speed, in seconds (FR_MOVE).
   */
  public static final float STUDENT_SPEED_UP_SECONDS = 0.5f;

  /** How big the student is drawn, the collision box stays the same. */
  public static final float DRAW_SCALE = 0.75f;

  /**
   * How long the student takes to stop from full speed, in seconds (FR_MOVE). Turning back sheds speed at the same
   * rate.
   */
  public static final float STUDENT_STOP_SECONDS = 0.5f;

  /** The amount of stamina the player starts with. */
  public static final float PLAYER_STAMINA = 20f;

  /** The constant velocity the geese will approach the player. (FR_GOOSE_MOVE)*/
  public static final float GOOSE_VELOCITY = 100f;

   /** The chance a new goose is white*/
  public static final float WHITE_GOOSE_CHANCE = 0.25f;

  /** How many pieces of bread fill a goose (FR_FULLNESS). */
  public static final int GOOSE_FULLNESS = 2;

  /** How fast a full goose flies away, per second (FR_FULLNESS). */
  public static final float GOOSE_FLY_SPEED = 220f;

  /** How long a full goose flies before it is gone, in seconds (FR_FULLNESS). */
  public static final float GOOSE_FLY_SECONDS = 2f;

  /** The amount of stamina the player loses when attacked by a goose. */
  public static final float GOOSE_ATTACK_POWER = 1f;

  /** The distance from which a goose can attack the player. */
  public static final float GOOSE_ATTACK_RANGE = 20f;

  /** Length of goose attack cooldown period in seconds */
  public static final float GOOSE_ATTACK_COOLDOWN = 1.5f;

  /** Seconds with no geese at the start of a run (FR_SPAWN). */
  public static final float CALM_START_SECONDS = 10f;

  // TODO: unused until stuck geese and the Goose Man turn away from walls (MovingBox, Goose, GooseMan).
  /** How far a stuck goose or Goose Man walks after turning away from a wall,  (FR_GOOSE_MOVE). */
  public static final float GOOSE_TURN_DISTANCE = 48f;

  /** If the Goose Man is in range, the dispenser throws in rounds of GOOSE_MAN_ROUND_THROWS. */
  public static final int GOOSE_MAN_ROUND_THROWS = 3;

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
  public static final int MAX_GEESE = 20;

  /** How far from the student a goose arrives, so off screen (FR_SPAWN). */
  public static final float SPAWN_DISTANCE = 360f;

  /** Seconds between two pieces of bread (FR_BREAD). */
  public static final float BREAD_GAP_SECONDS = 0.5f;

  /** How fast bread flies, per second (FR_BREAD). */
  public static final float BREAD_SPEED = 240f;

  /** How close a goose must be for the dispenser to throw at it, (FR_BREAD). */
  public static final float BREAD_RANGE = 200f;

  /** How many pieces of bread fill the Goose Man (FR_BOSS). */
  public static final int GOOSE_MAN_FULLNESS = 15;

  /** How many geese must be filled before the Goose Man arrives (FR_BOSS). */
  public static final int GOOSE_MAN_AFTER_GEESE = 30;

  /** Scale for the map rendering. */
  public static final float MAP_SCALE = 8f;

  private GameConfig() {
  }
}
