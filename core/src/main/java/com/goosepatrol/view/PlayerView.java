package com.goosepatrol.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
  /** The size of one frame in the player sheets, in pixels. */
  private static final int FRAME_WIDTH = 32;
  private static final int FRAME_HEIGHT = 64;

  /** How long each frame shows, in seconds. */
  private static final float SECONDS_PER_FRAME = 0.1f;

  private final GameSession session;
  private final Texture idleDown = new Texture("player/Student Idle Down.png");
  private final Texture idleUp = new Texture("player/Student Idle Up.png");
  private final Texture idleRight = new Texture("player/Student Idle Right.png");
  private final Texture walkDown = new Texture("player/Student Walk Down.png");
  private final Texture walkUp = new Texture("player/Student Walk Up.png");
  private final Texture walkRight = new Texture("player/Student Walk Right.png");
  private final Animation<TextureRegion> walkDownAnimation = loop(walkDown);
  private final Animation<TextureRegion> walkUpAnimation = loop(walkUp);
  private final Animation<TextureRegion> walkRightAnimation = loop(walkRight);

  public PlayerView(GameSession session) {
    this.session = session;
  }

  // Cuts a sheet into frames that play in a loop.
  private static Animation<TextureRegion> loop(Texture sheet) {
    TextureRegion[] frames = TextureRegion.split(sheet, FRAME_WIDTH, FRAME_HEIGHT)[0];
    return new Animation<>(SECONDS_PER_FRAME, frames);
  }

  /**
   * Updates the position of the camera to center the player on screen
   * 
   * @param cam the camera to update
   */
  public void updateCameraPosition(OrthographicCamera cam) {
    Player player = session.getPlayer();
    Rectangle playerBounds = player.getBounds();
    cam.position.set((playerBounds.x + playerBounds.width / 2) * GameConfig.MAP_SCALE,
        (playerBounds.y + playerBounds.height / 2) * GameConfig.MAP_SCALE, 0f);
    cam.update();
  }

  /**
   * Draws the player on screen. Called in render function of GameScreen.
   * 
   * @param cam the camera to use for rendering
   */
  public void draw(SpriteBatch batch, OrthographicCamera cam) {
    Player player = session.getPlayer();
    Rectangle playerBounds = player.getBounds();

    TextureRegion frame;
    if (player.getVelocity().len2() > 0) {
      switch (player.getFacing()) {
        case UP:
          frame = walkUpAnimation.getKeyFrame((float)(GameConfig.RUN_SECONDS - session.getTimeLeft()), true);
          break;
        case DOWN:
          frame = walkDownAnimation.getKeyFrame((float)(GameConfig.RUN_SECONDS - session.getTimeLeft()), true);
          break;
        case LEFT:
          frame = walkRightAnimation.getKeyFrame((float)(GameConfig.RUN_SECONDS - session.getTimeLeft()), true);
          frame = new TextureRegion(frame);
          frame.flip(true, false); 
          break;
        case RIGHT:
          frame = walkRightAnimation.getKeyFrame((float)(GameConfig.RUN_SECONDS - session.getTimeLeft()), true);
          break;
        default:
          frame = walkDownAnimation.getKeyFrame((float)(GameConfig.RUN_SECONDS - session.getTimeLeft()), true);
      }
    } else {
      switch (player.getFacing()) {
        case UP:
          frame = new TextureRegion(idleUp, (int)playerBounds.width, (int)playerBounds.height);
          break;
        case DOWN:
          frame = new TextureRegion(idleDown, (int)playerBounds.width, (int)playerBounds.height);
          break;
        case LEFT:
          frame = new TextureRegion(idleRight, (int)playerBounds.width, (int)playerBounds.height);
          frame.flip(true, false); 
          break;
        case RIGHT:
          frame = new TextureRegion(idleRight, (int)playerBounds.width, (int)playerBounds.height);
          break;
        default:
          frame = new TextureRegion(idleDown, (int)playerBounds.width, (int)playerBounds.height );
      }
    }

    batch.setProjectionMatrix(cam.combined);
    batch.begin();
    batch.draw(frame, playerBounds.x * GameConfig.MAP_SCALE, playerBounds.y * GameConfig.MAP_SCALE, 
      playerBounds.width * GameConfig.MAP_SCALE, playerBounds.height * GameConfig.MAP_SCALE);
    batch.end();
  }

  public void dispose() {
    idleDown.dispose();
    idleUp.dispose();
    idleRight.dispose();
    walkDown.dispose();
    walkUp.dispose();
    walkRight.dispose();
  }
}
