package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/** Throws bread by itself at the nearest hungry goose goose or Goose Man  in range (FR_BREAD). */
public class BreadDispenser {

  /** Seconds until the next throw; 0 means ready. */
  private float secondsToReady;
  private float dispenseBoostSeconds;
  private float dispenseBoostFactor;
  private int throwsWhileGooseManNear;

  /** Throws a piece if it is ready and a goose is in range, and returns it, or null (FR_BREAD). */
  public Bread update(float delta, Rectangle student, List<Goose> geese, GooseMan gooseMan, List<Bread> bread) {
    dispenseBoostSeconds = Math.max(0, dispenseBoostSeconds - delta);
    secondsToReady = Math.max(0, secondsToReady - delta);
    if (secondsToReady > 0) {
      return null;
    }

    Vector2 from = student.getCenter(new Vector2());
    Feedable target = pickTarget(from, geese, gooseMan, bread);

    if (target == null) {
      return null;
    }
    secondsToReady = GameConfig.BREAD_GAP_SECONDS;
    if (dispenseBoostSeconds > 0) {
      secondsToReady /= dispenseBoostFactor;
    }
    return new Bread(from, target);
  }

// If the Goose Man is in range and hungry, the last throw of each round goes to the nearest hungry goose and
  // the rest go to him (FR_BOSS). Otherwise the nearest hungry goose gets it.
  private Feedable pickTarget(Vector2 from, List<Goose> geese, GooseMan gooseMan, List<Bread> bread) {
    Goose goose = nearestHungryGoose(from, geese, bread);
    if (gooseMan == null || !isHungryInRange(from, gooseMan, bread)) {
      return goose;
    }
    throwsWhileGooseManNear++;
    boolean gooseTurn = throwsWhileGooseManNear % GameConfig.GOOSE_MAN_ROUND_THROWS == 0;
    if (gooseTurn && goose != null) {
      return goose;
    }
    return gooseMan;
  }

    // The nearest goose in range that can eat and that the bread on its way won't fill, or null.
  private static Goose nearestHungryGoose(Vector2 from, List<Goose> geese, List<Bread> bread) {
    Goose nearest = null;
    float nearestDistance2 = Float.MAX_VALUE;
    for (Goose goose : geese) {
      float distance2 = distance2(from, goose);
      if (isHungryInRange(from, goose, bread) && distance2 < nearestDistance2) {
        nearest = goose;
        nearestDistance2 = distance2;
      }
    }
    return nearest;
  }

  // Whether it can eat, is in range, and won't be filled by the bread already on its way.
  private static boolean isHungryInRange(Vector2 from, Feedable target, List<Bread> bread) {
    boolean hungry = target.getFullness() + piecesOnTheWay(target, bread) < target.getMaxFullness();
    boolean inRange = distance2(from, target) <= GameConfig.BREAD_RANGE * GameConfig.BREAD_RANGE;
    return target.canEat() && hungry && inRange;
  }

    // The squared distance to the middle of its box, which skips the square root.
  private static float distance2(Vector2 from, Feedable target) {
    Rectangle box = target.getBounds();
    return from.dst2(box.x + box.width / 2, box.y + box.height / 2);
  }

// How many pieces are flying to this target.
  private static int piecesOnTheWay(Feedable target, List<Bread> bread) {
    int count = 0;
    for (Bread piece : bread) {
      if (piece.getTarget() == target && !piece.isGone()) {
        count++;
      }
    }
    return count;
  }

  public void dispenseBoost(float factor, float seconds) {
    dispenseBoostSeconds = seconds;
    dispenseBoostFactor = factor;
    secondsToReady /= factor;
  }

  public float getDispenseBoostSecondsRemaining() {
    return dispenseBoostSeconds;
  }
}
