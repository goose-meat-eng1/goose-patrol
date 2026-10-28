package com.goosepatrol.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.goosepatrol.model.CampusMap;
import com.goosepatrol.model.GameClock;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.Player;
import com.goosepatrol.model.RunState;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class InputHandlerTest {

  private GameSession session;
  private InputHandler keys;

  @BeforeEach
  void newRun() {
    CampusMap openGround = new CampusMap(1000, 1000, List.of(), new Vector2(500, 500));
    session = new GameSession(new GameClock(300), openGround, new Player(openGround.getStudentStart()));
    keys = new InputHandler(session);
  }

  @Test
  @Tag("FR_PAUSE")
  void spaceStartsTheRun() {
    assertTrue(keys.keyDown(Input.Keys.SPACE));

    assertEquals(RunState.PLAYING, session.getState());
  }

  @Test
  @Tag("FR_PAUSE")
  void pPausesAndResumes() {
    keys.keyDown(Input.Keys.SPACE);

    keys.keyDown(Input.Keys.P);
    assertEquals(RunState.PAUSED, session.getState());

    keys.keyDown(Input.Keys.P);
    assertEquals(RunState.PLAYING, session.getState());
  }

  @Test
  @Tag("FR_PAUSE")
  void leavesOtherKeysAlone() {
    assertFalse(keys.keyDown(Input.Keys.F12));

    assertEquals(RunState.READY, session.getState());
  }
}
