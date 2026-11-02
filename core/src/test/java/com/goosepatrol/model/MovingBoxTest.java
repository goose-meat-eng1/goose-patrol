package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class MovingBoxTest {

  @Test
  @Tag("FR_MAP")
  void saysWhenAWallCutsTheMoveShort() {
    Rectangle wall = new Rectangle(100, 0, 32, 1000);
    CampusMap map = new CampusMap(1000, 1000, List.of(wall), new Vector2(500, 500), new TiledMap());
    MovingBox box = new MovingBox(50, 500, 12, 12);

    assertFalse(box.moveX(10, map));
    assertTrue(box.moveX(50, map));
  }
}
