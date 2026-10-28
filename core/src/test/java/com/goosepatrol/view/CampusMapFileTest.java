package com.goosepatrol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.goosepatrol.model.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Checks the real map file as plain XML, so no window is needed. Coordinates here are Tiled's: y grows downwards.
class CampusMapFileTest {

  private static final File MAP_FILE = new File("../assets/map/campus-east.tmx");

  // Tiled keeps flip and rotation flags in a tile number's top 4 bits; the rest is the tile.
  private static final long TILE_NUMBER_BITS = 0x0FFFFFFFL;

  private static Element map;

  @BeforeAll
  static void readTheFile() throws Exception {
    map = read(MAP_FILE);
  }

  private static Element read(File file) throws Exception {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    return document.getDocumentElement();
  }

  private static int number(Element element, String attribute) {
    return Math.round(Float.parseFloat(element.getAttribute(attribute)));
  }

  private static Element objectGroup(String name) {
    NodeList groups = map.getElementsByTagName("objectgroup");
    for (int i = 0; i < groups.getLength(); i++) {
      Element group = (Element) groups.item(i);
      if (name.equals(group.getAttribute("name"))) {
        return group;
      }
    }
    return null;
  }

  // The tile numbers ticked "solid" or "water", from tilesets in the map or in their own .tsx files.
  private static Set<Long> solidTileNumbers() throws Exception {
    Set<Long> solid = new HashSet<>();
    NodeList tilesets = map.getElementsByTagName("tileset");
    for (int i = 0; i < tilesets.getLength(); i++) {
      Element tileset = (Element) tilesets.item(i);
      long firstGid = Long.parseLong(tileset.getAttribute("firstgid"));
      String source = tileset.getAttribute("source");
      Element tiles = source.isEmpty() ? tileset : read(new File(MAP_FILE.getParentFile(), source));
      NodeList tileList = tiles.getElementsByTagName("tile");
      for (int j = 0; j < tileList.getLength(); j++) {
        Element tile = (Element) tileList.item(j);
        NodeList properties = tile.getElementsByTagName("property");
        for (int k = 0; k < properties.getLength(); k++) {
          Element property = (Element) properties.item(k);
          String name = property.getAttribute("name");
          boolean wall = CampusMapLoader.SOLID_PROPERTY.equals(name) || CampusMapLoader.WATER_PROPERTY.equals(name);
          if (wall && "true".equals(property.getAttribute("value"))) {
            solid.add(firstGid + Long.parseLong(tile.getAttribute("id")));
          }
        }
      }
    }
    return solid;
  }

  // Every wall in the file, as {x, y, width, height}: the Collisions rectangles, then each solid or water tile.
  private static List<int[]> walls() throws Exception {
    List<int[]> walls = new ArrayList<>();
    Element collisions = objectGroup(CampusMapLoader.COLLISIONS_LAYER);
    if (collisions != null) {
      NodeList objects = collisions.getElementsByTagName("object");
      for (int i = 0; i < objects.getLength(); i++) {
        Element o = (Element) objects.item(i);
        walls.add(new int[] {number(o, "x"), number(o, "y"), number(o, "width"), number(o, "height")});
      }
    }
    Set<Long> solid = solidTileNumbers();
    int columns = number(map, "width");
    int tileWidth = number(map, "tilewidth");
    int tileHeight = number(map, "tileheight");
    NodeList layers = map.getElementsByTagName("layer");
    for (int i = 0; i < layers.getLength(); i++) {
      Element data = (Element) ((Element) layers.item(i)).getElementsByTagName("data").item(0);
      assertEquals("csv", data.getAttribute("encoding"), "save the map's tile layers as CSV in Tiled");
      String[] cells = data.getTextContent().split(",");
      for (int cell = 0; cell < cells.length; cell++) {
        long tile = Long.parseLong(cells[cell].trim()) & TILE_NUMBER_BITS;
        if (solid.contains(tile)) {
          walls.add(new int[] {(cell % columns) * tileWidth, (cell / columns) * tileHeight, tileWidth, tileHeight});
        }
      }
    }
    return walls;
  }

  @Test
  @Tag("FR_MAP")
  void isAFiniteMap() {
    assertEquals("0", map.getAttribute("infinite")); // libGDX can't load infinite maps
  }

  @Test
  @Tag("FR_MAP")
  void hasWallsInsideTheMap() throws Exception {
    int width = number(map, "width") * number(map, "tilewidth");
    int height = number(map, "height") * number(map, "tileheight");
    List<int[]> walls = walls();

    assertFalse(walls.isEmpty());
    for (int[] w : walls) {
      assertTrue(w[0] >= 0 && w[1] >= 0 && w[0] + w[2] <= width && w[1] + w[3] <= height);
    }
  }

  @Test
  @Tag("FR_MAP")
  void studentStartsClearOfWalls() throws Exception {
    Element spawns = objectGroup(CampusMapLoader.SPAWNS_LAYER);
    assertNotNull(spawns);
    NodeList objects = spawns.getElementsByTagName("object");
    Element student = null;
    for (int i = 0; i < objects.getLength(); i++) {
      Element o = (Element) objects.item(i);
      if (CampusMapLoader.STUDENT_SPAWN.equals(o.getAttribute("name"))) {
        student = o;
      }
    }
    assertNotNull(student);
    float left = number(student, "x") - Player.WIDTH / 2; // the student's box, centred on the point
    float top = number(student, "y") - Player.HEIGHT / 2;
    float right = left + Player.WIDTH;
    float bottom = top + Player.HEIGHT;
    for (int[] w : walls()) {
      boolean overlaps = left < w[0] + w[2] && w[0] < right && top < w[1] + w[3] && w[1] < bottom;
      assertFalse(overlaps);
    }
  }
}
