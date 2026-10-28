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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class InputHandlerTest {

  private GameSession session;
  private InputHandler keys;

  // The keys a test is holding down, in place of a real keyboard.
  private final Set<Integer> held = new HashSet<>();

  @BeforeEach
  void newRun() {
    CampusMap openGround = new CampusMap(1000, 1000, List.of(), new Vector2(500, 500));
    session = new GameSession(new GameClock(300), openGround, new Player(openGround.getStudentStart()));
    keys = new InputHandler(session, held::contains);
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
    assertFalse(keys.keyDown(Input.Keys.W));
    assertFalse(keys.keyDown(Input.Keys.UP));

    assertEquals(RunState.READY, session.getState());
  }

  @Test
  void eachKeyGivesItsDirection() {
    assertMove(0, 1, Input.Keys.W);
    assertMove(0, -1, Input.Keys.S);
    assertMove(-1, 0, Input.Keys.A);
    assertMove(1, 0, Input.Keys.D);
    assertMove(0, 1, Input.Keys.UP);
    assertMove(0, -1, Input.Keys.DOWN);
    assertMove(-1, 0, Input.Keys.LEFT);
    assertMove(1, 0, Input.Keys.RIGHT);
  }

  @Test
  void twoKeysGiveADiagonal() {
    assertMove(1, 1, Input.Keys.W, Input.Keys.D);
    assertMove(-1, -1, Input.Keys.DOWN, Input.Keys.LEFT);
    assertMove(-1, 1, Input.Keys.A, Input.Keys.UP);
  }

  @Test
  void oppositeKeysCancel() {
    assertMove(0, 0, Input.Keys.W, Input.Keys.S);
    assertMove(0, 0, Input.Keys.LEFT, Input.Keys.D);
    assertMove(1, 0, Input.Keys.W, Input.Keys.DOWN, Input.Keys.D);
  }

  @Test
  void lettingGoOfOneKeyKeepsTheOther() {
    held.add(Input.Keys.W);
    held.add(Input.Keys.D);
    keys.readMovement();

    held.remove(Input.Keys.W);
    keys.readMovement();

    assertEquals(1, session.getMoveX());
    assertEquals(0, session.getMoveY());
  }

  @Test
  void lettingGoStopsTheMove() {
    held.add(Input.Keys.W);
    keys.readMovement();

    held.clear();
    keys.readMovement();

    assertEquals(0, session.getMoveX());
    assertEquals(0, session.getMoveY());
  }

  @Test
  void otherHeldKeysDoNotMove() {
    assertMove(0, 0, Input.Keys.SPACE, Input.Keys.F12);
  }

  private void assertMove(int x, int y, int... keycodes) {
    held.clear();
    for (int keycode : keycodes) {
      held.add(keycode);
    }
    keys.readMovement();
    assertEquals(x, session.getMoveX(), "x");
    assertEquals(y, session.getMoveY(), "y");
  }
}
