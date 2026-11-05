package com.goosepatrol.view;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.goosepatrol.model.GameConfig;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.RunState;

/**
 * The screen for play (D1). Each frame it reads the held keys, passes libGDX's frame time to
 * {@link GameSession#update(float)}, then draws (D4). Slice 2 adds the map and the student.
 */
public class GameScreen extends ScreenAdapter {

  private final GameSession session;
  private final Runnable readInput;
  private final SpriteBatch batch;
  private final Hud hud;
  private final MapView mapView;
  private final PlayerView playerView;
  private final Runnable onRunEnded;

  private static final float VIEW_WIDTH = 320;
  private static final float VIEW_HEIGHT = 180;

  private final Viewport hudViewport = new ScreenViewport();
  private final OrthographicCamera camera = new OrthographicCamera();
  private final Viewport worldViewport = new FitViewport(VIEW_WIDTH * GameConfig.MAP_SCALE, VIEW_HEIGHT * GameConfig.MAP_SCALE, camera);

  /**
   * Makes the play screen for one run.
   *
   * @param session the run to show
   * @param readInput called at the start of each frame to read the held keys (FR_MOVE)
   * @param batch the shared batch to draw with
   * @param font the shared font to write with
   * @param onRunEnded called once, when the run has ended, to show the end screen
   */
  public GameScreen(GameSession session, Runnable readInput,TiledMap tiledMap, SpriteBatch batch, BitmapFont font, Runnable onRunEnded) {
    this.session = session;
    this.readInput = readInput;
    this.batch = batch;
    this.hud = new Hud(session, font);
    this.mapView = new MapView(session, tiledMap);
    this.playerView = new PlayerView(session);
    this.onRunEnded = onRunEnded;
    this.playerView.updateCameraPosition(camera);
  }

  @Override
  public void render(float delta) {
    readInput.run(); // FR_MOVE: read the held keys before the update

    session.update(delta); // D4: a frame starts with update(delta)
    if (session.getState() == RunState.ENDED) {
      onRunEnded.run(); // D3: ENDED shows the end screen
      return;
    }

    // Update camera position to follow player
    playerView.updateCameraPosition(camera);
    
    ScreenUtils.clear(0.17f, 0.17f, 0.2f, 1f);

    worldViewport.apply();

    mapView.draw(camera);
    playerView.draw(camera);

    hudViewport.apply();
    batch.setProjectionMatrix(hudViewport.getCamera().combined);
    batch.begin();
    hud.draw(batch, hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    worldViewport.update(width, height);
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

  @Override
  public void dispose() {
    mapView.dispose();
    playerView.dispose();
  }
}
