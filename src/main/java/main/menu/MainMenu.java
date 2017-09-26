package main.menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import main.game.model.saveandload.WorldLoader;
import main.game.model.saveandload.GameSaveModel;
import main.game.model.world.saveandload.WorldLoader;
import main.game.model.world.saveandload.WorldSaveModel;

/**
 * Wrapper for the MainMenu of the game. Includes play load and exit buttons
 *
 * @author Andrew McGhie
 */
public class MainMenu extends Menu {

  private final WorldLoader worldLoader;
  private final WorldSaveModel worldSaveModel;

  public MainMenu(WorldLoader worldLoader, WorldSaveModel worldSaveModel) {
    this.worldLoader = worldLoader;
    this.worldSaveModel = worldSaveModel;
  }

  @Override
  public String getHtml() {
    return this.fileToString("resources/html/main_menu.html");
  }

  @Override
  MenuController getMenuController() {
    return new MainMenuController(worldLoader, gameSaveModel);
  }
}
