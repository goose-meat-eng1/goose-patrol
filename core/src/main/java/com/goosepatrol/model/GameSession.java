package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One run of the game, from the start prompt to the end screen.
 */
public class GameSession {

  private final GameClock clock;
  private final CampusMap map;
  private final Player player;

  private final Inventory inventory = new Inventory(3, 3);

  /** Null until enough geese have been fed (FR_BOSS). */
  private GooseMan gooseMan;
  private Boolean gooseManDefeated = false;

  /** Brings the geese in waves (FR_SPAWN) null means no geese, as in most tests. */
  private final GooseSpawner spawner;

  private final BreadDispenser dispenser = new BreadDispenser();
  private final List<Goose> geese = new ArrayList<>();
  private final List<Bread> bread = new ArrayList<>();
  private final List<Egg> eggs = new ArrayList<>();
  private final List<HiddenEvent> hiddenEvents;

  // Every run opens waiting for the start key (FR_PAUSE).
  private RunState state = RunState.READY;
  private EndCondition endCondition;

  // The held movement keys, each -1, 0 or 1, set every frame by the input handler (FR_MOVE).
  private int moveX;
  private int moveY;

  /** How many geese have been filled this run. */
  private int geeseFed;
  private int itemsUsed;
  private int eventsTriggered;

  private String announcementMessage;
  private double announcementTime;

  /** Makes a run with no geese that waits for the start key, for tests. */
  public GameSession(GameClock clock, CampusMap map, Player player) {
    this(clock, map, player, null);
  }

  /** Makes a run that waits for the start key. The game builds runs with GameSetup#newSession(CampusMap). */
  public GameSession(GameClock clock, CampusMap map, Player player, GooseSpawner spawner) {
    this.clock = clock;
    this.map = map;
    this.player = player;
    this.spawner = spawner;
    this.hiddenEvents = HiddenEvent.fromMap(map);
  }

  /**
   * Starts the run READY becomes PLAYING (FR_PAUSE). In any other state it does nothing.
   */
  public void start() {
    if (state == RunState.READY) {
      state = RunState.PLAYING;
    }
  }

  /**
   * Pauses or resumes the run PLAYING becomes PAUSED, and PAUSED becomes PLAYING (FR_PAUSE). 
   */
  public void togglePause() {
    if (state == RunState.PLAYING) {
      state = RunState.PAUSED;
    } else if (state == RunState.PAUSED) {
      state = RunState.PLAYING;
    }
  }

  /**
   * Moves the run on by one frame. Only a PLAYING run changes the clock counts down, the run ends when the
   * time is up (FR_TIMER), and otherwise the student, the geese and the bread move (FR_MOVE, FR_SPAWN, FR_BREAD).
   */
  public void update(float delta) {
    if (state != RunState.PLAYING) {
      return; 
    }
    float frame = Math.min(delta, GameConfig.MAX_FRAME_SECONDS);
    clock.tick(frame);

    if (announcementTime > 0) {
      announcementTime -= frame;
    }
    
    if (clock.isUp()) {
      endGame(EndCondition.TIMER);
      return;
    }
    player.update(moveX, moveY, frame, map);
    updateHiddenEvents();
    updateGeese(frame);
    updateGooseMan(frame);
    updateBread(frame);
  }

  // Gives the item and story of a hidden event the student has just walked into and counts it (FR_EVENT_HIDDEN,
  // FR_EVENT_EFFECT, FR_EVENT_COUNT).
  private void updateHiddenEvents() {
    Rectangle student = player.getBounds();
    for (HiddenEvent event : hiddenEvents) {
      if (event.check(student)) {
        inventory.addItem(event.getReward(), 1);
        setHudMessage(event.getStory(), 5);
        eventsTriggered++;
      }
    }
  }

  // Brings in a wave when one is due, moves the geese, and drops the ones that have flown off (FR_SPAWN).
  private void updateGeese(float frame) {
    Rectangle student = player.getBounds();
    if (spawner != null && !gooseManDefeated) {
      geese.addAll(spawner.update(frame, student, map, geese));
    }
    Vector2 target = student.getCenter(new Vector2());
    for (Goose goose : geese) {
      if (gooseManDefeated)
        goose.feedMax();

      goose.update(target, geese, frame, map);

      // Attack the player if the goose is in range
      if (goose.getState() == Goose.State.CHASING) {
        Rectangle gooseBox = goose.getBounds();
        Vector2 gooseCenter = gooseBox.getCenter(new Vector2());

        if (gooseCenter.dst(target) < GameConfig.GOOSE_ATTACK_RANGE) {
          float remainingStamina = player.reduceStamina(GameConfig.GOOSE_ATTACK_POWER);
          if (remainingStamina == 0) {
            endGame(EndCondition.STAMINA);
            return;
          }

          goose.startCooldown();
        }
      }
    }
    geese.removeIf(goose -> goose.getState() == Goose.State.GONE);
  }

  private void updateGooseMan(float frame) {
    if (gooseMan == null) {
      if (spawner != null && geeseFed >= GameConfig.GOOSE_MAN_AFTER_GEESE) {
        gooseMan = spawner.bringGooseMan(player.getBounds(), map, geese);
        eventsTriggered++;
      }
      return;
    }
    
    gooseMan.update(frame,map);

    if (gooseMan.getState() == GooseMan.State.DEFEATED) {
      gooseManDefeated = true;
      endGame(EndCondition.VICTORY); // TODO: don't end immediately, add a delay or an escape sequence (collect golden egg?)
    }
  }

  // Throws and moves the bread, and feeds the geese; a goose that gets full lays an egg (FR_BREAD, FR_FULLNESS).
  private void updateBread(float frame) {
    Bread thrown = dispenser.update(frame, player.getBounds(), geese, gooseMan, bread);
    if (thrown != null) {
      bread.add(thrown);
    }
    for (Bread piece : bread) {
      Feedable target = piece.getTarget();
      // A full Goose Man is defeated (FR_BOSS), but he isn't a goose fed and lays no egg.
      if (piece.update(frame) && target.feed() && target != gooseMan) {
        geeseFed++;
        Rectangle box = target.getBounds();

        eggs.add(new Egg(box.x + box.width / 2, box.y + box.height / 2));
      }
    }
    bread.removeIf(Bread::isGone);
  }

  /**
   * Sets the direction the player is asking the student to move in (FR_MOVE). The input handler calls this every
   * frame. 
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
            setHudMessage("Drank coffee!\n2x movement speed for 10 seconds!", 5);
            itemsUsed++;
          } else {
            // If there is already a boost, return the unused coffee to the inventory
            inventory.addItem(Items.COFFEE, 1);
          }
          break;
        case BREAD:
          // Increase dispense rate by 2x for 10 seconds, but only if there is no boost currently active
          if (dispenser.getDispenseBoostSecondsRemaining() == 0) {
            dispenser.dispenseBoost(2f, 10f);
            setHudMessage("Bread dispenser boost!\n2x dispense rate for 10 seconds!", 5);
            itemsUsed++;
          } else {
            // If there is already a boost, return the unused bread to the inventory
            inventory.addItem(Items.BREAD, 1);
          }
          break;
      }
    }
  }

  private void endGame(EndCondition endCondition) {
    this.endCondition = endCondition;
    state = RunState.ENDED;
  }
  
  public void setHudMessage(String message, double displayTime) {
    announcementMessage = message;
    announcementTime = displayTime;
  }

  public String getHudMessage() {
    return announcementMessage;
  }
  
  public double getHudMessageTime() {
    return announcementTime;
  }

  public int getMoveX() {
    return moveX;
  }

 
  public int getMoveY() {
    return moveY;
  }


  public RunState getState() {
    return state;
  }


  public double getTimeLeft() {
    return clock.getTimeLeft();
  }


  public Player getPlayer() {
    return player;
  }


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

  public int getItemsUsed() {
    return itemsUsed;
  }

  public int getEventsTriggered() {
    return eventsTriggered;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public BreadDispenser getDispenser() {
    return dispenser;
  }

  public EndCondition getEndCondition() {
    return endCondition;
  }
}
