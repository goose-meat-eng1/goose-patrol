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
 * The screen for play. Each frame it reads the held keys.
 */
public class GameScreen extends ScreenAdapter {

  private final GameSession session;
  private final Runnable readInput;
  private final SpriteBatch batch;
  private final Hud hud;
  private final MapView mapView;
  private final PlayerView playerView;
  private final GooseView gooseView;
  private final Runnable onRunEnded;
  private final GooseManView gooseManView;

  private static final float VIEW_WIDTH = 480;
  private static final float VIEW_HEIGHT = 270;

  private final Viewport hudViewport = new ScreenViewport();
  private final OrthographicCamera camera = new OrthographicCamera();
  private final Viewport worldViewport = new FitViewport(VIEW_WIDTH * GameConfig.MAP_SCALE, VIEW_HEIGHT * GameConfig.MAP_SCALE, camera);

  /**
   * Makes the play screen for one run.
   */
  public GameScreen(GameSession session, Runnable readInput,TiledMap tiledMap, SpriteBatch batch, BitmapFont font, Runnable onRunEnded) {
    this.session = session;
    this.readInput = readInput;
    this.batch = batch;
    this.hud = new Hud(session, font);
    this.mapView = new MapView(session, tiledMap);
    this.playerView = new PlayerView(session);
    this.gooseView = new GooseView(session);
    this.gooseManView = new GooseManView(session);
    this.onRunEnded = onRunEnded;
    this.playerView.updateCameraPosition(camera);
  }

  @Override
  public void render(float delta) {
    readInput.run(); 

    session.update(delta); 
    if (session.getState() == RunState.ENDED) {
      onRunEnded.run(); 
      return;
    }

    // Update camera position to follow player
    playerView.updateCameraPosition(camera);
    
    ScreenUtils.clear(0.17f, 0.17f, 0.2f, 1f);

    worldViewport.apply();

    mapView.draw(camera);
    gooseView.drawEggs(batch, camera);
    gooseManView.draw(batch, camera);
    playerView.draw(batch, camera);
    gooseView.draw(batch, camera);

    hudViewport.apply();
    hud.drawBackground(hudViewport.getCamera().combined, hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
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
   * Pauses when the window is minimised or loses focus. 
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
    gooseView.dispose();
    gooseManView.dispose();
    hud.dispose();
  }
}
