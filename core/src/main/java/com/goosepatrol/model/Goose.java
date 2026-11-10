package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/** A goose that chases the student, eats bread and flies away when full (FR_GOOSE_MOVE, FR_FULLNESS). */
public class Goose implements Feedable {

  /** What a goose is doing (FR_FULLNESS). */
  public enum State {
    CHASING,
    FLYING_AWAY,
    GONE
  }

  /** The width of the geese's box, in map pixels. */
  public static final float WIDTH = 12;

  /** The height of the geese's box, in map pixels. */
  public static final float HEIGHT = 12;

  /** Pieces of bread eaten so far. */
  private int fullness;

  private State state = State.CHASING;

  /** Seconds spent flying away. */
  private float flyingSeconds;

  /** The goose's collision box, in map pixels, which moves and stops at walls. */
  private final MovingBox body;

  private final Vector2 velocity = new Vector2();
  private final Vector2 direction = new Vector2();
  private Facing facing = Facing.RIGHT;
  private final GooseKind kind;

  /** Makes a goose standing at a point, facing right. */
  public Goose(Vector2 centre, GooseKind kind) {
    body = new MovingBox(centre.x - WIDTH / 2, centre.y - HEIGHT / 2, WIDTH, HEIGHT);
    this.kind = kind;
  }

  /** Moves the goose one frame. It walks to the target, or flies away from it once full (FR_GOOSE_MOVE). */
  public void update(Vector2 target, List<Goose> flock, float delta, CampusMap map) {
    if (state == State.FLYING_AWAY) {
      flyAwayFrom(target, delta);
      return;
    }
    if (state == State.GONE) {
      return;
    }
    Rectangle box = body.getBounds();
    direction.set(target).sub(box.x + WIDTH / 2, box.y + HEIGHT / 2);
    float distance = direction.len();
    if (distance < 1) {
      velocity.setZero(); // already at the target
      return;
    }
    direction.nor();
    facing = facingFor(direction, facing);
    // Never step past the target, as on a slow frame a full step would jump over it.
    velocity.set(direction).scl(Math.min(GameConfig.GOOSE_VELOCITY, distance / delta));

    // Each axis separately, so a goose blocked across can still slide up or down.
    if (body.moveX(velocity.x * delta, map, b -> blockingBox(b, map, flock))) {
      velocity.x = 0;
    }
    if (body.moveY(velocity.y * delta, map, b -> blockingBox(b, map, flock))) {
      velocity.y = 0;
    }
  }

  // Flies straight away from the target over everything, and is gone after GOOSE_FLY_SECONDS (FR_FULLNESS).
  private void flyAwayFrom(Vector2 target, float delta) {
    Rectangle box = body.getBounds();
    if (flyingSeconds == 0) {
      velocity.set(box.x + WIDTH / 2, box.y + HEIGHT / 2).sub(target);
      if (velocity.isZero()) {
        velocity.set(0, 1); // standing right on the target, so fly straight up
      }
      velocity.nor().scl(GameConfig.GOOSE_FLY_SPEED);
      facing = facingFor(velocity, facing);
    }
    	// In the air, so no MovingBox, and nothing stops it.
    box.x += velocity.x * delta;
    box.y += velocity.y * delta;
    flyingSeconds += delta;
    if (flyingSeconds >= GameConfig.GOOSE_FLY_SECONDS) {
      state = State.GONE;
    }
  }

  // Solids block the goose, then other geese, so it stops flush against either.
  private Rectangle blockingBox(Rectangle b, CampusMap map, List<Goose> flock) {
    Rectangle solid = map.solidOverlapping(b);
    if (solid != null) {
      return solid;
    }
    return gooseOverlapping(b, flock);
  }

  // Faces the way the goose moves across, keeping the old facing when it moves straight up or down.
  private static Facing facingFor(Vector2 direction, Facing current) {
    if (direction.x > 0) {
      return Facing.RIGHT;
    }
    if (direction.x < 0) {
      return Facing.LEFT;
    }
    return current;
  }

  // The first other goose on the ground that overlaps box, or null.
  private Rectangle gooseOverlapping(Rectangle box, List<Goose> flock) {
    for (Goose other : flock) {
      if (other != this && other.blocks(box)) {
        return other.body.getBounds();
      }
    }
    return null;
  }

  /** Whether this goose is in the box's way; only a goose on the ground is (FR_GOOSE_MOVE). */
  public boolean blocks(Rectangle box) {
    return state == State.CHASING && body.getBounds().overlaps(box);
  }

  /** Feeds the goose one piece, and returns true if that filled it, so it flies away (FR_FULLNESS). */
  public boolean feed() {
    if (state != State.CHASING) {
      return false;
    }
    fullness++;
    if (!isFull()) {
      return false;
    }
    state = State.FLYING_AWAY;
    return true;
  }

  public void feedMax() {
    fullness = GameConfig.GOOSE_FULLNESS;
    state = State.FLYING_AWAY;
  }

  /** Whether the goose has eaten all the bread it can (FR_FULLNESS). */
  public boolean isFull() {
    return fullness >= GameConfig.GOOSE_FULLNESS;
  }

  /** If the goose is still on the ground, so it can eat (FR_FULLNESS). */
  @Override
  public boolean canEat() {
    return state == State.CHASING;
  }

  @Override
  public int getMaxFullness() {
    return GameConfig.GOOSE_FULLNESS;
  }

  public int getFullness() {
    return fullness;
  }

  public State getState() {
    return state;
  }

  public float getFlyingSeconds() {
    return flyingSeconds;
  }

  public Rectangle getBounds() {
    return new Rectangle(body.getBounds());
  }

  public Vector2 getVelocity() {
    return new Vector2(velocity);
  }

  public Facing getFacing() {
    return facing;
  }

  public GooseKind getKind() {
    return kind;
  }
}
