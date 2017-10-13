package main.menu.controller;

import javafx.scene.image.ImageView;
import main.Main;
import main.common.util.Looper;
import main.common.GameController;
import main.game.controller.DefaultGameController;
import main.game.model.DefaultGameModel;
import main.game.model.world.World;
import main.common.WorldLoader;
import main.common.WorldSaveModel;
import main.game.view.GameView;
import main.game.view.events.MouseClick;
import main.images.DefaultImageProvider;
import main.common.images.ImageProvider;
import main.menu.Hud;
import main.menu.LoadMenu;
import main.menu.MainMenu;
import main.renderer.Renderer;
import main.common.util.Config;
import main.common.util.Event;
import main.common.util.Events.MainGameTick;

/**
 * Controller for the Main Menu. Responsible for making a new game loading a game and exiting.
 *
 * @author Andrew McGhie
 */
public class MainMenuController extends MenuController {

  private final WorldLoader worldLoader;
  private final WorldSaveModel worldSaveModel;
  private final MainMenu mainMenu;
  private final Main main;
  private final ImageView imageView;
  private final Config config;

  /**
   * inject the dependencies.
   */
  public MainMenuController(Main main,
                            MainMenu mainMenu,
                            WorldLoader worldLoader,
                            WorldSaveModel worldSaveModel,
                            ImageView imageView,
                            Config config) {
    this.main = main;
    this.mainMenu = mainMenu;
    this.worldLoader = worldLoader;
    this.worldSaveModel = worldSaveModel;
    this.imageView = imageView;
    this.config = config;
  }

  /**
   * Starts a new game from the beginning.
   */
  public void startBtn() {
    try {
      ImageProvider imageProvider = new DefaultImageProvider();
      MainGameTick tickEvent = new MainGameTick();
      Event<MouseClick> mouseClickEvent = new Event<>();
      World world = this.worldLoader.load();
      DefaultGameModel defaultGameModel = new DefaultGameModel(world, tickEvent);
      GameController gameController = new DefaultGameController(defaultGameModel);
      GameView gameView = new GameView(this.config,
          gameController,
          defaultGameModel,
          imageProvider,
          mouseClickEvent);
      tickEvent.registerListener(parameter -> gameView.onTick(parameter));
      mouseClickEvent.registerListener(parameter -> gameController.onMouseEvent(parameter));
      Renderer renderer = new Renderer(gameView, this.imageView, config, new Looper());
      Hud hud = new Hud(this.main,
          this.mainMenu,
          gameView,
          renderer,
          defaultGameModel,
          imageProvider);
      tickEvent.registerListener(parameter -> hud.updateIcons());
      tickEvent.registerListener(parameter -> hud.updateGoal(world.getCurrentGoalDescription()));
      tickEvent.registerListener(parameter -> world.tick(config.getGameModelDelay()));
      renderer.start();
      defaultGameModel.startGame();

      this.main.loadMenu(hud);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads the load menu.
   */
  public void loadBtn() {
    try {
      this.main.loadMenu(new LoadMenu(this.main, this.mainMenu, this.worldSaveModel));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Exits the program imediatly.
   */
  public void exitBtn() {
    System.exit(0);
  }
}
