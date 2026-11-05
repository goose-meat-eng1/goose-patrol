package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** A piece of bread flying to a goose (FR_BREAD). It follows the goose, so it can't miss. */
public class Bread {

  /** The middle of the bread, in map pixels. */
  private final Vector2 middle;

  private final Goose target;
  private final Vector2 way = new Vector2();
  private boolean gone;

  /** Throws a piece from a point, in map pixels, at a goose (FR_BREAD). */
  public Bread(Vector2 from, Goose target) {
    this.middle = new Vector2(from);
    this.target = target;
  }

  /** Moves the bread one frame, and returns true when it reaches its goose (FR_BREAD). */
  public boolean update(float delta) {
    if (gone) {
      return false;
    }
    if (target.getState() != Goose.State.CHASING) {
      gone = true; // the goose is already full and flying off, so nobody eats this piece
      return false;
    }
    Rectangle box = target.getBounds();
    way.set(box.x + box.width / 2, box.y + box.height / 2).sub(middle);
    float distance = way.len();
    float step = GameConfig.BREAD_SPEED * delta;
    if (distance <= step) {
      middle.add(way);
      gone = true;
      return true;
    }
    middle.mulAdd(way, step / distance);
    return false;
  }

  public Goose getTarget() {
    return target;
  }

  public boolean isGone() {
    return gone;
  }

  public Vector2 getMiddle() {
    return new Vector2(middle);
  }
}
