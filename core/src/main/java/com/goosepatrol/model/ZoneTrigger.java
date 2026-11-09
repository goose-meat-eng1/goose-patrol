package com.goosepatrol.model;

import com.badlogic.gdx.math.Rectangle;

/**
 * ZoneTrigger is the One area  that will trigger an event and where the event will spawn
 */
public class ZoneTrigger {
  private final Rectangle area;
  private final float spawnX, spawnY;
  private boolean triggered = false;

  public ZoneTrigger(float x, float y, float width, float height, float spawnX, float spawnY){
    this.area = new Rectangle(x, y, width, height);
    this.spawnX = spawnX;
    this.spawnY = spawnY;
  }

  public boolean isMet(Rectangle playerBox){
    if (!triggered && area.overlaps(playerBox)){
      triggered = true;
      return true;
    }
    return false;
  }

  public float getSpawnX(){
    return spawnX;
  }

  public float getSpawnY(){
    return spawnY;
  }
}
