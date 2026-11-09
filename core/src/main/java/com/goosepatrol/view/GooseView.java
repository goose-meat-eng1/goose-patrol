package com.goosepatrol.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.goosepatrol.model.Bread;
import com.goosepatrol.model.Egg;
import com.goosepatrol.model.Facing;
import com.goosepatrol.model.GameConfig;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.Goose;
import com.goosepatrol.model.GooseKind;

/** Draws the geese, the bread and the eggs (FR_GOOSE_MOVE, FR_BREAD, FR_FULLNESS). */
public class GooseView {

  /** The size of one frame in the goose sheets, in pixels. */
  private static final int FRAME_SIZE = 32;

  /** How long each frame shows, in seconds. */
  private static final float SECONDS_PER_FRAME = 0.1f;

  /** How fast a flying goose rises, in map pixels per second, so it looks airborne. */
  private static final float LIFT_SPEED = 20f;

  /** The fullness bar's height, in map pixels. */
  private static final float BAR_HEIGHT = 2;

  private final GameSession session;
  private final Texture walkSheet = new Texture("geese/Canadian Goose Chase.png");
  private final Texture idleSheet = new Texture("geese/Canadian Goose Idle.png");
  private final Texture whiteWalkSheet = new Texture("geese/White Goose Chase.png");
  private final Texture whiteIdleSheet = new Texture("geese/White Goose Idle.png");
  private final Texture breadPicture = new Texture("items/Bread.png");
  private final Texture eggPicture = new Texture("items/Egg.png");

  private final Animation<TextureRegion> walk = loop(walkSheet);
  private final Animation<TextureRegion> idle = loop(idleSheet);
  private final Animation<TextureRegion> whiteWalk = loop(whiteWalkSheet);
  private final Animation<TextureRegion> whiteIdle = loop(whiteIdleSheet);
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();

  /** Makes the view for one run. */
  public GooseView(GameSession session) {
    this.session = session;
  }

  // Cuts a sheet into frames that play in a loop.
  private static Animation<TextureRegion> loop(Texture sheet) {
    TextureRegion[] frames = TextureRegion.split(sheet, FRAME_SIZE, FRAME_SIZE)[0];
    return new Animation<>(SECONDS_PER_FRAME, frames);
  }

  /** Draws the eggs, before the student so the student walks over them. */
  public void drawEggs(SpriteBatch batch, OrthographicCamera cam) {
    batch.setProjectionMatrix(cam.combined);
    batch.begin();
    for (Egg egg : session.getEggs()) {
      drawCentred(batch, eggPicture, egg.x(), egg.y());
    }
    batch.end();
  }

  /** Draws the geese, the bread and the fullness bars, after the student so they are on top. */
  public void draw(SpriteBatch batch, OrthographicCamera cam) {
    // Play time stops while paused, so the geese don't flap during a pause.
    float playSeconds = (float) (GameConfig.RUN_SECONDS - session.getTimeLeft());
    batch.setProjectionMatrix(cam.combined);
    batch.begin();
    for (Goose goose : session.getGeese()) {
      drawGoose(batch, goose, playSeconds);
    }
    for (Bread piece : session.getBread()) {
      Vector2 middle = piece.getMiddle();
      drawCentred(batch, breadPicture, middle.x, middle.y);
    }
    batch.end();

    shapeRenderer.setProjectionMatrix(cam.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (Goose goose : session.getGeese()) {
      if (goose.getState() == Goose.State.CHASING) {
        drawFullnessBar(goose);
      }
    }
    shapeRenderer.end();
  }

  // The goose's frame with its feet on its box's bottom, mirrored when it faces left.
  private void drawGoose(SpriteBatch batch, Goose goose, float playSeconds) {
    Rectangle box = goose.getBounds();
    boolean moving = goose.getState() != Goose.State.CHASING || !goose.getVelocity().isZero();
    // A different start frame for each goose, so the flock doesn't step in time.
    float time = playSeconds + Math.floorMod(System.identityHashCode(goose), 5) * SECONDS_PER_FRAME;

    boolean white = goose.getKind() == GooseKind.WHITE;
    Animation<TextureRegion> walking = white ? whiteWalk : walk;
    Animation<TextureRegion> standing = white ? whiteIdle : idle;
    TextureRegion frame = (moving ? walking : standing).getKeyFrame(time, true);

    float scale = GameConfig.MAP_SCALE;
    float left = (box.x + box.width / 2 - FRAME_SIZE / 2f) * scale;
    float bottom = (box.y + goose.getFlyingSeconds() * LIFT_SPEED) * scale;
    float size = FRAME_SIZE * scale;
    if (goose.getFacing() == Facing.LEFT) {
      batch.draw(frame, left + size, bottom, -size, size); // a negative width draws the frame mirrored
    } else {
      batch.draw(frame, left, bottom, size, size);
    }
  }

  // A dark bar over the goose, filled yellow by how much it has eaten.
  private void drawFullnessBar(Goose goose) {
    Rectangle box = goose.getBounds();
    float scale = GameConfig.MAP_SCALE;
    float x = box.x * scale;
    float y = (box.y + FRAME_SIZE) * scale;
    float filled = (float) goose.getFullness() / GameConfig.GOOSE_FULLNESS;
    shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
    shapeRenderer.rect(x, y, box.width * scale, BAR_HEIGHT * scale);
    shapeRenderer.setColor(1f, 0.8f, 0.2f, 1f);
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
    whiteWalkSheet.dispose();
    whiteIdleSheet.dispose();
    breadPicture.dispose();
    eggPicture.dispose();
    shapeRenderer.dispose();
  }
}
