package com.goosepatrol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.goosepatrol.model.CampusMap;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class CampusMapLoaderTest {

  // A tile with no picture, ticked "solid" or "water" if asked.
  private static TiledMapTile tile(String ticked) {
    StaticTiledMapTile tile = new StaticTiledMapTile(new TextureRegion());
    if (ticked != null) {
      tile.getProperties().put(ticked, true);
    }
    return tile;
  }

  // A 10 x 5 tile layer drawn as text, top row first: # is solid, ~ is water, o is a plain tile, . is empty.
  private static TiledMapTileLayer layer(String... rows) {
    TiledMapTileLayer layer = new TiledMapTileLayer(10, 5, 16, 16);
    for (int i = 0; i < rows.length; i++) {
      int row = rows.length - 1 - i; // the picture's top row is the layer's highest row
      for (int column = 0; column < rows[i].length(); column++) {
        char c = rows[i].charAt(column);
        if (c != '.') {
          String ticked = c == '#' ? CampusMapLoader.SOLID_PROPERTY : c == '~' ? CampusMapLoader.WATER_PROPERTY : null;
          layer.setCell(column, row, new TiledMapTileLayer.Cell().setTile(tile(ticked)));
        }
      }
    }
    return layer;
  }

  // A 10 x 5 map of 16 px tiles, with or without a Collisions rectangle and a student point.
  private static TiledMap tiledMap(boolean withCollisions, boolean withStudent) {
    TiledMap map = new TiledMap();
    map.getProperties().put("width", 10);
    map.getProperties().put("height", 5);
    map.getProperties().put("tilewidth", 16);
    map.getProperties().put("tileheight", 16);
    if (withCollisions) {
      MapLayer collisions = new MapLayer();
      collisions.setName(CampusMapLoader.COLLISIONS_LAYER);
      collisions.getObjects().add(new RectangleMapObject(32, 16, 48, 16));
      map.getLayers().add(collisions);
    }
    MapLayer spawns = new MapLayer();
    spawns.setName(CampusMapLoader.SPAWNS_LAYER);
    if (withStudent) {
      PointMapObject student = new PointMapObject(24, 40);
      student.setName(CampusMapLoader.STUDENT_SPAWN);
      spawns.getObjects().add(student);
    }
    map.getLayers().add(spawns);
    return map;
  }

  @Test
  @Tag("FR_MAP")
  void readsTheSizeTheWallsAndTheStart() {
    CampusMap campus = CampusMapLoader.fromTiledMap(tiledMap(true, true));

    assertEquals(160, campus.getWidth());
    assertEquals(80, campus.getHeight());
    assertEquals(new Vector2(24, 40), campus.getStudentStart());
    assertNotNull(campus.wallOverlapping(new Rectangle(40, 20, 4, 4)));
    assertNull(campus.wallOverlapping(new Rectangle(0, 0, 4, 4)));
  }

  @Test
  @Tag("FR_MAP")
  void readsAMapWithoutACollisionsLayer() {
    CampusMap campus = CampusMapLoader.fromTiledMap(tiledMap(false, true));

    assertNull(campus.wallOverlapping(new Rectangle(0, 0, 160, 80)));
  }

  @Test
  @Tag("FR_MAP")
  void readsWallsFromSolidTiles() {
    TiledMap map = tiledMap(false, true);
    map.getLayers().add(layer(
        "..........",
        "..###.....",
        "..###..o..",
        "..........",
        ".........."));

    CampusMap campus = CampusMapLoader.fromTiledMap(map);

    assertNotNull(campus.wallOverlapping(new Rectangle(40, 40, 4, 4)));
    assertNull(campus.wallOverlapping(new Rectangle(116, 36, 4, 4)));
  }

  @Test
  @Tag("FR_MAP")
  void readsWaterTilesAsWallsForTheStudent() {
    TiledMap map = tiledMap(false, true);
    map.getLayers().add(layer(
        "..........",
        "......~~~.",
        "......~~~.",
        "..........",
        ".........."));

    CampusMap campus = CampusMapLoader.fromTiledMap(map);

    assertNotNull(campus.wallOverlapping(new Rectangle(110, 40, 4, 4)));
  }

  @Test
  @Tag("FR_MAP")
  void mergesABlockOfSolidTilesIntoOneWall() {
    TiledMap map = new TiledMap();
    map.getLayers().add(layer(
        "..........",
        "..###.....",
        "..###.....",
        "..........",
        ".........."));

    List<Rectangle> walls = CampusMapLoader.solidTileWalls(map.getLayers());

    assertEquals(List.of(new Rectangle(32, 32, 48, 32)), walls);
  }

  @Test
  @Tag("FR_MAP")
  void complainsWithoutAStudentStart() {
    assertThrows(IllegalArgumentException.class, () -> CampusMapLoader.fromTiledMap(tiledMap(true, false)));
  }
}
