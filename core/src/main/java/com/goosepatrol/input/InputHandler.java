package com.goosepatrol.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.goosepatrol.model.GameSession;

/**
 * Turns key presses into commands on the run (D1). libGDX calls {@link #keyDown(int)} before the frame's update,
 * so a pause takes effect in the same frame (QR_PAUSE_RESPONSE). Slice 2 adds the movement keys, and slice 4 the
 * number keys for items.
 */
public class InputHandler extends InputAdapter {

  /** Starts the run (FR_START_PAUSED). */
  public static final int START_KEY = Input.Keys.SPACE;

  /** Pauses and resumes the run (FR_PAUSE). */
  public static final int PAUSE_KEY = Input.Keys.P;

  private final GameSession session;

  /**
   * Makes the key handler for one run.
   *
   * @param session the run the keys control
   */
  public InputHandler(GameSession session) {
    this.session = session;
  }

  /**
   * Handles one key press.
   *
   * @param keycode the key, as one of libGDX's {@link Input.Keys}
   * @return true if the key was used
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
    return false;
  }
}
