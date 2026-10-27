package com.goosepatrol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.goosepatrol.input.InputHandler;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.GameSetup;
import com.goosepatrol.view.EndScreen;
import com.goosepatrol.view.GameScreen;

/**
 * The libGDX {@link Game}, shared by all platforms (D1). It builds each run with {@link GameSetup}, makes the
 * screens and the key handler, and switches screens.
 */
public class GoosePatrolGame extends Game {

  // One batch and one font, shared by every screen and freed in dispose().
  private SpriteBatch batch;
  private BitmapFont font;

  /** Makes the game. The set-up happens in {@link #create()}, which libGDX calls once the window is ready. */
  public GoosePatrolGame() {
  }

  @Override
  public void create() {
    batch = new SpriteBatch();
    // font: Liberation Sans at 15 px
    // font made at a readable size (UR_ACCESS).
    font = new BitmapFont();
    startRun();
  }

  /** Builds a new run and shows it, waiting for the start key (FR_PAUSE). */
  private void startRun() {
    GameSession session = GameSetup.newSession();
    InputHandler keys = new InputHandler(session, Gdx.input::isKeyPressed);
    Gdx.input.setInputProcessor(keys);
    setScreen(new GameScreen(session, keys::readMovement, batch, font, this::showEndScreen));
  }

  /** Swaps the play screen for the end screen once the run has ended (FR_SCREENS). */
  private void showEndScreen() {
    Screen playScreen = getScreen();
    setScreen(new EndScreen(batch, font));
    playScreen.dispose(); // setScreen() only hides the old screen, so free it here
  }

  @Override
  public void dispose() {
    super.dispose(); // hides the current screen
    if (getScreen() != null) {
      getScreen().dispose();
    }
    batch.dispose();
    font.dispose();
  }
}
