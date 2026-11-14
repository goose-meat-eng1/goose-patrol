package com.goosepatrol.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
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
  private final ShapeRenderer shapeRenderer = new ShapeRenderer();

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
    // Display generic message based on current state
    font.draw(batch, messageFor(session.getState()), 0, height / 2 - MARGIN/2, width, Align.center, false);

    // Only draw the rest of the HUD after the run has started
    if (session.getState() != RunState.READY) {
      font.draw(batch, "Time left " + formatTime(session.getTimeLeft()), MARGIN, height - MARGIN);

      if (session.getHudMessageTime() > 0) {
        font.draw(batch, session.getHudMessage(), 0, height / 2 + MARGIN*10, width, Align.center, false);
      }

      font.draw(batch, "Geese fed: " + session.getGeeseFed(), MARGIN, height - MARGIN * 3);
      font.draw(batch, "Items used: " + session.getItemsUsed(), MARGIN, height - MARGIN * 4);
      font.draw(batch, "Events triggered: " + session.getEventsTriggered(), MARGIN, height - MARGIN * 5);

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
  }

  /**
   * Draw a translucent rectangle behind the HUD during certain states to dim the background
   */
  public void drawBackground(Matrix4 projectionMatrix, float width, float height) {
    switch (session.getState()) {
      case READY, PAUSED -> {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(projectionMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.3f);
        shapeRenderer.rect(0, 0, width, height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
      }
      default -> {
      }
    }
  }

  public void dispose() {
    inventoryTexture.dispose();
    coffeeItemTexture.dispose();
    breadItemTexture.dispose();
    shapeRenderer.dispose();
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
      case READY -> "Move with WASD or the arrow keys.\nUse items by pressing the number keys (e.g. 1, 2)\nP pauses the game.\nPress SPACE to start.";
      case PAUSED -> "Paused. Press P to carry on.";
      default -> "";
    };
  }
}
