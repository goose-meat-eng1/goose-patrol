package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


class GameSessionTest {

  private static final float FRAME = 1f / 60;
  private static final double TOLERANCE = 0.001;

  private GameSession session;

  @BeforeEach
  void newRun() {
    session = new GameSession(new GameClock(300));
  }

  private void play(int seconds) {
    for (int i = 0; i < seconds * 60; i++) {
      session.update(FRAME);
    }
  }

  @Test
  @Tag("FR_PAUSE")
  void opensReadyAndNoTimePasses() {
    play(10);

    assertEquals(RunState.READY, session.getState());
    assertEquals(300, session.getTimeLeft(), TOLERANCE);
  }

  @Test
  @Tag("FR_TIMER")
  void startBeginsTheCountdown() {
    session.start();
    play(10);

    assertEquals(RunState.PLAYING, session.getState());
    assertEquals(290, session.getTimeLeft(), TOLERANCE);
  }

  @Test
  @Tag("FR_PAUSE")
  void pauseTakesEffectInTheSameFrame() {
    session.start();
    play(1);
    double before = session.getTimeLeft();

    session.togglePause();
    session.update(FRAME);

    assertEquals(RunState.PAUSED, session.getState());
    assertEquals(before, session.getTimeLeft());
  }

  @Test
  @Tag("FR_PAUSE")
  @Tag("FR_TIMER")
  void pausedTimeAddsNothing() {
    session.start();
    play(10);
    session.togglePause();
    play(120);
    session.togglePause();
    play(10);

    assertEquals(RunState.PLAYING, session.getState());
    assertEquals(280, session.getTimeLeft(), TOLERANCE);
  }

  @Test
  @Tag("FR_TIMER")
  void endsWhenTheTimeIsUp() {
    session.start();
    int frames = 0;
    while (session.getState() == RunState.PLAYING && frames < 400 * 60) {
      session.update(FRAME);
      frames++;
    }

    assertEquals(RunState.ENDED, session.getState());
    assertEquals(0, session.getTimeLeft());
    assertEquals(300, frames * (double) FRAME, 1.0);
  }

  @Test
  @Tag("FR_TIMER")
  void nothingChangesAfterTheEnd() {
    session.start();
    play(301);

    session.togglePause();
    session.start();
    play(1);

    assertEquals(RunState.ENDED, session.getState());
    assertEquals(0, session.getTimeLeft());
  }

  @Test
  @Tag("FR_PAUSE")
  void pauseKeyDoesNothingBeforeTheStart() {
    session.togglePause();

    assertEquals(RunState.READY, session.getState());
  }

  @Test
  @Tag("FR_PAUSE")
  void startKeyDoesNotResumeAPausedRun() {
    session.start();
    session.togglePause();
    session.start();

    assertEquals(RunState.PAUSED, session.getState());
  }

  @Test
  @Tag("FR_TIMER")
  void aLongFrameCountsAsAtMostTheMaximum() {
    session.start();
    session.update(5);

    assertEquals(300 - GameConfig.MAX_FRAME_SECONDS, session.getTimeLeft(), TOLERANCE);
  }
}
