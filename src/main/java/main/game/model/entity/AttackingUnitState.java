package main.game.model.entity;

import main.common.images.UnitSpriteSheet;
import main.game.model.world.World;

public class AttackingUnitState extends UnitState {

  private static final long serialVersionUID = 1L;

  public AttackingUnitState(Direction direction, Unit unit) {
    super(unit.getUnitType().getAttackSequence(), direction, unit);
  }

  @Override
  public void tick(Long timeSinceLastTick, World world) {
    super.tick(timeSinceLastTick, world);

    if (imagesComponent.isOnAttackTick()) {
      onAttackFrame(world);
    }
  }

  @Override
  UnitState updateState() {
    if (!imagesComponent.isReadyToTransition() || nextState == null) {
      return this;
    }

    return nextState;
  }

  /**
   * Called when the attack frame is reached and the animation
   * {@link UnitSpriteSheet.Sequence}.
   */
  private void onAttackFrame(World world) {
    UnitType unitType = unit.getUnitType();
    Unit target = unit.getTarget();

    if (unitType.canShootProjectiles()) {
      Projectile projectile = unitType.createProjectile(unit, target);
      world.addProjectile(projectile);
    } else {
      // Non projectile attack (e.g. spear)
      target.takeDamage(unit.getDamageAmount(), world);
    }
  }
}
