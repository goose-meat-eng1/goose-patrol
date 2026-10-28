package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * The campus the student walks on (FR_MAP): its size, its walls and where the student starts. Sizes are in map
 * pixels, with y growing upwards, as in libGDX. The map only answers questions; the student moves itself.
 */
public class CampusMap {

  private final float width;
  private final float height;
  private final List<Rectangle> walls;
  private final Vector2 studentStart;

  /**
   * Makes the map. The game builds it from the Tiled map with {@code CampusMapLoader}; tests can build their own.
   *
   * @param width the map's width in map pixels
   * @param height the map's height in map pixels
   * @param walls the walls the student can't walk through; the list is copied
   * @param studentStart the centre of the student's starting place
   */
  public CampusMap(float width, float height, List<Rectangle> walls, Vector2 studentStart) {
    this.width = width;
    this.height = height;
    this.walls = List.copyOf(walls);
    this.studentStart = new Vector2(studentStart);
  }

  /**
   * Finds a wall that a box overlaps. Boxes that only touch a wall's edge don't overlap it.
   *
   * @param box the box to check, such as the student's
   * @return a wall the box overlaps, or null if there is none
   */
  public Rectangle wallOverlapping(Rectangle box) {
    for (Rectangle wall : walls) {
      if (wall.overlaps(box)) {
        return wall;
      }
    }
    return null;
  }

  /**
   * Says whether a box lies wholly inside the map.
   *
   * @param box the box to check
   * @return true if no part of the box is outside the map
   */
  public boolean contains(Rectangle box) {
    return box.x >= 0 && box.y >= 0 && box.x + box.width <= width && box.y + box.height <= height;
  }

  /**
   * Returns the map's width.
   *
   * @return the width in map pixels
   */
  public float getWidth() {
    return width;
  }

  /**
   * Returns the map's height.
   *
   * @return the height in map pixels
   */
  public float getHeight() {
    return height;
  }

  /**
   * Returns where the student starts.
   *
   * @return a copy of the centre of the student's starting place
   */
  public Vector2 getStudentStart() {
    return new Vector2(studentStart);
  }
}
