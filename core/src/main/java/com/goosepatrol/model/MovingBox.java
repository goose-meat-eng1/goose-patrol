package com.goosepatrol.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

final class MovingBox {
  /** The box, in map pixels: its bottom-left corner is (x, y). */
  private final Rectangle bounds;

  MovingBox(float x, float y, float width, float height) {
    bounds = new Rectangle(x, y, width, height);
  }

  // Moves across, keeping the box inside the map and out of walls. True means the move was cut short.
  boolean moveX(float dx, CampusMap map) {
    if (dx == 0) {
      return false;
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
    return bounds.x != wantedX;
  }

  // Moves up or down, the same way as moveX.
  boolean moveY(float dy, CampusMap map) {
    if (dy == 0) {
      return false;
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
    return bounds.y != wantedY;
  }

  // The box itself, not a copy, so Player hands out copies.
  Rectangle getBounds() {
    return bounds;
  }
}
