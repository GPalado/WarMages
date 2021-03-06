package main.game.model.world.saveandload;

import main.game.model.entity.Entity;
import main.game.model.world.World;

/**
 * A Factory and Facade.
 * Creates a new [@link {@link World} and it's required {@link Entity} objects in the default
 * positions in the {@link World}.
 * <p>
 * NOTE: We decided not to load the world from a file for now
 * because that does not provide any improvements to the game or requirements of the game.
 * </p>
 * @author chongdyla
 */
public interface WorldLoader {

  /**
   * Creates the default {@link World}.
   */
  World load();

}
