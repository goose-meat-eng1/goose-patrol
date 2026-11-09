package com.goosepatrol.view;

import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayers;
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
 * Turns the Tiled map into the model's CampusMap (FR_MAP), so the model never reads a file. The walls are the
 * rectangles drawn on the "Collisions" and "Deep water" object layers, so the student can't walk through a building
 * or into the lake. 
 */
public final class CampusMapLoader {

  /** The Tiled group that holds the Solids, Deep water and Shallow water layers. */
  public static final String COLLISIONS_GROUP = "Collisions";

  public static final String SOLIDS_LAYER = "Solids";

  /** The Tiled object layer whose rectangles are deep water, which stops the student. */
  public static final String DEEP_WATER_LAYER = "Deep water";

  /** The layer in the Collisions group whose rectangles are shallow water. */
  public static final String SHALLOW_WATER_LAYER = "Shallow water";

  /** The Tiled object layer that holds the starting places. */
  public static final String SPAWNS_LAYER = "Spawns";

  /** The name of the student's starting point in the spawns layer. */
  public static final String STUDENT_SPAWN = "student";

  private CampusMapLoader() {
  }

  /**
   * Reads the size, the walls and the student's start from a Tiled map.
   */
  public static CampusMap fromTiledMap(TiledMap tiledMap) {
    MapProperties properties = tiledMap.getProperties();
    float width = properties.get("width", Integer.class) * properties.get("tilewidth", Integer.class);
    float height = properties.get("height", Integer.class) * properties.get("tileheight", Integer.class);

    MapLayers collisions = collisionLayers(tiledMap);
    List<Rectangle> solids = rectangles(collisions, SOLIDS_LAYER);
    List<Rectangle> deepWater = rectangles(collisions, DEEP_WATER_LAYER);
    List<Rectangle> shallowWater = rectangles(collisions, SHALLOW_WATER_LAYER);

    MapLayer spawns = tiledMap.getLayers().get(SPAWNS_LAYER);
    MapObject student = spawns == null ? null : spawns.getObjects().get(STUDENT_SPAWN);
    if (!(student instanceof PointMapObject)) {
      throw new IllegalArgumentException("The map has no \"" + STUDENT_SPAWN + "\" point in its \"" + SPAWNS_LAYER
          + "\" layer");
    }
    
    return new CampusMap(width, height, solids, deepWater, shallowWater, ((PointMapObject) student).getPoint());

  }

  private static MapLayers collisionLayers(TiledMap tiledMap) {
    MapLayer group = tiledMap.getLayers().get(COLLISIONS_GROUP);
    if (group instanceof MapGroupLayer) {
      return ((MapGroupLayer) group).getLayers();
    }
    return new MapLayers(); // no group, so no walls and no water
  }

  /**
   * Reads the rectangles drawn on one object layer. Other shapes, such as polygons, are left out, so these layers
   * are drawn with Tiled's rectangle tool.*/
  private static List<Rectangle> rectangles(MapLayers layers, String layerName) {
    List<Rectangle> rectangles = new ArrayList<>();
    MapLayer layer = layers.get(layerName);
    if (layer != null) {
      for (RectangleMapObject shape : layer.getObjects().getByType(RectangleMapObject.class)) {
        rectangles.add(new Rectangle(shape.getRectangle()));
      }
    }
    return rectangles;
  }
}
