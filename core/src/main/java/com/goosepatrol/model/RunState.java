package com.goosepatrol.model;

/**
 * The states of one run, as drawn in D3. A run opens in {@link #READY}, and only {@link #PLAYING} lets time pass.
 */
public enum RunState {
  /** Waiting for the start key, with the start prompt on screen (FR_START_PAUSED). */
  READY,
  /** The clock counts down and the game moves (FR_TIMER). */
  PLAYING,
  /** Stopped by the pause key. Nothing changes until it is pressed again (FR_PAUSE). */
  PAUSED,
  /** The time is up and the end screen shows (FR_TIME_UP). */
  ENDED
}
