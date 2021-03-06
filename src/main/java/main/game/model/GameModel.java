package main.game.model;

import java.util.Collection;
import main.game.model.entity.Entity;
import main.game.model.entity.HeroUnit;
import main.game.model.entity.Unit;
import main.game.model.world.World;

/**
 * Contains the main game loop, and controls the the progression of the story/game through the use
 * of levels.
 *
 * @author Eric Diputado
 */
public interface GameModel {

  /**
   * Milliseconds between ticks.
   */
  long DELAY = 50;

  /**
   * A getter method to get all possible entities.
   *
   * @return a collection of all possible entities
   */
  Collection<Entity> getAllEntities();

  /**
   * Starts the main game loop of this app.
   */
  void startGame();

  /**
   * A setter method to select a collection.
   *
   * @param unitSelection Selection points on the world that may contain units
   */
  void setUnitSelection(Collection<Unit> unitSelection);

  /**
   * A getter method get a previously selected collection.
   *
   * @return a collection of selected entities
   */
  Collection<Unit> getUnitSelection();

  void addToUnitSelection(Unit unit);

  /**
   * A getter method to get all possible units.
   *
   * @return a collection of all possible units
   */
  Collection<Unit> getAllUnits();

  /**
   * Getter for the world.
   *
   * @return world
   */
  World getWorld();

  HeroUnit getHeroUnit();

  /**
   * Pauses the main game loop.
   */
  void pauseGame();

  /**
   * Resumes the main game loop.
   */
  void resumeGame();

  /**
   * Stops the main game loop.
   */
  void stopGame();
}
