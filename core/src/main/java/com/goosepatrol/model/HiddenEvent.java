package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * An invisible zone on the map that gives the student an item and a story message the first time they walk into it
 * (FR_EVENT_HIDDEN). Each one fires once per run.
 */
public class HiddenEvent {

  /** The name of the free coffee zone in the map's events layer. */
  public static final String FREE_COFFEE_ZONE = "free-coffee";

  /** The name of the fire drill zone in the map's events layer. */
  public static final String FIRE_DRILL_ZONE = "fire-drill";

  private final ZoneTrigger trigger;
  private final Items reward;
  private final String story;

  public HiddenEvent(ZoneTrigger trigger, Items reward, String story) {
    this.trigger = trigger;
    this.reward = reward;
    this.story = story;
  }

  /** Makes the run's hidden events from the zones the map names. A zone the map leaves out has no event. */
  public static List<HiddenEvent> fromMap(CampusMap map) {
    List<HiddenEvent> events = new ArrayList<>();
    addIfOnMap(events, map.getEventZone(FREE_COFFEE_ZONE), Items.COFFEE,
        "Free coffee stand!\n+1 coffee");
    addIfOnMap(events, map.getEventZone(FIRE_DRILL_ZONE), Items.BREAD,
        "Fire drill! Someone left their lunch behind.\n+1 bread");
    return events;
  }

  private static void addIfOnMap(List<HiddenEvent> events, Rectangle zone, Items reward, String story) {
    if (zone != null) {
      ZoneTrigger trigger = new ZoneTrigger(zone.x, zone.y, zone.width, zone.height,
          zone.x + zone.width / 2, zone.y + zone.height / 2);
      events.add(new HiddenEvent(trigger, reward, story));
    }
  }

  /** Says whether the student has just walked into the zone, which is true only the first time. */
  public boolean check(Rectangle studentBox) {
    return trigger.isMet(studentBox);
  }

  public Items getReward() {
    return reward;
  }

  public String getStory() {
    return story;
  }
}
