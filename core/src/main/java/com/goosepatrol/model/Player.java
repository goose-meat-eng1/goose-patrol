package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * The student the player controls (FR_MOVE, FR_MAP). The student owns its movement it moves in eight directions,
 * speeds up and slows down smoothly and stops at the map's edges and at walls.
 */
public class Player {

  /** The width of the student's box, in map pixels. */
  public static final float WIDTH = 12;

  /** The height of the student's box, in map pixels. */
  public static final float HEIGHT = 12;

  // How fast the speed rises and falls, in map pixels per second, each second.
  private static final float ACCELERATION = GameConfig.STUDENT_MAX_SPEED / GameConfig.STUDENT_SPEED_UP_SECONDS;
  private static final float DECELERATION = GameConfig.STUDENT_MAX_SPEED / GameConfig.STUDENT_STOP_SECONDS;

  /** The student's collision box, in map pixels, which moves and stops at walls. */
  private final MovingBox body;

  private final Vector2 velocity = new Vector2();
  private final Vector2 direction = new Vector2();
  private Facing facing = Facing.DOWN;

  // A boost such as the coffee multiplies the top speed by speedBoost for boostSecondsLeft seconds (FR_PICKUP).  
  private float speedBoost = 1;
  private float boostSecondsLeft = 0;

  private float stamina = GameConfig.PLAYER_STAMINA;

  /**
   * Makes the student, standing still and facing down.
   */
  public Player(Vector2 centre) {
    body = new MovingBox(centre.x - WIDTH / 2, centre.y - HEIGHT / 2, WIDTH, HEIGHT);
  }

  /**
   * Moves the student by one frame. The student turns to face the keys, speeds up towards full speed in the keys'
   * direction or slows to a stop, then moves across and up, stopping at the map's edges and at walls.
   */
  public void update(float inputX, float inputY, float delta, CampusMap map) {
    facing = facingFor(inputX, inputY, facing);
    direction.set(inputX, inputY);
    if (direction.len2() > 1) {
      direction.nor(); // walking diagonally is no faster than walking straight
    }
    float topSpeed = GameConfig.STUDENT_MAX_SPEED;
    if (boostSecondsLeft > 0) {
      topSpeed *= speedBoost;
      boostSecondsLeft -= delta;
    }
    velocity.x = approach(velocity.x, direction.x * topSpeed, Math.abs(direction.x), delta);
    velocity.y = approach(velocity.y, direction.y * topSpeed, Math.abs(direction.y), delta);
    // A blocked move loses speed on that axis only, so the student slides along walls.
    if (body.moveX(velocity.x * delta, map)) {
      velocity.x = 0;
    }
    if (body.moveY(velocity.y * delta, map)) {
      velocity.y = 0;
    }
  }

  public void boostSpeed(float factor, float seconds) {
    speedBoost = factor;
    boostSecondsLeft = seconds;
  }

  public float getBoostSecondsLeft() {
    return Math.max(0, boostSecondsLeft);
  }

  public float reduceStamina(float amount) {
    stamina = Math.max(0, stamina - amount);
    return stamina;
  }

  /**
   * Works out which way the student faces. With no keys held, the student keeps facing the same way. 
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


  public Rectangle getBounds() {
    return new Rectangle(body.getBounds());
  }


  public Vector2 getVelocity() {
    return new Vector2(velocity);
  }

  
  public Facing getFacing() {
    return facing;
  }


  public float getStamina() {
    return stamina;
  }
}
