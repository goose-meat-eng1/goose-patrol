package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One run of the game, from the start prompt to the end screen (D2, D3).
 *
 * <p>It holds the run's {@link RunState}, its {@link GameClock}, the student, the map, the geese, the bread and the
 * eggs. {@link #update(float)} changes them only while the run is PLAYING, so nothing moves while the run waits to
 * start, is paused or has ended. Later slices add the counters, the items and the events here.
 */
public class GameSession {

  private final GameClock clock;
  private final CampusMap map;
  private final Player player;

  private final Inventory inventory = new Inventory(3, 3);

  /** Null until enough geese have been fed (FR_BOSS). */
  private GooseMan gooseMan;

  /** Brings the geese in waves (FR_SPAWN); null means no geese, as in most tests. */
  private final GooseSpawner spawner;

  private final BreadDispenser dispenser = new BreadDispenser();
  private final List<Goose> geese = new ArrayList<>();
  private final List<Bread> bread = new ArrayList<>();
  private final List<Egg> eggs = new ArrayList<>();

  // FR_PAUSE: every run opens waiting for the start key.
  private RunState state = RunState.READY;

  // FR_MOVE: the held movement keys, each -1, 0 or 1, set every frame by the input handler.
  private int moveX;
  private int moveY;

  /** How many geese have been filled this run. */
  private int geeseFed;

  /** Makes a run with no geese that waits for the start key, for tests. */
  public GameSession(GameClock clock, CampusMap map, Player player) {
    this(clock, map, player, null);
  }

  /** Makes a run that waits for the start key. The game builds runs with {@link GameSetup#newSession(CampusMap)}. */
  public GameSession(GameClock clock, CampusMap map, Player player, GooseSpawner spawner) {
    this.clock = clock;
    this.map = map;
    this.player = player;
    this.spawner = spawner;
  }

  /**
   * Starts the run: READY becomes PLAYING (FR_PAUSE, D3). In any other state it does nothing.
   */
  public void start() {
    if (state == RunState.READY) {
      state = RunState.PLAYING;
    }
  }

  /**
   * Pauses or resumes the run: PLAYING becomes PAUSED, and PAUSED becomes PLAYING (FR_PAUSE, D3). The next call
   * to {@link #update(float)} already sees the new state, so a pause takes effect in the same frame
   * (FR_PAUSE). Before the start and after the end it does nothing.
   */
  public void togglePause() {
    if (state == RunState.PLAYING) {
      state = RunState.PAUSED;
    } else if (state == RunState.PAUSED) {
      state = RunState.PLAYING;
    }
  }

  /**
   * Moves the run on by one frame (D4). Only a PLAYING run changes: the clock counts down, the run ends when the
   * time is up (FR_TIMER), and otherwise the student, the geese and the bread move (FR_MOVE, FR_SPAWN, FR_BREAD). A
   * frame longer than {@link GameConfig#MAX_FRAME_SECONDS} counts as that much.
   *
   * @param delta the frame's length in seconds, as libGDX gives it
   */
  public void update(float delta) {
    if (state != RunState.PLAYING) {
      return; // FR_PAUSE: nothing changes while paused, before the start or after the end
    }
    float frame = Math.min(delta, GameConfig.MAX_FRAME_SECONDS);
    clock.tick(frame);
    if (clock.isUp()) {
      state = RunState.ENDED; // D3: when(timeLeft = 0)
      return;
    }
    player.update(moveX, moveY, frame, map);
    updateGeese(frame);
    updateGooseMan(frame);
    updateBread(frame);
  }

  // Brings in a wave when one is due, moves the geese, and drops the ones that have flown off (FR_SPAWN).
  private void updateGeese(float frame) {
    Rectangle student = player.getBounds();
    if (spawner != null) {
      geese.addAll(spawner.update(frame, student, map, geese));
    }
    Vector2 target = student.getCenter(new Vector2());
    for (Goose goose : geese) {
      goose.update(target, geese, frame, map);
    }
    geese.removeIf(goose -> goose.getState() == Goose.State.GONE);
  }

  private void updateGooseMan(float frame) {
    if (gooseMan == null) {
      if (spawner != null && geeseFed >= GameConfig.GOOSE_MAN_AFTER_GEESE) {
        gooseMan = spawner.bringGooseMan(player.getBounds(), map, geese);
      }
      return;
    }
    gooseMan.update(frame,map);
  }

  // Throws and moves the bread, and feeds the geese; a goose that gets full lays an egg (FR_BREAD, FR_FULLNESS).
  private void updateBread(float frame) {
    Bread thrown = dispenser.update(frame, player.getBounds(), geese, bread);
    if (thrown != null) {
      bread.add(thrown);
    }
    for (Bread piece : bread) {
      Goose goose = piece.getTarget();
      if (piece.update(frame) && goose.feed()) {
        geeseFed++;
        Rectangle box = goose.getBounds();
        eggs.add(new Egg(box.x + box.width / 2, box.y + box.height / 2));
      }
    }
    bread.removeIf(Bread::isGone);
  }

  /**
   * Sets the direction the player is asking the student to move in (FR_MOVE). The input handler calls this every
   * frame. Slice 2's student and map model (#4) moves the student by it in {@link #update(float)}.
   *
   * @param x -1 for left, 1 for right, 0 for neither
   * @param y -1 for down, 1 for up, 0 for neither
   */
  public void setMoveInput(int x, int y) {
    moveX = x;
    moveY = y;
  }

  public void useItem(Items item) {
    if (inventory.useItem(item)) {
      switch (item) {
        case COFFEE:
          // Give a 2x speed boost, but only if there is no boost currently active
          if (player.getBoostSecondsLeft() == 0) {
            player.boostSpeed(2f, 10f);
          } else {
            // If there is already a boost, return the unused coffee to the inventory
            inventory.addItem(Items.COFFEE, 1);
          }
          break;
        case BREAD:
          // Increase dispense rate by 2x for 10 seconds, but only if there is no boost currently active
          if (dispenser.getDispenseBoostSecondsRemaining() == 0) {
            dispenser.dispenseBoost(2f, 10f);
          } else {
            // If there is already a boost, return the unused bread to the inventory
            inventory.addItem(Items.BREAD, 1);
          }
          break;
      }
    }
  }

  /**
   * Returns the left-right part of the move input.
   *
   * @return -1 for left, 1 for right, 0 for neither
   */
  public int getMoveX() {
    return moveX;
  }

  /**
   * Returns the up-down part of the move input.
   *
   * @return -1 for down, 1 for up, 0 for neither
   */
  public int getMoveY() {
    return moveY;
  }

  /**
   * Returns the state the run is in.
   *
   * @return READY, PLAYING, PAUSED or ENDED
   */
  public RunState getState() {
    return state;
  }

  /**
   * Returns the time left in the run, for the HUD (FR_TIMER).
   *
   * @return the time left in seconds, from 300 down to 0
   */
  public double getTimeLeft() {
    return clock.getTimeLeft();
  }

  /**
   * Returns the student, for drawing and for tests.
   *
   * @return the student
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns the campus this run is played on.
   *
   * @return the map
   */
  public CampusMap getMap() {
    return map;
  }

  public List<Goose> getGeese() {
    return Collections.unmodifiableList(geese);
  }

  public List<Bread> getBread() {
    return Collections.unmodifiableList(bread);
  }

  public List<Egg> getEggs() {
    return Collections.unmodifiableList(eggs);
  }

  public GooseMan getGooseMan(){
    return gooseMan;
  }

  public int getGeeseFed() {
    return geeseFed;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public BreadDispenser getDispenser() {
    return dispenser;
  }
}
