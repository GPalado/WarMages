package main.game.model.world;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import main.game.model.GameModel;
import main.game.model.Level;
import main.game.model.entity.Entity;
import main.game.model.entity.HeroUnit;
import main.game.model.entity.Item;
import main.game.model.entity.MapEntity;
import main.game.model.entity.Unit;
import main.util.Event;
import main.util.MapPoint;
import main.util.MapRect;

/**
 * World class is a representation of all the in-play entities and in-play entities: all entity
 * objects that have been instantiated.
 */
public class World {
  private final List<Level> levels;
  private int levelIndex = 0;
  private final Collection<MapEntity> mapEntities;

  private final HeroUnit heroUnit;

  private final Collection<Unit> units;
  private final Collection<Item> items;
  private final Collection<MapRect> bounds = new HashSet<>();

  /**
   * Creates the world.
   *
   * @param levels The levels sorted from start to finish. The first level in this list is the
   *     initial level.
   * @param heroUnit The hero unit used throughout the whole game.
   */
  public World(List<Level> levels, HeroUnit heroUnit) {
    this.levels = levels;
    this.heroUnit = heroUnit;
    this.units = levels.get(levelIndex).getUnits();
    this.items = levels.get(levelIndex).getItems();
//    bounds.add(levels.get(levelIndex).getMapBounds());
    mapEntities = levels.get(levelIndex).getMapEntities();
  }

  /**
   * Converts a mapEntity collection into a map of MapPoints to Entities.
   * @param mapEntities collection of MapEntities
   * @return returns converted map
   */
  private Map<MapPoint, Entity> convertMapEntitiesToMap(Collection<MapEntity> mapEntities) {
    return mapEntities.stream()
        .collect(Collectors.toMap(
            Entity::getPosition,
            e -> e
        ));
  }

  /**
   * A getter method which returns all the entities in the world thats within the selection.
   * The collection to be return must be ordered.
   *
   * @param rect a selection box.
   * @return A collection of Entities within the given selection rect.
   */
  public Collection<Entity> findEntities(MapRect rect) {
    return mapEntities.stream()
        .filter(e -> rect.contains(e.getPosition()))
        .collect(Collectors.toList());
  }

  /**
   * A getter method which checks if a certain point in the map can be moved into.
   * TODO - make sure that the method returns false for points outside the Map
   *
   * @param point a point in the map.
   * @return returns whether the point can be moved into.
   */
  public boolean isPassable(MapPoint point) {
    for (MapEntity mapEntity : mapEntities){
      if (mapEntity.getPosition().equals(point))
        return false;
    }
    boolean isPassable = false;
    for (MapRect rect : bounds){
      isPassable |= rect.contains(point);
    }
    return isPassable;
  }

  /**
   * A method specific for progression of game. Triggers are specific quests/goals to be achieved
   * for progression.
   */
  public void checkCompletion() {
    if (levels.get(levelIndex).areGoalsCompleted(this)){
      levelIndex = (levelIndex >= levels.size()) ? levels.size()-1 : levelIndex + 1;
    }
  }

  /**
   * A method to change all the current positions/animations of all entities in the world.
   */
  public void tick(long timeSinceLastTick) {
    for (Unit unit : units){
      throw new Error("tick not implemented");
      //UNIT CANT BE CHANGED ATM.
    }
  }

}
