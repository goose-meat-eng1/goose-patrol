package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/** Throws bread by itself at the nearest hungry goose in range (FR_BREAD). */
public class BreadDispenser {

  /** Seconds until the next throw; 0 means ready. */
  private float secondsToReady;

  /** Throws a piece if it is ready and a goose is in range, and returns it, or null (FR_BREAD). */
  public Bread update(float delta, Rectangle student, List<Goose> geese, List<Bread> bread) {
    secondsToReady = Math.max(0, secondsToReady - delta);
    if (secondsToReady > 0) {
      return null;
    }
    Vector2 from = student.getCenter(new Vector2());
    Goose target = nearestHungryGoose(from, geese, bread);
    if (target == null) {
      return null;
    }
    secondsToReady = GameConfig.BREAD_GAP_SECONDS;
    return new Bread(from, target);
  }

  // The nearest chasing goose in range that the bread on its way won't fill, or null.
  private static Goose nearestHungryGoose(Vector2 from, List<Goose> geese, List<Bread> bread) {
    Goose nearest = null;
    float nearestDistance2 = GameConfig.BREAD_RANGE * GameConfig.BREAD_RANGE; // squared, to skip the square roots
    for (Goose goose : geese) {
      Rectangle box = goose.getBounds();
      float distance2 = from.dst2(box.x + box.width / 2, box.y + box.height / 2);
      boolean hungry = goose.getFullness() + piecesOnTheWay(goose, bread) < GameConfig.GOOSE_FULLNESS;
      if (goose.getState() == Goose.State.CHASING && hungry && distance2 <= nearestDistance2) {
        nearest = goose;
        nearestDistance2 = distance2;
      }
    }
    return nearest;
  }

  // How many pieces are flying to this goose.
  private static int piecesOnTheWay(Goose goose, List<Bread> bread) {
    int count = 0;
    for (Bread piece : bread) {
      if (piece.getTarget() == goose && !piece.isGone()) {
        count++;
      }
    }
    return count;
  }
}
