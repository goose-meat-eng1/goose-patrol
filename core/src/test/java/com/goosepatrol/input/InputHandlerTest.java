package com.goosepatrol.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.Input;
import com.goosepatrol.model.GameClock;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.RunState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InputHandlerTest {

  private GameSession session;
  private InputHandler keys;

  @BeforeEach
  void newRun() {
    session = new GameSession(new GameClock(300));
    keys = new InputHandler(session);
  }

  @Test
  void spaceStartsTheRun() {
    assertTrue(keys.keyDown(Input.Keys.SPACE));

    assertEquals(RunState.PLAYING, session.getState());
  }

  @Test
  void pPausesAndResumes() {
    keys.keyDown(Input.Keys.SPACE);

    keys.keyDown(Input.Keys.P);
    assertEquals(RunState.PAUSED, session.getState());

    keys.keyDown(Input.Keys.P);
    assertEquals(RunState.PLAYING, session.getState());
  }

  @Test
  void leavesOtherKeysAlone() {
    assertFalse(keys.keyDown(Input.Keys.F12));

    assertEquals(RunState.READY, session.getState());
  }
}
