package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private static final float FRAME = 1f / 60;
  private static final Vector2 START = new Vector2(500, 500);
  private static final CampusMap OPEN_GROUND = new CampusMap(1000, 1000, List.of(), START);

  private static void hold(Player player, float x, float y, float seconds, CampusMap map) {
    int frames = Math.round(seconds * 60);
    for (int i = 0; i < frames; i++) {
      player.update(x, y, FRAME, map);
    }
  }

  @Test
  @Tag("FR_MAP")
  void stopsRightAgainstAWall() {
    Rectangle wall = new Rectangle(600, 400, 32, 200);
    CampusMap map = new CampusMap(1000, 1000, List.of(wall), START);
    Player player = new Player(START);
    hold(player, 1, 0, 3, map);

    assertEquals(wall.x, player.getBounds().x + Player.WIDTH);
    assertEquals(0, player.getVelocity().x);
  }

  @Test
  @Tag("FR_MAP")
  void staysInsideTheMap() {
    Player player = new Player(new Vector2(30, 30));
    hold(player, -1, -1, 3, OPEN_GROUND);

    assertEquals(0, player.getBounds().x);
    assertEquals(0, player.getBounds().y);
  }
}
