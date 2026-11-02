package com.goosepatrol.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.goosepatrol.model.GameConfig;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.Player;

/**
 * Renders the player from the current run on the screen
 * 
 * @param session the run to show
 */
public class PlayerView {

  private final GameSession session;
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();

  public PlayerView(GameSession session) {
    this.session = session;
  }

  /**
   * Updates the position of the camera to center the player on screen
   * 
   * @param cam the camera to update
   */
  public void updateCameraPosition(OrthographicCamera cam) {
    Player player = session.getPlayer();
    Rectangle playerBounds = player.getBounds();
    cam.position.set(playerBounds.x * GameConfig.MAP_SCALE, playerBounds.y * GameConfig.MAP_SCALE, 0f);
    cam.update();
  }
  
  /**
   * Draws the player on screen. Called in render function of GameScreen.
   * Currently using ShapeRenderer to draw a rectangle.
   * TODO: update to draw using player sprite + animations
   * 
   * @param cam the camera to use for rendering
   */
  public void draw(OrthographicCamera cam) {
    Player player = session.getPlayer();
    Rectangle playerBounds = player.getBounds();

    shapeRenderer.setProjectionMatrix(cam.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(1f, 0f, 0f, 1f);
    shapeRenderer.rect(
        playerBounds.x * GameConfig.MAP_SCALE,
        playerBounds.y * GameConfig.MAP_SCALE,
        playerBounds.width * GameConfig.MAP_SCALE,
        playerBounds.height * GameConfig.MAP_SCALE);
    shapeRenderer.end();
  }
}
