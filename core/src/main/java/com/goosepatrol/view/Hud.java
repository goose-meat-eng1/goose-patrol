package com.goosepatrol.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.Items;
import com.goosepatrol.model.RunState;
import java.util.Locale;

/**
 * The overlay, the time left, and a message line for the start prompt and the paused
 * message.
 */
public class Hud {

  private static final float MARGIN = 24;
  private static final int INVENTORY_SCALE = 3;

  private final GameSession session;
  private final BitmapFont font;

  private final Texture inventoryTexture = new Texture("items/Inventory.png");
  private final int inventoryWidth = inventoryTexture.getWidth() * INVENTORY_SCALE;
  private final int inventoryHeight = inventoryTexture.getHeight() * INVENTORY_SCALE;

  private final Texture coffeeItemTexture = new Texture("items/Coffee.png");
  private final Texture breadItemTexture = new Texture("items/Bread.png");
  private final int itemSize = 16 * INVENTORY_SCALE;
  private final int itemGap = itemSize + INVENTORY_SCALE;

  /**
   * Makes the HUD for one run.
   */
  public Hud(GameSession session, BitmapFont font) {
    this.session = session;
    this.font = font;
  }

  /**
   * Draws the HUD. Call draw between batch.begin() and  batch.end().
   */
  public void draw(SpriteBatch batch, float width, float height) {
    // Draw remaining time and the start/pause message
    font.draw(batch, "Time left " + formatTime(session.getTimeLeft()), MARGIN, height - MARGIN);
    font.draw(batch, messageFor(session.getState()), 0, height / 2, width, Align.center, false);

    // Draw the inventory
    float inventoryX = width/2 - inventoryWidth/2;
    float inventoryY = height - inventoryHeight/2 - MARGIN*1.5f;

    batch.draw(inventoryTexture, inventoryX, inventoryY, inventoryWidth, inventoryHeight);

    if (session.getInventory().getItemCount(Items.COFFEE) > 0) {
      batch.draw(coffeeItemTexture, inventoryX + INVENTORY_SCALE, inventoryY + INVENTORY_SCALE, itemSize, itemSize);

      float boostTime = session.getPlayer().getBoostSecondsLeft();
      if (boostTime == 0)
        font.draw(batch, "1 (x" + session.getInventory().getItemCount(Items.COFFEE) + ")", inventoryX + INVENTORY_SCALE, inventoryY - INVENTORY_SCALE);
      else
        font.draw(batch, formatTime(boostTime), inventoryX + INVENTORY_SCALE, inventoryY - INVENTORY_SCALE);
    }

    if (session.getInventory().getItemCount(Items.BREAD) > 0) {
      batch.draw(breadItemTexture, inventoryX + INVENTORY_SCALE + itemGap, inventoryY + INVENTORY_SCALE, itemSize, itemSize);

      float boostTime = session.getDispenser().getDispenseBoostSecondsRemaining();
      if (boostTime == 0)
        font.draw(batch, "2 (x" + session.getInventory().getItemCount(Items.BREAD) + ")", inventoryX + INVENTORY_SCALE + itemGap, inventoryY - INVENTORY_SCALE);
      else
        font.draw(batch, formatTime(boostTime), inventoryX + INVENTORY_SCALE + itemGap, inventoryY - INVENTORY_SCALE);
    }
  }

  public void dispose() {
    inventoryTexture.dispose();
    coffeeItemTexture.dispose();
    breadItemTexture.dispose();
  }

  /**
   * Formats seconds as m:ss.
   */
  static String formatTime(double seconds) {
    int whole = (int) Math.ceil(seconds);
    return String.format(Locale.ROOT, "%d:%02d", whole / 60, whole % 60);
  }

  /**
   * Returns the start prompt or the paused message for a state (FR_PAUSE)..
  
   */
  static String messageFor(RunState state) {
    return switch (state) {
      case READY -> "Press SPACE to start. Move with WASD or the arrow keys. P pauses the game.";
      case PAUSED -> "Paused. Press P to carry on.";
      default -> "";
    };
  }
}
