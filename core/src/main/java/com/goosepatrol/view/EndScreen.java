package com.goosepatrol.view;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Shows why the run ended (FR_SCREENS).
 */
public class EndScreen extends ScreenAdapter {

  private final SpriteBatch batch;
  private final BitmapFont font;
  private final Viewport viewport = new ScreenViewport();

  /**
   * Makes the end screen.
   */
  public EndScreen(SpriteBatch batch, BitmapFont font) {
    this.batch = batch;
    this.font = font;
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    font.draw(batch, "Time's up! The run is over.",
        0, viewport.getWorldHeight() / 2, viewport.getWorldWidth(), Align.center, false);
    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }
}
