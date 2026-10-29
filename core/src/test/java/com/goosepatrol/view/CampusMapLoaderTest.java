package com.goosepatrol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.goosepatrol.model.CampusMap;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class CampusMapLoaderTest {

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
  void readsDeepWaterAsWallsForTheStudent() {
    TiledMap map = tiledMap(false, true);
    MapLayer deepWater = new MapLayer();
    deepWater.setName(CampusMapLoader.DEEP_WATER_LAYER);
    deepWater.getObjects().add(new RectangleMapObject(96, 32, 48, 32));
    map.getLayers().add(deepWater);

    CampusMap campus = CampusMapLoader.fromTiledMap(map);

    assertNotNull(campus.wallOverlapping(new Rectangle(110, 40, 4, 4)));
  }
}
