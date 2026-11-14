package com.goosepatrol.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.goosepatrol.model.GameConfig;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.Player;

/**
 * Renders the player from the current run on the screen
 */
public class PlayerView {
  /** The size of the player sprite in pixels. */
  private static final int SPRITE_WIDTH = 32;
  private static final int SPRITE_HEIGHT = 64;

  /** How long each animation frame shows, in seconds. */
  private static final float SECONDS_PER_FRAME = 0.1f;

  /** The stamina bar's width, in map pixels. */
  private static final float BAR_WIDTH = 24;

  /** The stamina bar's height, in map pixels. */
  private static final float BAR_HEIGHT = 3;

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
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();

  public PlayerView(GameSession session) {
    this.session = session;
  }

  // Cuts a sheet into frames that play in a loop.
  private static Animation<TextureRegion> loop(Texture sheet) {
    TextureRegion[] frames = TextureRegion.split(sheet, SPRITE_WIDTH, SPRITE_HEIGHT)[0];
    return new Animation<>(SECONDS_PER_FRAME, frames);
  }

  /**
   * Updates the position of the camera to center the player on screen
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
          frame = new TextureRegion(idleUp, SPRITE_WIDTH, SPRITE_HEIGHT);
          break;
        case DOWN:
          frame = new TextureRegion(idleDown, SPRITE_WIDTH, SPRITE_HEIGHT);
          break;
        case LEFT:
          frame = new TextureRegion(idleRight, SPRITE_WIDTH, SPRITE_HEIGHT);
          frame.flip(true, false); 
          break;
        case RIGHT:
          frame = new TextureRegion(idleRight, SPRITE_WIDTH, SPRITE_HEIGHT);
          break;
        default:
          frame = new TextureRegion(idleDown, SPRITE_WIDTH, SPRITE_HEIGHT);
      }
    }

    batch.setProjectionMatrix(cam.combined);
    batch.begin();
    float width = SPRITE_WIDTH * GameConfig.DRAW_SCALE;
    float height = SPRITE_HEIGHT * GameConfig.DRAW_SCALE;
    float frameX = playerBounds.x + playerBounds.width / 2 - width / 2; // the feet box sits at the frame's bottom middle
    batch.draw(frame, frameX * GameConfig.MAP_SCALE, playerBounds.y * GameConfig.MAP_SCALE,
      width * GameConfig.MAP_SCALE, height * GameConfig.MAP_SCALE);
    batch.end();

    shapeRenderer.setProjectionMatrix(cam.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    drawStaminaBar(player);
    shapeRenderer.end();
  }

  private void drawStaminaBar(Player player) {
    Rectangle box = player.getBounds();
    float scale = GameConfig.MAP_SCALE;
    float x = (box.x - (BAR_WIDTH - box.width)/2) * scale;
    float y = ((box.y - (BAR_HEIGHT - box.height)/2) + SPRITE_HEIGHT/1.35f) * scale;
    float filled = (float) player.getStamina() / GameConfig.PLAYER_STAMINA;

    // Outline
    shapeRenderer.setColor(0f, 0f, 0f, 1f);
    shapeRenderer.rect(x-4, y-5, BAR_WIDTH * scale + 8, BAR_HEIGHT * scale + 8);
    // Background
    shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
    shapeRenderer.rect(x, y, BAR_WIDTH * scale, BAR_HEIGHT * scale);
    // Filled section (remaining stamina)
    shapeRenderer.setColor(1.0f, 0.3f, 0.1f, 1f);
    shapeRenderer.rect(x, y, BAR_WIDTH * filled * scale, BAR_HEIGHT * scale);
  }

  public void dispose() {
    idleDown.dispose();
    idleUp.dispose();
    idleRight.dispose();
    walkDown.dispose();
    walkUp.dispose();
    walkRight.dispose();
    shapeRenderer.dispose();
  }
}
