package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class CampusMapTest {

  private final Rectangle wall = new Rectangle(100, 100, 32, 32);
  private final CampusMap map = new CampusMap(400, 300, List.of(wall), new Vector2(50, 60));

  @Test
  @Tag("FR_MAP")
  void knowsItsSizeAndStart() {
    assertEquals(400, map.getWidth());
    assertEquals(300, map.getHeight());
    assertEquals(new Vector2(50, 60), map.getStudentStart());
  }

  @Test
  @Tag("FR_MAP")
  void containsOnlyBoxesWhollyInside() {
    assertTrue(map.contains(new Rectangle(0, 0, 12, 12)));
    assertTrue(map.contains(new Rectangle(388, 288, 12, 12)));
    assertFalse(map.contains(new Rectangle(-1, 0, 12, 12)));
    assertFalse(map.contains(new Rectangle(389, 0, 12, 12)));
    assertFalse(map.contains(new Rectangle(0, -1, 12, 12)));
    assertFalse(map.contains(new Rectangle(0, 289, 12, 12)));
  }

  @Test
  @Tag("FR_MAP")
  void findsOnlyWallsABoxReallyOverlaps() {
    assertSame(wall, map.wallOverlapping(new Rectangle(110, 110, 12, 12)));
    assertNull(map.wallOverlapping(new Rectangle(88, 100, 12, 12)));
    assertNull(map.wallOverlapping(new Rectangle(10, 10, 12, 12)));
  }
}
