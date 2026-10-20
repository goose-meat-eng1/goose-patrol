package com.goosepatrol.view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.RunState;
import java.util.Locale;

/**
 * The overlay, the time left, and a message line for the start prompt and the paused
 * message.
 */
public class Hud {

  private static final float MARGIN = 24;

  private final GameSession session;
  private final BitmapFont font;

  /**
   * Makes the HUD for one run.
   *
   * @param session the run to show
   * @param font the font to write with
   */
  public Hud(GameSession session, BitmapFont font) {
    this.session = session;
    this.font = font;
  }

  /**
   * Draws the HUD. Call draw between {@code batch.begin()} and {@code batch.end()}.
   *
   * @param batch the batch to draw with
   * @param width the width of the area, in pixels
   * @param height the height of the area, in pixels
   */
  public void draw(SpriteBatch batch, float width, float height) {
    font.draw(batch, "Time left " + formatTime(session.getTimeLeft()), MARGIN, height - MARGIN);
    font.draw(batch, messageFor(session.getState()), 0, height / 2, width, Align.center, false);
  }

  /**
   * Formats seconds as m:ss.
   * @param seconds the time left in seconds
   * @return  string representing the time as minutes and seconds, such as 4:59
   */
  static String formatTime(double seconds) {
    int whole = (int) Math.ceil(seconds);
    return String.format(Locale.ROOT, "%d:%02d", whole / 60, whole % 60);
  }

  /**
   * Returns the message for a state: the start prompt () or the paused message ().
   *
   * @param state the state of the run
   * @return the message, or an empty string while playing
   */
  static String messageFor(RunState state) {
    return switch (state) {
      case READY -> "Press SPACE to start. P pauses the game.";
      case PAUSED -> "Paused. Press P to carry on.";
      default -> "";
    };
  }
}
