package com.goosepatrol.view;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.RunState;

/**
 * The screen for play (D1). Each frame it passes libGDX's frame time to {@link GameSession#update(float)}, then
 * draws (D4). Slice 2 adds the map and the student.
 */
public class GameScreen extends ScreenAdapter {

  private final GameSession session;
  private final SpriteBatch batch;
  private final Hud hud;
  private final Runnable onRunEnded;


  private final Viewport hudViewport = new ScreenViewport();

  /**
   * Makes the play screen for one run.
   *
   * @param session the run to show
   * @param batch the shared batch to draw with
   * @param font the shared font to write with
   * @param onRunEnded called once, when the run has ended, to show the end screen
   */
  public GameScreen(GameSession session, SpriteBatch batch, BitmapFont font, Runnable onRunEnded) {
    this.session = session;
    this.batch = batch;
    this.hud = new Hud(session, font);
    this.onRunEnded = onRunEnded;
  }

  @Override
  public void render(float delta) {
    session.update(delta); // D4: a frame starts with update(delta)
    if (session.getState() == RunState.ENDED) {
      onRunEnded.run(); // D3: ENDED shows the end screen
      return;
    }
    ScreenUtils.clear(0.17f, 0.17f, 0.2f, 1f);
    hudViewport.apply();
    batch.setProjectionMatrix(hudViewport.getCamera().combined);
    batch.begin();
    hud.draw(batch, hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    hudViewport.update(width, height, true);
  }

  /**
   * Pauses when the window is minimised or loses focus. This ensures that no time passes while
   * the player is away (FR_PAUSE). It stays paused until the player presses the pause key.
   */
  @Override
  public void pause() {
    if (session.getState() == RunState.PLAYING) {
      session.togglePause();
    }
  }
}
