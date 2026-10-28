package com.goosepatrol.view;

import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.goosepatrol.model.CampusMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the Tiled map into the model's {@link CampusMap} (FR_MAP), so the model never reads a file. The walls come
 * from tiles ticked "solid" or "water" in their tileset, on any tile layer, and from the rectangles on the optional
 * "Collisions" layer. Water has its own tick, so geese can be let across it later. The point called "student" on the
 * "Spawns" layer is where the student starts.
 */
public final class CampusMapLoader {

  /** The Tiled object layer whose rectangles are extra walls. The layer is optional. */
  public static final String COLLISIONS_LAYER = "Collisions";

  /** The tile property that makes a tile a wall: a {@code bool} called "solid", ticked in the tileset. */
  public static final String SOLID_PROPERTY = "solid";

  /** The tile property that marks water: a {@code bool} called "water". Water stops the student like a wall. */
  public static final String WATER_PROPERTY = "water";

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

    List<Rectangle> walls = solidTileWalls(tiledMap.getLayers()); // walls from tiles ticked solid or water
    // The Collisions layer is optional. If the map has one, each rectangle drawn on it in Tiled is an extra wall.
    MapLayer collisions = tiledMap.getLayers().get(COLLISIONS_LAYER);
    if (collisions != null) {
      for (RectangleMapObject wall : collisions.getObjects().getByType(RectangleMapObject.class)) {
        walls.add(new Rectangle(wall.getRectangle()));
      }
    }

    MapLayer spawns = tiledMap.getLayers().get(SPAWNS_LAYER);
    MapObject student = spawns == null ? null : spawns.getObjects().get(STUDENT_SPAWN);
    if (!(student instanceof PointMapObject)) {
      throw new IllegalArgumentException("The map has no \"" + STUDENT_SPAWN + "\" point in its \"" + SPAWNS_LAYER
          + "\" layer");
    }
    return new CampusMap(width, height, walls, ((PointMapObject) student).getPoint());
  }

  /**
   * Finds the walls painted as tiles ticked "solid" or "water", on every tile layer, including those inside group
   * layers.
   *
   * @param layers the layers to look through
   * @return the walls, in map pixels, with y counted upwards
   */
  static List<Rectangle> solidTileWalls(MapLayers layers) {
    List<Rectangle> walls = new ArrayList<>();
    for (MapLayer layer : layers) {
      if (layer instanceof MapGroupLayer group) {
        walls.addAll(solidTileWalls(group.getLayers()));
      } else if (layer instanceof TiledMapTileLayer tiles) {
        walls.addAll(mergeSolidCells(tiles));
      }
    }
    return walls;
  }

  /**
   * Merges one layer's wall tiles into rectangles. It reads the layer row by row, from the bottom up, and finds runs
   * of wall tiles in each row. A run with the same start and length as one in the row below makes that rectangle a
   * row taller; any other run starts a new rectangle. It doesn't always find the fewest rectangles, but it turns a
   * building into one or a few walls instead of one per tile.
   *
   * @param layer the tile layer
   * @return its walls, in map pixels, with y counted upwards
   */
  private static List<Rectangle> mergeSolidCells(TiledMapTileLayer layer) {
    List<Rectangle> walls = new ArrayList<>();
    Map<Long, Rectangle> open = new HashMap<>(); // the rectangles that reached the row below
    for (int row = 0; row < layer.getHeight(); row++) {
      Map<Long, Rectangle> next = new HashMap<>(); // the rectangles that reach this row
      int column = 0;
      while (column < layer.getWidth()) {
        if (!isWall(layer.getCell(column, row))) {
          column++;
          continue;
        }
        int start = column;
        while (column < layer.getWidth() && isWall(layer.getCell(column, row))) {
          column++;
        }
        long key = (long) start << 32 | (column - start); // the run's start column and length, in one number
        Rectangle wall = open.get(key);
        if (wall == null) {
          wall = new Rectangle(start * layer.getTileWidth(), row * layer.getTileHeight(),
              (column - start) * layer.getTileWidth(), layer.getTileHeight());
          walls.add(wall);
        } else {
          wall.height += layer.getTileHeight(); // the same run as in the row below: one row taller
        }
        next.put(key, wall);
      }
      open = next;
    }
    return walls;
  }

  // A cell is a wall if its tile is ticked "solid" or "water" in its tileset.
  private static boolean isWall(TiledMapTileLayer.Cell cell) {
    if (cell == null || cell.getTile() == null) {
      return false;
    }
    MapProperties properties = cell.getTile().getProperties();
    return isTicked(properties.get(SOLID_PROPERTY)) || isTicked(properties.get(WATER_PROPERTY));
  }

  // libGDX gives a Tiled bool property as a Boolean, and an untyped one as the text "true".
  private static boolean isTicked(Object value) {
    return Boolean.TRUE.equals(value) || "true".equals(value);
  }
}
