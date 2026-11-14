package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import java.util.Map;

/**
 * The campus the student walks on, with its size, its walls and where the student starts (FR_MAP).
 */
public class CampusMap {

  private final float width;
  private final float height;
  private final Vector2 studentStart;

  private final List<Rectangle> solids;
  private final List<Rectangle> deepWater;
  private final List<Rectangle> shallowWater;

  /** The hidden event zones, by name (FR_EVENT_HIDDEN). */
  private final Map<String, Rectangle> eventZones;

  /**
   * Makes the map. The game add CampusMapLoader.
   */
  public CampusMap(float width, float height, List<Rectangle> solids, Vector2 studentStart) {
    this(width, height, solids, List.of(), List.of(), Map.of(), studentStart);
  }

  public CampusMap(float width, float height, List<Rectangle> solids, List<Rectangle> deepWater,
      List<Rectangle> shallowWater, Map<String, Rectangle> eventZones, Vector2 studentStart) {
    this.width = width;
    this.height = height;
    this.solids = List.copyOf(solids);
    this.deepWater = List.copyOf(deepWater);
    this.shallowWater = List.copyOf(shallowWater);
    this.eventZones = Map.copyOf(eventZones);
    this.studentStart = new Vector2(studentStart);
  }

  /** Finds a wall or deep water that a box overlaps, which is what stops the student (FR_MAP). */
  public Rectangle wallOverlapping(Rectangle box) {
    Rectangle solid = solidOverlapping(box);
    return solid != null ? solid : firstOverlapping(deepWater, box);
  }

  /** Finds a solid wall that a box overlaps. Geese can swim, so only walls stop them. */
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
   */
  public boolean contains(Rectangle box) {
    return box.x >= 0 && box.y >= 0 && box.x + box.width <= width && box.y + box.height <= height;
  }


  public float getWidth() {
    return width;
  }


  public float getHeight() {
    return height;
  }


  public Vector2 getStudentStart() {
    return new Vector2(studentStart);
  }

  /** The hidden event zone with this name, or null if the map has none (FR_EVENT_HIDDEN). */
  public Rectangle getEventZone(String name) {
    Rectangle zone = eventZones.get(name);
    return zone == null ? null : new Rectangle(zone);
  }

}
