package com.goosepatrol.model;

/**
 * Counts down the time left in a run from the frame times it is given (FR_TIMER, QR_TIMER_ACCURACY, D4).
 *
 * <p>The clock doesn't know about pausing. {@link GameSession} calls {@link #tick(float)} only while the run is
 * PLAYING, so paused time never reaches it.
 */
public class GameClock {

  // A double, not a float: a run adds up about 18,000 frame times, and a float would collect rounding error.
  private double timeLeft;

  /**
   * Makes the clock for one run.
   *
   * @param runSeconds the length of the run in seconds, such as 300 for 5:00
   * @throws IllegalArgumentException if runSeconds is not more than 0
   */
  public GameClock(float runSeconds) {
    if (runSeconds <= 0) {
      throw new IllegalArgumentException("runSeconds must be more than 0, but was " + runSeconds);
    }
    timeLeft = runSeconds;
  }

  /**
   * Takes one frame's time off the time left. The time left stops at 0, and a frame time that is 0, less than 0
   * or not a number changes nothing.
   *
   * @param delta the frame's length in seconds
   */
  public void tick(float delta) {
    if (Float.isNaN(delta) || delta <= 0) {
      return;
    }
    timeLeft = Math.max(0, timeLeft - delta);
  }

  /**
   * Returns the time left in the run.
   *
   * @return the time left in seconds, from the run's length down to 0
   */
  public double getTimeLeft() {
    return timeLeft;
  }

  /**
   * Says whether the run's time is up (FR_TIME_UP).
   *
   * @return true once the time left has reached 0
   */
  public boolean isUp() {
    return timeLeft <= 0;
  }
}
