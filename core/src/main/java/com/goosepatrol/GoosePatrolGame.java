package com.goosepatrol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.goosepatrol.input.InputHandler;
import com.goosepatrol.model.CampusMap;
import com.goosepatrol.model.GameSession;
import com.goosepatrol.model.GameSetup;
import com.goosepatrol.view.CampusMapLoader;
import com.goosepatrol.view.EndScreen;
import com.goosepatrol.view.GameScreen;

/**
 * The libGDX Game, shared by all platforms. 
 * It loads the campus map, builds each run with GameSetup, 
 * makes the screens and the key handler, and switches screens.
 */
public class GoosePatrolGame extends Game {

  /** The campus map, made in Tiled (FR_MAP). */
  public static final String MAP_FILE = "map/campus-east.tmx";

  // One batch and one font, shared by every screen and freed in dispose().
  private SpriteBatch batch;
  private BitmapFont font;

  // The map as Tiled made it, kept for drawing, and the same map as the model sees it.
  private TiledMap tiledMap;
  private CampusMap campusMap;

  /** Makes the game. The set-up happens in create(), which libGDX calls once the window is ready. */
  public GoosePatrolGame() {
  }

  @Override
  public void create() {
    batch = new SpriteBatch();
    font = new BitmapFont();
    tiledMap = new TmxMapLoader().load(MAP_FILE);
    campusMap = CampusMapLoader.fromTiledMap(tiledMap);
    startRun();
  }

  /** Builds a new run and shows it, waiting for the start key (FR_PAUSE). */
  private void startRun() {
    GameSession session = GameSetup.newSession(campusMap);
    InputHandler keys = new InputHandler(session, Gdx.input::isKeyPressed);
    Gdx.input.setInputProcessor(keys);
    setScreen(new GameScreen(session, keys::readMovement, tiledMap, batch, font, this::showEndScreen));
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
    tiledMap.dispose();
    batch.dispose();
    font.dispose();
  }
}
