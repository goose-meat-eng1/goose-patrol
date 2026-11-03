package com.goosepatrol.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.GameConfig;

/**
 * Renders the CampusMap loaded in the current session
 * 
 * @param session the run to show
 */
public class MapView {

  private final GameSession session;
  private final OrthogonalTiledMapRenderer mapRenderer;

  public MapView(GameSession session, TiledMap tiledMap) {
    this.session = session;
    mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, GameConfig.MAP_SCALE);
  }

  /**
   * Draws the map. Called in render function of GameScreen.
   * 
   * @param cam the camera to use for rendering
   */
  public void draw(OrthographicCamera cam) {
    constrainCamera(cam);
    mapRenderer.setView(cam);
    mapRenderer.render();
  }

  /**
   * Constrains the camera to the bounds of the map so that out-of-bounds areas
   * are not visible when the player approaches the edge.
   * 
   * @param cam the camera to constrain
   */
  private void constrainCamera(OrthographicCamera cam) {
    float mapWidth = session.getMap().getWidth() * GameConfig.MAP_SCALE;
    float mapHeight = session.getMap().getHeight() * GameConfig.MAP_SCALE;

    cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth / 2f, mapWidth - cam.viewportWidth / 2f);
    cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight / 2f, mapHeight - cam.viewportHeight / 2f);

    cam.update();
  }

  public void dispose() {
    mapRenderer.dispose();
  }
}
