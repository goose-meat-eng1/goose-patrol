package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameClockTest {

  @Test
  @Tag("FR_TIMER")
  void startsWithTheWholeRun() {
    GameClock clock = new GameClock(300);

    assertEquals(300, clock.getTimeLeft());
    assertFalse(clock.isUp());
  }

  @Test
  @Tag("FR_TIMER")
  void tickTakesOffTheFrameTime() {
    GameClock clock = new GameClock(300);

    clock.tick(1.5f);

    assertEquals(298.5, clock.getTimeLeft(), 1e-6);
  }

  @Test
  @Tag("FR_TIMER")
  void stopsAtZero() {
    GameClock clock = new GameClock(300);

    clock.tick(400);

    assertEquals(0, clock.getTimeLeft());
    assertTrue(clock.isUp());
  }

  @Test
  @Tag("FR_TIMER")
  void ignoresFrameTimesThatAreNotPositive() {
    GameClock clock = new GameClock(300);

    clock.tick(0);
    clock.tick(-1);
    clock.tick(Float.NaN);

    assertEquals(300, clock.getTimeLeft());
  }

  @Test
  @Tag("FR_TIMER")
  void rejectsARunWithNoTime() {
    assertThrows(IllegalArgumentException.class, () -> new GameClock(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {30, 60, 144, 240})
  @Tag("FR_TIMER")
  void fiveMinutesOfFramesEndsWithinOneSecond(int framesPerSecond) {
    GameClock clock = new GameClock(300);
    float frame = 1f / framesPerSecond;
    int maxFrames = 400 * framesPerSecond; 
    double played = 0;

    for (int i = 0; i < maxFrames && !clock.isUp(); i++) {
      clock.tick(frame);
      played += frame;
    }

    assertTrue(clock.isUp());
    assertEquals(300, played, 1.0);
  }
}
