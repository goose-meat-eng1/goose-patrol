package com.goosepatrol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.goosepatrol.model.RunState;
import org.junit.jupiter.api.Test;

class HudTest {

  @Test
  void formatsTheTimeLeftRoundingUp() {
    assertEquals("5:00", Hud.formatTime(300));
    assertEquals("5:00", Hud.formatTime(299.5));
    assertEquals("4:59", Hud.formatTime(299));
    assertEquals("1:05", Hud.formatTime(65));
    assertEquals("0:01", Hud.formatTime(0.2));
    assertEquals("0:00", Hud.formatTime(0));
  }

  @Test
  void promptsOnlyWhenNotPlaying() {
    assertTrue(Hud.messageFor(RunState.READY).contains("SPACE"));
    assertTrue(Hud.messageFor(RunState.PAUSED).contains("P"));
    assertEquals("", Hud.messageFor(RunState.PLAYING));
  }
}
