package com.goosepatrol.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.Items;
import com.goosepatrol.model.RunState;

import java.util.function.IntPredicate;

/**
 * Turns keys into commands on the run. libGDX calls keyDown before the frame's update, so a
 * pause takes effect in the same frame (FR_PAUSE).
 */
public class InputHandler extends InputAdapter {

  /** Starts the run (FR_PAUSE). */
  public static final int START_KEY = Input.Keys.SPACE;

  /** Pauses and resumes the run (FR_PAUSE). */
  public static final int PAUSE_KEY = Input.Keys.P;

  /** Moves the student up (FR_MOVE). */
  public static final int UP_KEY = Input.Keys.W;

  /** Moves the student down (FR_MOVE). */
  public static final int DOWN_KEY = Input.Keys.S;

  /** Moves the student left (FR_MOVE). */
  public static final int LEFT_KEY = Input.Keys.A;

  /** Moves the student right (FR_MOVE). */
  public static final int RIGHT_KEY = Input.Keys.D;

  /** Moves the student up, as {@link #UP_KEY} does (FR_MOVE). */
  public static final int UP_ARROW = Input.Keys.UP;

  /** Moves the student down, as {@link #DOWN_KEY} does (FR_MOVE). */
  public static final int DOWN_ARROW = Input.Keys.DOWN;

  /** Moves the student left, as {@link #LEFT_KEY} does (FR_MOVE). */
  public static final int LEFT_ARROW = Input.Keys.LEFT;

  /** Moves the student right, as {@link #RIGHT_KEY} does (FR_MOVE). */
  public static final int RIGHT_ARROW = Input.Keys.RIGHT;

  public static final int COFFEE_KEY = Input.Keys.NUM_1;
  public static final int BREAD_KEY = Input.Keys.NUM_2;

  private final GameSession session;
  private final IntPredicate isHeld;

  /**
   * Makes the key handler for one run.
   *
   */
  public InputHandler(GameSession session, IntPredicate isHeld) {
    this.session = session;
    this.isHeld = isHeld;
  }

  /**
   * Handles one key press.
   *
   */
  @Override
  public boolean keyDown(int keycode) {
    if (keycode == START_KEY) {
      session.start();
      return true;
    }
    if (keycode == PAUSE_KEY) {
      session.togglePause();
      return true;
    }

    if (session.getState() == RunState.PLAYING) {
      if (keycode == COFFEE_KEY) {
        session.useItem(Items.COFFEE);
        return true;
      }
      if (keycode == BREAD_KEY) {
        session.useItem(Items.BREAD);
        return true;
      }
    }
    
    return false;
  }

  /**
   * Reads the held movement keys and passes the direction to the run (FR_MOVE). Call it once per frame, before GameSession.update.
   *  Each of x and y is -1, 0 or 1 opposite keys cancel, so holding left and
   * right gives x = 0.
   */
  public void readMovement() {
    int x = axis(LEFT_KEY, LEFT_ARROW, RIGHT_KEY, RIGHT_ARROW);
    int y = axis(DOWN_KEY, DOWN_ARROW, UP_KEY, UP_ARROW);
    session.setMoveInput(x, y);
  }

  /** Returns -1 if a negative key is held, 1 if a positive one is, and 0 for both or neither. */
  private int axis(int negative, int negativeArrow, int positive, int positiveArrow) {
    int value = 0;
    if (isHeld.test(negative) || isHeld.test(negativeArrow)) {
      value--;
    }
    if (isHeld.test(positive) || isHeld.test(positiveArrow)) {
      value++;
    }
    return value;
  }
}
