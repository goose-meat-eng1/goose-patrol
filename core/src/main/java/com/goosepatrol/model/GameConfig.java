package com.goosepatrol.model;

/**
 * Named numbers for tuning the game, each with the requirement it serves. Later slices add the calm start, the gap
 * between geese and the speeds here.
 */
public final class GameConfig {

  /** The length of one run in seconds: 5:00 (FR_TIMER). */
  public static final float RUN_SECONDS = 5 * 60f;

  /**
   * The longest frame the clock counts, in seconds. libGDX doesn't cap its frame time, so a frame stalled while
   * the window is dragged could take several seconds off the timer. Such a frame counts as this much instead
   * (QR_TIMER_ACCURACY). A normal frame at 60 fps is about 0.017 s, so it is never cut.
   */
  public static final float MAX_FRAME_SECONDS = 0.1f;

  private GameConfig() {
  }
}
