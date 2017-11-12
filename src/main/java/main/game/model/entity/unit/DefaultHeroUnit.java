package main.game.model.entity.unit;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import main.common.World;
import main.common.entity.HeroUnit;
import main.common.entity.Team;
import main.common.entity.usable.Ability;
import main.common.entity.usable.Item;
import main.common.exceptions.ItemNotInRangeException;
import main.common.images.UnitSpriteSheet;
import main.common.util.MapPoint;
import main.common.util.MapSize;
import main.game.model.entity.unit.state.TargetItem;

/**
 * Default implementation of HeroUnit.
 * @author paladogabr
 */
public class DefaultHeroUnit extends DefaultUnit implements HeroUnit {

  private static final long serialVersionUID = 1L;

  private final List<Ability> abilities;

  private List<Item> itemInventory = new CopyOnWriteArrayList<>();

  /**
   * Constructor takes initial position of HeroUnit, size, sprite sheet, and unit type.
   *
   * @param position of HeroUnit.
   * @param size of HeroUnit on Map.
   * @param sheet SpriteSheet of HeroUnit images.
   * @param type of HeroUnit.
   */
  public DefaultHeroUnit(
      MapPoint position,
      MapSize size,
      UnitSpriteSheet sheet,
      UnitType type,
      Collection<Ability> abilities,
      int level
  ) {
    super(position, size, Team.PLAYER, sheet, type, level);
    this.abilities = new CopyOnWriteArrayList<>(abilities);
  }

  /**
   * Adds the given item to the HeroUnit's itemInventory. Requires the item is not null.
   */
  @Override
  public void pickUp(Item item) {
    requireNonNull(item);

    if (!isItemWithinRange(item)) {
      throw new ItemNotInRangeException("Item is too far away");
    }

    if (itemInventory.contains(item)) {
      return;
    }

    itemInventory.add(item);
  }

  public boolean isItemWithinRange(Item item) {
    return new TargetItem(this, item).hasArrived();
  }

  /**
   * Returns the HeroUnit's abilities.
   */
  @Override
  public Collection<Ability> getAbilities() {
    return Collections.unmodifiableList(abilities);
  }

  /**
   * Returns the HeroUnit's itemInventory.
   */
  @Override
  public Collection<Item> getItemInventory() {
    return Collections.unmodifiableList(itemInventory);
  }

  @Override
  public void tick(long timeSinceLastTick, World world) {
    super.tick(timeSinceLastTick, world);

    abilities.forEach(ability -> ability.usableTick(timeSinceLastTick));
    itemInventory.forEach(item -> item.usableTick(timeSinceLastTick));
  }
}
