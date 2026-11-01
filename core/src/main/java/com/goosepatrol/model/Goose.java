package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * Goose movement (FR_GOOSE_MOVE),
 * Each goose will move at a constant speed towards the player from the position they spawn at,
 * Each goose has its own area it occupies and cannot be overlapping with other geese. 
 */
public class Goose {

  /** The width of the geese's box, in map pixels. */
  public static final float WIDTH = 12;

  /** The height of the geese's box, in map pixels. */
  public static final float HEIGHT = 12;

  public float gooseHunger = 3;

  /** The goose's collision box, in map pixels, which moves and stops at walls. */
  private final MovingBox body;
  
  private final Vector2 velocity = new Vector2();
  private final Vector2 direction = new Vector2();
  private Facing facing = Facing.RIGHT;

  /**
   * Makes a goose, standing still and facing right.
   * 
   * @param centre the centre of the goose's starting place, such as one of the map's goose spawns
   */
  public Goose(Vector2 centre, float gooseHunger){
    body = new MovingBox(centre.x - WIDTH / 2, centre.y - HEIGHT / 2, WIDTH, HEIGHT);
    this.gooseHunger = gooseHunger;
  }

  /**
   * Moves the goose by one frame. The goose turns to face the target, then walks towards it at
   * {@link GameConfig#GOOSE_VELOCITY}, moves across and up, stopping at the map's edges and at walls 
   * and other geese.
   * 
   * @param target the point to walk toward
   * @param flock every goose on the map
   * @param delta the frame's length in seconds
   * @param map the map, for its edges and solids and geese
   */
  public void update(Vector2 target, List<Goose> flock, float delta, CampusMap map) {
    do{
      Rectangle box = body.getBounds();
      direction.set(target).sub(box.x + WIDTH / 2, box.y + HEIGHT / 2);
      if (direction.len2() < 1) {
        velocity.setZero(); // already at the target
        return;
      }
      direction.nor();
      facing = facingFor(direction, facing);
      velocity.set(direction).scl(GameConfig.GOOSE_VELOCITY);

      // Each axis separately, so a goose blocked across can still slide up or down.
      if (body.moveX(velocity.x * delta, map, b -> blockingBox(b, map, flock))) {
        velocity.x = 0;
      }
      if (body.moveY(velocity.y * delta, map, b -> blockingBox(b, map, flock))) {
        velocity.y = 0;
      }
    } while (gooseHunger > 0);
    velocity.x = 0;
    velocity.y = 0;
  }

  // Solids block the goose, then other geese, so it stops flush against either.
  private Rectangle blockingBox(Rectangle b, CampusMap map, List<Goose> flock) {
    Rectangle solid = map.solidOverlapping(b);
    if (solid != null) {
      return solid;
    }
    return gooseOverlapping(b, flock);
  }

  // Faces the way the goose walks across, keeping the old facing when it walks straight up or down.
  private static Facing facingFor(Vector2 direction, Facing current) {
    if (direction.x > 0) {
      return Facing.RIGHT;
    }
      if (direction.x < 0) {
        return Facing.LEFT;
      }
      return current;
    }
  
  // The first other goose's box that overlaps box, or null. Boxes that only touch at an edge don't overlap.
  private Rectangle gooseOverlapping(Rectangle box, List<Goose> flock) {
  for (Goose other : flock) {
    Rectangle otherBox = other.body.getBounds();
    if (otherBox != box && otherBox.overlaps(box)) {
      return otherBox;
    }
  }
  return null;
}

  /**
   * Returns the goose's box.
   *
   * @return a copy of the box, in map pixels
   */
  public Rectangle getBounds() {
    return new Rectangle(body.getBounds());
  }

  /**
   * Returns the goose's speed.
   *
   * @return a copy of the speed, in map pixels per second
   */
  public Vector2 getVelocity() {
    return new Vector2(velocity);
  }

  /**
   * Returns which way the goose faces, which the view draws the goose by.
   *
   * @return LEFT or RIGHT
   */
  public Facing getFacing() {
    return facing;
  }

}