package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameClockTest {

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
