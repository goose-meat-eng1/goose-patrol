package com.goosepatrol.view;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.goosepatrol.model.CampusMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns the Tiled map into the model's {@link CampusMap} (FR_MAP), so the model never reads a file. The walls are the
 * rectangles drawn on the "Collisions" and "Deep water" object layers, so the student can't walk through a building
 * or into the lake. The point called "student" on the "Spawns" layer is where the student starts. The tiles are only
 * pictures. The map's "Shallow water" layer isn't read yet, as no requirement says what shallow water does.
 */
public final class CampusMapLoader {

  /** The Tiled object layer whose rectangles are walls, such as the sides of the buildings. The layer is optional. */
  public static final String COLLISIONS_LAYER = "Collisions";

  /** The Tiled object layer whose rectangles are deep water, which stops the student. The layer is optional. */
  public static final String DEEP_WATER_LAYER = "Deep water";

  /** The Tiled object layer that holds the starting places. */
  public static final String SPAWNS_LAYER = "Spawns";

  /** The name of the student's starting point in the spawns layer. */
  public static final String STUDENT_SPAWN = "student";

  private CampusMapLoader() {
  }

  /**
   * Reads the size, the walls and the student's start from a Tiled map.
   *
   * @param tiledMap the map, as libGDX's TmxMapLoader loaded it
   * @return the campus for the model
   * @throws IllegalArgumentException if the map has no "student" point in its "Spawns" layer
   */
  public static CampusMap fromTiledMap(TiledMap tiledMap) {
    MapProperties properties = tiledMap.getProperties();
    float width = properties.get("width", Integer.class) * properties.get("tilewidth", Integer.class);
    float height = properties.get("height", Integer.class) * properties.get("tileheight", Integer.class);

    List<Rectangle> walls = rectangles(tiledMap, COLLISIONS_LAYER);
    walls.addAll(rectangles(tiledMap, DEEP_WATER_LAYER)); // the student can't swim, so deep water stops them

    MapLayer spawns = tiledMap.getLayers().get(SPAWNS_LAYER);
    MapObject student = spawns == null ? null : spawns.getObjects().get(STUDENT_SPAWN);
    if (!(student instanceof PointMapObject)) {
      throw new IllegalArgumentException("The map has no \"" + STUDENT_SPAWN + "\" point in its \"" + SPAWNS_LAYER
          + "\" layer");
    }
    return new CampusMap(width, height, walls, ((PointMapObject) student).getPoint());
  }

  /**
   * Reads the rectangles drawn on one object layer. Other shapes, such as polygons, are left out, so these layers
   * are drawn with Tiled's rectangle tool.
   *
   * @param tiledMap the map
   * @param layerName the object layer's name in Tiled
   * @return copies of the rectangles, in map pixels with y counted upwards, or an empty list if there is no such layer
   */
  private static List<Rectangle> rectangles(TiledMap tiledMap, String layerName) {
    List<Rectangle> rectangles = new ArrayList<>();
    MapLayer layer = tiledMap.getLayers().get(layerName);
    if (layer != null) {
      for (RectangleMapObject shape : layer.getObjects().getByType(RectangleMapObject.class)) {
        rectangles.add(new Rectangle(shape.getRectangle()));
      }
    }
    return rectangles;
  }
}
