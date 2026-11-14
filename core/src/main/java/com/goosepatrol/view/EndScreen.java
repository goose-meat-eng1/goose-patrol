package com.goosepatrol.view;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.goosepatrol.model.EndCondition;
import com.goosepatrol.model.GameSession;

/**
 * Shows why the run ended (FR_SCREENS).
 */
public class EndScreen extends ScreenAdapter {

  private final GameSession session;
  private final SpriteBatch batch;
  private final BitmapFont font;
  private final Viewport viewport = new ScreenViewport();

  /**
   * Makes the end screen.
   */
  public EndScreen(GameSession session, SpriteBatch batch, BitmapFont font) {
    this.session = session;
    this.batch = batch;
    this.font = font;
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    font.draw(batch, getEndMessage(session.getEndCondition()),
        0, viewport.getWorldHeight() / 2, viewport.getWorldWidth(), Align.center, false);
    font.draw(batch, "Items used: " + session.getItemsUsed()+ "\nEvents triggered: " + session.getEventsTriggered(),
        0, viewport.getWorldHeight() * .25f, viewport.getWorldWidth(), Align.center, false);
    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  private String getEndMessage(EndCondition endCondition) {
    return switch (endCondition) {
      case VICTORY -> "Congratulations!\nYou defeated Goose Man!\n\nThe geese have flown away, and you have escaped!";
      case TIMER -> "Time's up!\nYou weren't able to escape in time!\n\nYou were swarmed by the army of geese and failed to escape!";
      case STAMINA -> "Game Over!\nYou ran out of stamina!\n\nYou were swarmed by the army of geese and failed to escape!";
      default -> "";
    };
  }
}
