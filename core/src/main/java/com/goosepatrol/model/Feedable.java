package com.goosepatrol.model;


import com.badlogic.gdx.math.Rectangle;

/** Something the dispenser can throw bread at, a goose or the Goose Man (FR_BREAD, FR_BOSS). */
public interface Feedable {

  /** Whether it can still eat bread, so it is worth a throw (FR_FULLNESS). */
  boolean canEat();

  /** Feeds it one piece, and returns true if that filled it (FR_FULLNESS). */
  boolean feed();

  int getFullness();

  int getMaxFullness();

  Rectangle getBounds();
}