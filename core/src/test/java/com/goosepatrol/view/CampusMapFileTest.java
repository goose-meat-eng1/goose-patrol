package com.goosepatrol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Checks the real map file as plain XML, so no window is needed.
class CampusMapFileTest {

  private static final File MAP_FILE = new File("../assets/map/campus-east.tmx");

  private static Element map;

  @BeforeAll
  static void readTheFile() throws Exception {
    map = read(MAP_FILE);
  }

  private static Element read(File file) throws Exception {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    return document.getDocumentElement();
  }

  @Test
  @Tag("FR_MAP")
  void isAFiniteMap() {
    assertEquals("0", map.getAttribute("infinite")); // libGDX can't load infinite maps
  }
}
