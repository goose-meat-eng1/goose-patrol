package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Brings the geese in waves that get bigger and come sooner (FR_SPAWN). */
public class GooseSpawner {

  /** Places to try for one goose before leaving it out, so a crowded spot can't hang the game. */
  private static final int TRIES = 12;

  private final Random random;

  /** Seconds played so far. */
  private float playSeconds;

  /** When the next wave is due, in seconds played. */
  private float nextWaveAt = GameConfig.CALM_START_SECONDS;

  private int waves;

  /** Makes the spawner for one run; a test can pass a Random with a fixed seed. */
  public GooseSpawner(Random random) {
    this.random = random;
  }

  /** Brings in a wave when one is due, and returns the new geese (FR_SPAWN). */
  public List<Goose> update(float delta, Rectangle student, CampusMap map, List<Goose> geese) {
    playSeconds += delta;
    if (playSeconds < nextWaveAt) {
      return List.of();
    }
    waves++;
    nextWaveAt += waveGap(waves);
    int size = Math.min(waveSize(waves), GameConfig.MAX_GEESE - geese.size());
    List<Goose> arrivals = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      Vector2 place = arrivalPlace(student, map, geese, arrivals);
      if (place != null) {
        GooseKind kind = random.nextFloat() < GameConfig.WHITE_GOOSE_CHANCE ? GooseKind.WHITE : GooseKind.CANADIAN;
        arrivals.add(new Goose(place, kind));
      }
    }
    return arrivals;
  }

  /** How many geese a wave brings, counting waves from 1 (FR_SPAWN). */
  static int waveSize(int wave) {
    return GameConfig.FIRST_WAVE_SIZE + (wave - 1) * GameConfig.WAVE_GROWTH;
  }

  /** The seconds between a wave and the next one, counting waves from 1 (FR_SPAWN). */
  static float waveGap(int wave) {
    return Math.max(GameConfig.MIN_WAVE_GAP, GameConfig.FIRST_WAVE_GAP - (wave - 1) * GameConfig.WAVE_GAP_SHRINK);
  }

  // A random place off screen, inside the map and clear of walls and geese; null if none is found.
  private Vector2 arrivalPlace(Rectangle student, CampusMap map, List<Goose> geese, List<Goose> arrivals) {
    for (int i = 0; i < TRIES; i++) {
      Vector2 place = new Vector2(GameConfig.SPAWN_DISTANCE, 0).setAngleDeg(random.nextFloat() * 360)
          .add(student.x + student.width / 2, student.y + student.height / 2);
      Rectangle box = new Rectangle(place.x - Goose.WIDTH / 2, place.y - Goose.HEIGHT / 2, Goose.WIDTH, Goose.HEIGHT);
      if (map.contains(box) && map.solidOverlapping(box) == null
          && !aGooseStandsIn(box, geese) && !aGooseStandsIn(box, arrivals)) {
        return place;
      }
    }
    return null;
  }

  // Whether a goose on the ground stands in the box.
  private static boolean aGooseStandsIn(Rectangle box, List<Goose> geese) {
    for (Goose goose : geese) {
      if (goose.blocks(box)) {
        return true;
      }
    }
    return false;
  }

  /**
 * Brings the Goose Man in off screen, the way a goose arrives (FR_BOSS).
 */
  public GooseMan bringGooseMan(Rectangle student, CampusMap map, List<Goose> geese) {
    Vector2 place = arrivalPlace(student, map, geese, List.of());
    return place == null ? null : new GooseMan(place, map, random);
  }
}
