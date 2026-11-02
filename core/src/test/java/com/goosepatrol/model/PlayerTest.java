package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private static final float FRAME = 1f / 60;
  private static final float MAX = GameConfig.STUDENT_MAX_SPEED;
  private static final float SPEED_UP = GameConfig.STUDENT_SPEED_UP_SECONDS;
  private static final Vector2 START = new Vector2(500, 500);
  private static final CampusMap OPEN_GROUND = new CampusMap(1000, 1000, List.of(), START, new TiledMap());

  private static void hold(Player player, float x, float y, float seconds, CampusMap map) {
    int frames = Math.round(seconds * 60);
    for (int i = 0; i < frames; i++) {
      player.update(x, y, FRAME, map);
    }
  }

  private static void holdFrames(Player player, float x, float y, int frames, float frame) {
    for (int i = 0; i < frames; i++) {
      player.update(x, y, frame, OPEN_GROUND);
    }
  }

  private static int frames(float seconds, int fps) {
    return Math.round(seconds * fps);
  }

  @Test
  @Tag("FR_MOVE")
  void reachesFullSpeedInTheSpeedUpTime() {
    for (int fps : new int[] {60, 100}) {
      Player player = new Player(START);
      holdFrames(player, 1, 0, frames(SPEED_UP, fps) - 1, 1f / fps);
      assertTrue(player.getVelocity().x < MAX);

      holdFrames(player, 1, 0, 1, 1f / fps);
      assertEquals(MAX, player.getVelocity().x, 0.001);
    }
  }

  @Test
  @Tag("FR_MOVE")
  void walkingDiagonallyIsNoFaster() {
    Player player = new Player(START);
    hold(player, 1, 1, 1, OPEN_GROUND);

    assertTrue(player.getVelocity().len() <= MAX + 0.01f);
  }

  @Test
  @Tag("FR_MAP")
  void stopsRightAgainstAWall() {
    Rectangle wall = new Rectangle(600, 400, 32, 200);
    CampusMap map = new CampusMap(1000, 1000, List.of(wall), START, new TiledMap());
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
