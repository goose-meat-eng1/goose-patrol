package com.goosepatrol.model;

/**
 * Counts down the time left in a run from the frame times it is given (FR_TIMER).
 */
public class GameClock {

// A double, not a float, because a run adds up about 18,000 frame times and a float would collect rounding error.
  private double timeLeft;

  public GameClock(float runSeconds) {
    if (runSeconds <= 0) {
      throw new IllegalArgumentException("runSeconds must be more than 0, but was " + runSeconds);
    }
    timeLeft = runSeconds;
  }

  /**
   * Takes one frame's time off the time left. The time left stops at 0, and a frame time that is 0, less than 0
   * or not a number changes nothing.
   */
  public void tick(float delta) {
    if (Float.isNaN(delta) || delta <= 0) {
      return;
    }
    timeLeft = Math.max(0, timeLeft - delta);
  }


  public double getTimeLeft() {
    return timeLeft;
  }


  public boolean isUp() {
    return timeLeft <= 0;
  }
}
