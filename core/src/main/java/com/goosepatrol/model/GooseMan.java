package com.goosepatrol.model;

import java.util.Random;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * GooseMan (FR_BOSS),
 * The Goose Man will move at the same velocity as other geese towards a random point on the map,
 * then when it gets to a point, it changes the target point
 */
public class GooseMan{

  /** The width of the goose man's box, in map pixels. */
  public static final float WIDTH = 12;

  /** The height of the goose man's box, in map pixels. */
  public static final float HEIGHT = 12;

  /** The goose man's collision box, in map pixels, which moves and stops at walls. */
  private final MovingBox body;

  /** The point the goose man is walking to, in map pixels. */
  private final Vector2 target = new Vector2();
  private final Random random;
  
  private final Vector2 velocity = new Vector2();
  private final Vector2 direction = new Vector2();
  private Facing facing = Facing.RIGHT;

  /**
   * Makes the goose man, standing still and facing right.
   * 
   * @param centre the centre of the goose man's starting place, such as one of the map's goose spawns
   */
  public GooseMan(Vector2 centre, CampusMap map, Random random){
    body = new MovingBox(centre.x - WIDTH / 2, centre.y - HEIGHT / 2, WIDTH, HEIGHT);
    this.random = random;
    pickTarget(map);
  }

  /**
   * Moves the goose man by one frame. The goose man turns to face the target point, then walks towards it at
   * {@link GameConfig#GOOSE_VELOCITY}, moves across and up, stopping at the map's edges and at walls.
   * 
   * @param target the point to walk toward
   * @param delta the frame's length in seconds
   * @param map the map, for its edges and solids
   */
  public void update(float delta, CampusMap map) {
    Rectangle box = body.getBounds();
    direction.set(target).sub(box.x + WIDTH / 2, box.y + HEIGHT / 2);
    if (direction.len2() < 1) {
      velocity.setZero();
      pickTarget(map);
      return;
    }
    direction.nor();
    facing = facingFor(direction, facing);
    velocity.set(direction).scl(GameConfig.GOOSE_VELOCITY);

    if (body.moveX(velocity.x * delta, map, map::solidOverlapping)) {
      velocity.x = 0;
    }
    if (body.moveY(velocity.y * delta, map, map::solidOverlapping)) {
      velocity.y = 0;
    }
    if (velocity.x == 0 || velocity.y == 0) {
      pickTarget(map); // stuck against a wall: give up on this point
    }
}

  // Faces the way the goose man walks across, keeping the old facing when it walks straight up or down.
  private static Facing facingFor(Vector2 direction, Facing current) {
    if (direction.x > 0) {
      return Facing.RIGHT;
    }
      if (direction.x < 0) {
        return Facing.LEFT;
      }
      return current;
    }

  private void pickTarget(CampusMap map) {
    Rectangle spot = new Rectangle(0, 0, WIDTH, HEIGHT);
    do {
      spot.setPosition(random.nextFloat() * (map.getWidth() - WIDTH),
          random.nextFloat() * (map.getHeight() - HEIGHT));
    } while (map.solidOverlapping(spot) != null);
    spot.getCenter(target);
  }


  /**
   * Returns the goose man's box.
   *
   * @return a copy of the box, in map pixels
   */
  public Rectangle getBounds() {
    return new Rectangle(body.getBounds());
  }

  /**
   * Returns the goose man's speed.
   *
   * @return a copy of the speed, in map pixels per second
   */
  public Vector2 getVelocity() {
    return new Vector2(velocity);
  }

  /**
   * Returns which way the goose man faces, which the view draws the goose man by.
   *
   * @return LEFT or RIGHT
   */
  public Facing getFacing() {
    return facing;
  }

}