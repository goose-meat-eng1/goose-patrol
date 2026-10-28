package com.goosepatrol.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * The student the player controls (FR_MOVE, FR_MAP). The student owns its movement: it moves in eight directions,
 * speeds up and slows down smoothly, and stops at the map's edges and at walls. Moves are made one axis at a time,
 * so walking diagonally into a wall slides along it instead of sticking.
 */
public class Player {

  /** The width of the student's box, in map pixels. */
  public static final float WIDTH = 12;

  /** The height of the student's box, in map pixels. */
  public static final float HEIGHT = 12;

  // How fast the speed rises and falls, in map pixels per second, each second.
  private static final float ACCELERATION = GameConfig.STUDENT_MAX_SPEED / GameConfig.STUDENT_SPEED_UP_SECONDS;
  private static final float DECELERATION = GameConfig.STUDENT_MAX_SPEED / GameConfig.STUDENT_STOP_SECONDS;

  private final Rectangle bounds;
  private final Vector2 velocity = new Vector2();
  private final Vector2 direction = new Vector2();
  private Facing facing = Facing.DOWN;

  /**
   * Makes the student, standing still and facing down.
   *
   * @param centre the centre of the student's starting place, such as the map's student start
   */
  public Player(Vector2 centre) {
    bounds = new Rectangle(centre.x - WIDTH / 2, centre.y - HEIGHT / 2, WIDTH, HEIGHT);
  }

  /**
   * Moves the student by one frame. The student turns to face the keys, speeds up towards full speed in the keys'
   * direction or slows to a stop, then moves across and up, stopping at the map's edges and at walls.
   *
   * @param inputX the keys' left-right direction: -1, 0 or 1
   * @param inputY the keys' down-up direction: -1, 0 or 1
   * @param delta the frame's length in seconds
   * @param map the map, for its edges and walls
   */
  public void update(float inputX, float inputY, float delta, CampusMap map) {
    facing = facingFor(inputX, inputY, facing);
    direction.set(inputX, inputY);
    if (direction.len2() > 1) {
      direction.nor(); // walking diagonally is no faster than walking straight
    }
    velocity.x = approach(velocity.x, direction.x * GameConfig.STUDENT_MAX_SPEED, Math.abs(direction.x), delta);
    velocity.y = approach(velocity.y, direction.y * GameConfig.STUDENT_MAX_SPEED, Math.abs(direction.y), delta);
    moveX(velocity.x * delta, map);
    moveY(velocity.y * delta, map);
  }

  /**
   * Works out which way the student faces. With no keys held, the student keeps facing the same way. On a diagonal,
   * the student keeps the current way if one of the keys still points that way, so it doesn't flick between two.
   *
   * @param inputX the keys' left-right direction: -1, 0 or 1
   * @param inputY the keys' down-up direction: -1, 0 or 1
   * @param current the way the student faces now
   * @return the way the student faces after this frame
   */
  static Facing facingFor(float inputX, float inputY, Facing current) {
    if (inputX == 0 && inputY == 0) {
      return current;
    }
    boolean stillHeld = (current == Facing.LEFT && inputX < 0)  ||
                        (current == Facing.RIGHT && inputX > 0) ||
                        (current == Facing.DOWN && inputY < 0)  ||
                        (current == Facing.UP && inputY > 0);
    if (stillHeld) {
      return current;
    }
    if (inputX != 0) {
      return inputX < 0 ? Facing.LEFT : Facing.RIGHT;
    }
    return inputY < 0 ? Facing.DOWN : Facing.UP;
  }

  /**
   * Moves a speed towards a target speed, by at most one frame's change. Speeding up from standing to full speed
   * takes {@link GameConfig#STUDENT_SPEED_UP_SECONDS}; slowing from full speed to a stop takes
   * {@link GameConfig#STUDENT_STOP_SECONDS}.
   *
   * @param speed the speed now, along one axis
   * @param target the speed the keys ask for, along the same axis
   * @param share this axis's share of the direction, so a diagonal speeds up as fast as a straight line
   * @param delta the frame's length in seconds
   * @return the new speed
   */
  static float approach(float speed, float target, float share, float delta) {
    // Speeding up means the target points the same way as the speed (or the student stands still) and is faster.
    boolean speedingUp = speed * target >= 0 && Math.abs(target) > Math.abs(speed);
    float rate = speedingUp ? share * ACCELERATION : DECELERATION;
    float step = rate * delta; // the most the speed may change in this frame
    if (Math.abs(target - speed) <= step) {
      return target;
    }
    return speed + Math.signum(target - speed) * step;
  }

  // Moves across, keeping the student inside the map and out of walls.
  private void moveX(float dx, CampusMap map) {
    if (dx == 0) {
      return;
    }
    float oldX = bounds.x;
    float wantedX = oldX + dx;
    bounds.x = MathUtils.clamp(wantedX, 0, map.getWidth() - bounds.width);
    Rectangle wall = map.wallOverlapping(bounds);
    if (wall != null) {
      bounds.x = dx > 0 ? wall.x - bounds.width : wall.x + wall.width; // right against the wall's near side
      if (map.wallOverlapping(bounds) != null) {
        bounds.x = oldX; // squeezed between two walls: stay where it was
      }
    }
    if (bounds.x != wantedX) {
      velocity.x = 0; // blocked: lose speed on this axis only, so the student slides along walls
    }
  }

  // Moves up or down, the same way as moveX.
  private void moveY(float dy, CampusMap map) {
    if (dy == 0) {
      return;
    }
    float oldY = bounds.y;
    float wantedY = oldY + dy;
    bounds.y = MathUtils.clamp(wantedY, 0, map.getHeight() - bounds.height);
    Rectangle wall = map.wallOverlapping(bounds);
    if (wall != null) {
      bounds.y = dy > 0 ? wall.y - bounds.height : wall.y + wall.height;
      if (map.wallOverlapping(bounds) != null) {
        bounds.y = oldY;
      }
    }
    if (bounds.y != wantedY) {
      velocity.y = 0;
    }
  }

  /**
   * Returns the student's box.
   *
   * @return a copy of the box, in map pixels
   */
  public Rectangle getBounds() {
    return new Rectangle(bounds);
  }

  /**
   * Returns the student's speed.
   *
   * @return a copy of the speed, in map pixels per second
   */
  public Vector2 getVelocity() {
    return new Vector2(velocity);
  }

  /**
   * Returns which way the student faces.
   *
   * @return DOWN, UP, LEFT or RIGHT
   */
  public Facing getFacing() {
    return facing;
  }
}
