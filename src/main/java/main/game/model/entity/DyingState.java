package main.game.model.entity;

import main.common.images.UnitSpriteSheet.Sequence;
import main.game.model.world.World;

/**
 * Used for representing the dying animation when a unit is dead.
 * @author chongdyla
 */
public class DyingState extends UnitState {

  private static final long serialVersionUID = 1L;

  public DyingState(Sequence sequence, Unit unit) {
    super(sequence, unit);
  }

  @Override
  public void tick(Long timeSinceLastTick, World world) {
    super.tick(timeSinceLastTick, world);

    if (imagesComponent.isLastTick()) {
      world.onEnemyKilled(unit);
    }
  }

  @Override
  UnitState updateState() {
    return this;
  }
}
