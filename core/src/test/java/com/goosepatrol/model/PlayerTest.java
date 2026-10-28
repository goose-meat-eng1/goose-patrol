package com.goosepatrol.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private static final float FRAME = 1f / 60;
  private static final float MAX = GameConfig.STUDENT_MAX_SPEED;
  private static final float SPEED_UP = GameConfig.STUDENT_SPEED_UP_SECONDS;
  private static final float STOP = GameConfig.STUDENT_STOP_SECONDS;
  private static final Vector2 START = new Vector2(500, 500);
  private static final CampusMap OPEN_GROUND = new CampusMap(1000, 1000, List.of(), START);

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
  @Tag("FR_MAP")
  void startsCentredOnItsStartingPlace() {
    Rectangle box = new Player(START).getBounds();

    assertEquals(START.x - Player.WIDTH / 2, box.x);
    assertEquals(START.y - Player.HEIGHT / 2, box.y);
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
  void speedsUpEvenly() {
    Player player = new Player(START);
    holdFrames(player, 1, 0, frames(SPEED_UP / 2, 120), 1f / 120);

    assertEquals(MAX / 2, player.getVelocity().x, 0.01);
  }

  @Test
  @Tag("FR_MOVE")
  void reachesFullSpeedDiagonallyInTheSameTime() {
    Player player = new Player(START);
    holdFrames(player, 1, 1, frames(SPEED_UP, 60) - 1, FRAME);
    assertTrue(player.getVelocity().len() < MAX);

    holdFrames(player, 1, 1, 1, FRAME);
    assertEquals(MAX, player.getVelocity().len(), 0.01);
  }

  @Test
  @Tag("FR_MOVE")
  void stopsInTheStopTime() {
    Player player = new Player(START);
    hold(player, 1, 0, SPEED_UP + 0.5f, OPEN_GROUND);
    float letGoAt = player.getBounds().x;

    holdFrames(player, 0, 0, frames(STOP, 60) - 1, FRAME);
    assertTrue(player.getVelocity().x > 0);

    holdFrames(player, 0, 0, 1, FRAME);
    assertEquals(0, player.getVelocity().x, 0.001);
    float slide = player.getBounds().x - letGoAt;
    assertTrue(slide <= MAX * STOP / 2 && slide > MAX * STOP / 2 - MAX * FRAME);
  }

  @Test
  @Tag("FR_MOVE")
  void turnsBackByStoppingThenSpeedingUp() {
    Player player = new Player(START);
    hold(player, 1, 0, SPEED_UP + 0.5f, OPEN_GROUND);

    holdFrames(player, -1, 0, frames(STOP, 60), FRAME);
    assertEquals(0, player.getVelocity().x, 0.01);

    holdFrames(player, -1, 0, frames(SPEED_UP, 60), FRAME);
    assertEquals(-MAX, player.getVelocity().x, 0.001);
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
    CampusMap map = new CampusMap(1000, 1000, List.of(wall), START);
    Player player = new Player(START);
    hold(player, 1, 0, 3, map);

    assertEquals(wall.x, player.getBounds().x + Player.WIDTH);
    assertEquals(0, player.getVelocity().x);
  }

  @Test
  @Tag("FR_MAP")
  void slidesAlongAWall() {
    Rectangle wall = new Rectangle(600, 0, 32, 1000);
    CampusMap map = new CampusMap(1000, 1000, List.of(wall), START);
    Player player = new Player(START);
    hold(player, 1, 1, 2, map);

    assertEquals(wall.x, player.getBounds().x + Player.WIDTH);
    assertTrue(player.getBounds().y > START.y);
  }

  @Test
  @Tag("FR_MOVE")
  void facesTheWayTheKeysPoint() {
    Player player = new Player(START);
    assertEquals(Facing.DOWN, player.getFacing());

    hold(player, 1, 0, 0.1f, OPEN_GROUND);
    assertEquals(Facing.RIGHT, player.getFacing());

    hold(player, 0, 0, 0.1f, OPEN_GROUND);
    assertEquals(Facing.RIGHT, player.getFacing());

    hold(player, 0, 1, 0.1f, OPEN_GROUND);
    assertEquals(Facing.UP, player.getFacing());
  }

  @Test
  @Tag("FR_MOVE")
  void diagonalsKeepTheWayTheStudentFaces() {
    assertEquals(Facing.RIGHT, Player.facingFor(1, 1, Facing.RIGHT));
    assertEquals(Facing.UP, Player.facingFor(1, 1, Facing.UP));
    assertEquals(Facing.LEFT, Player.facingFor(-1, 1, Facing.DOWN));
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
