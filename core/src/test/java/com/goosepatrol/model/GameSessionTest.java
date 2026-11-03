package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class GameSessionTest {

  private static final float FRAME = 1f / 60;
  private static final double TOLERANCE = 0.001;
  private static final Vector2 START = new Vector2(500, 500);
  private static final float START_X = START.x - Player.WIDTH / 2;

  private GameSession session;

  @BeforeEach
  void newRun() {
    CampusMap openGround = new CampusMap(1000, 1000, List.of(), START);
    session = new GameSession(new GameClock(300), openGround, new Player(START));
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
  @Tag("FR_MOVE")
  void studentMovesWhilePlaying() {
    session.start();
    session.setMoveInput(1, 0);
    play(1);

    assertTrue(session.getPlayer().getBounds().x > START_X);
  }
}
