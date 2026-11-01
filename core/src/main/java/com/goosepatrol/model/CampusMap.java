package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * The campus the student walks on (FR_MAP): its size, its walls and where the
 * student starts. Sizes are in map
 * pixels, with y growing upwards, as in libGDX. The map only answers questions;
 * the student moves itself.
 */
public class CampusMap {

  private final float width;
  private final float height;
  private final Vector2 studentStart;

  private final List<Rectangle> solids;
  private final List<Rectangle> deepWater;
  private final List<Rectangle> shallowWater;

  /**
   * Makes the map. The game builds it from the Tiled map with
   * {@code CampusMapLoader}; tests can build their own.
   *
   * @param width        the map's width in map pixels
   * @param height       the map's height in map pixels
   * @param solids        the walls the student can't walk through;
   * @param studentStart the centre of the student's starting place
   */
  public CampusMap(float width, float height, List<Rectangle> solids, Vector2 studentStart) {
    this(width, height, solids, List.of(), List.of(), studentStart);
  }

  public CampusMap(float width, float height, List<Rectangle> solids, List<Rectangle> deepWater,
      List<Rectangle> shallowWater, Vector2 studentStart) {
    this.width = width;
    this.height = height;
    this.solids = List.copyOf(solids);
    this.deepWater = List.copyOf(deepWater);
    this.shallowWater = List.copyOf(shallowWater);
    this.studentStart = new Vector2(studentStart);
  }

  /** Finds a wall or deep water that a box overlaps: what stops the student (FR_MAP). */
  public Rectangle wallOverlapping(Rectangle box) {
    Rectangle solid = solidOverlapping(box);
    return solid != null ? solid : firstOverlapping(deepWater, box);
  }

  /** Finds a solid wall that a box overlaps: what stops a goose, which can swim. */
  public Rectangle solidOverlapping(Rectangle box) {
    return firstOverlapping(solids, box);
  }

  /** Says a box overlaps shallow water. Nothing uses it until a requirement says what shallow water does. */
  public boolean inShallowWater(Rectangle box) {
    return firstOverlapping(shallowWater, box) != null;
  }

  // The first area a box overlaps, or null. Touching an edge doesn't count.
  private static Rectangle firstOverlapping(List<Rectangle> areas, Rectangle box) {
    for (Rectangle area : areas) {
      if (area.overlaps(box)) {
        return area;
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
