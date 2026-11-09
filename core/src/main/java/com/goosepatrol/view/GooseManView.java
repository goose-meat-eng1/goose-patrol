package com.goosepatrol.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.goosepatrol.model.GameConfig;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.Bread;
import com.goosepatrol.model.Facing;
import com.goosepatrol.model.GooseMan;

public class GooseManView {

  /** The size of one frame in the goose sheets, in pixels. */
  private static final int FRAME_WIDTH = 32;
  private static final int FRAME_HEIGHT = 64;

  /** How long each frame shows, in seconds. */
  private static final float SECONDS_PER_FRAME = 0.1f;

  /** The fullness bar's height, in map pixels. */
  private static final float BAR_HEIGHT = 2;

  private final GameSession session;
  private final Texture walkSheet = new Texture("geese/Goose Man.png");
  private final Texture idleSheet = new Texture("geese/Goose Man Defeated.png");
  private final Texture breadPicture = new Texture("items/Bread.png");
  private final Animation<TextureRegion> walk = loop(walkSheet);
  private final Animation<TextureRegion> idle = loop(idleSheet);
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();

  /** Makes the view for one run. */
  public GooseManView(GameSession session) {
    this.session = session;
  }

  /** Draws the geese, the bread and the fullness bars, after the student so they are on top. */
  public void draw(SpriteBatch batch, OrthographicCamera cam) {
    GooseMan gooseMan = session.getGooseMan();
    if (gooseMan == null) {
      return;
    }

    // Play time stops while paused, so the goose man don't flap during a pause.
    float playSeconds = (float) (GameConfig.RUN_SECONDS - session.getTimeLeft());
    batch.setProjectionMatrix(cam.combined);
    batch.begin();
    drawGooseMan(batch, gooseMan, playSeconds);
    for (Bread piece : session.getBread()) {
      Vector2 middle = piece.getMiddle();
      drawCentred(batch, breadPicture, middle.x, middle.y);
    }
    batch.end();

    shapeRenderer.setProjectionMatrix(cam.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    if (gooseMan.getState() == GooseMan.State.RUNNING) {
      drawFullnessBar(gooseMan);
    }
    shapeRenderer.end();
  }

  // The goose man's frame with its feet on its box's bottom, mirrored when it faces left.
  private void drawGooseMan(SpriteBatch batch, GooseMan gooseMan, float playSeconds) {
    Rectangle box = gooseMan.getBounds();
    boolean defeated = gooseMan.getState() == GooseMan.State.DEFEATED;
    float time = playSeconds + Math.floorMod(System.identityHashCode(gooseMan), 5) * SECONDS_PER_FRAME;
    TextureRegion frame = (defeated ? idle : walk).getKeyFrame(time, true);
    float scale = GameConfig.MAP_SCALE;
    float left = (box.x + box.width / 2 - FRAME_WIDTH / 2f) * scale;
    float bottom = box.y * scale;
    float width = FRAME_WIDTH * scale;
    float height = FRAME_HEIGHT * scale;
    if (gooseMan.getFacing() == Facing.LEFT) {
      batch.draw(frame, left + width, bottom, -width, height); // a negative width draws the frame mirrored
    } else {
      batch.draw(frame, left, bottom, width, height);
    }
  }

  // Cuts a sheet into frames that play in a loop.
  private static Animation<TextureRegion> loop(Texture sheet) {
    TextureRegion[] frames = TextureRegion.split(sheet, FRAME_WIDTH, FRAME_HEIGHT)[0];
    return new Animation<>(SECONDS_PER_FRAME, frames);
  }

  // A dark bar over the goose, filled red by how much it has eaten.
  private void drawFullnessBar(GooseMan gooseMan) {
    Rectangle box = gooseMan.getBounds();
    float scale = GameConfig.MAP_SCALE;
    float x = box.x * scale;
    float y = (box.y + FRAME_HEIGHT) * scale;
    float filled = (float) gooseMan.getFullness() / GameConfig.GOOSE_MAN_FULLNESS;
    shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
    shapeRenderer.rect(x, y, box.width * scale, BAR_HEIGHT * scale);
    shapeRenderer.setColor(1f, 0f, 0f, 1f);
    shapeRenderer.rect(x, y, box.width * filled * scale, BAR_HEIGHT * scale);
  }

  // A picture at its own size, centred on a point in map pixels.
  private static void drawCentred(SpriteBatch batch, Texture picture, float x, float y) {
    float scale = GameConfig.MAP_SCALE;
    float width = picture.getWidth() * scale;
    float height = picture.getHeight() * scale;
    batch.draw(picture, x * scale - width / 2, y * scale - height / 2, width, height);
  }

  /** Frees the pictures and the shape renderer. */
  public void dispose() {
    walkSheet.dispose();
    idleSheet.dispose();
    breadPicture.dispose();
    shapeRenderer.dispose();
  }
}
