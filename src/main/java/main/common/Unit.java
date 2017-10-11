package main.common;

import java.util.List;
import main.common.images.GameImage;
import main.common.images.UnitSpriteSheet;
import main.common.util.MapPoint;
import main.game.model.entity.DeadUnit;
import main.game.model.entity.Direction;
import main.game.model.entity.Team;
import main.game.model.entity.UnitState;
import main.game.model.entity.UnitType;
import main.game.model.world.World;

/**
 * Unit extends{@link Entity}. A unit is a part of a team, specified by an enum colour. It has
 * health, and can attack other team units.
 */
public interface Unit extends Entity {

  /**
   * Applies the given damage to the Unit. Requires the amount given is a positive integer.
   */
  void takeDamage(int amount, World world);

  /**
   * Gives the Unit the given amount of health. Requires the amount given is a positive
   * integer.
   */
  void gainHealth(int amount);

  int getHealth();

  void setNextState(UnitState state);

  UnitType getUnitType();

  double getLineOfSight();

  DeadUnit createDeadUnit();

  void tick(long timeSinceLastTick, World world);

  void tickPosition(long timeSinceLastTick, World world);

  GameImage getImage();

  void translatePosition(double dx, double dy);

  Team getTeam();

  void addEffect(Effect effect);

  void tickEffects(long timeSinceLastTick);

  UnitSpriteSheet getSpriteSheet();

  Direction getCurrentDirection();

  /**
   * Attacks the current target.
   */
  void attack();

  /**
   * Set's the Unit's target to the given Unit.
   *
   * @param target to be attacked
   */
  void setTarget(Unit target, World world);

  /**
   * Clears the current target.
   */
  void clearTarget();

  /**
   * Sets the damage amount of this Unit's attack to the given amount. Must be 0 < amount < 100.
   *
   * @param amount of damage to deal to target.
   */
  void setDamageAmount(int amount);

  /**
   * Returns the amount of damage dealt.
   *
   * @return int amount of damage dealt.
   */
  int getDamageAmount();

  /**
   * Returns boolean whether the distance between the target and the Unit is less than the
   * leeway.
   *
   * @return boolean representing distance less than leeway.
   */
  boolean targetWithinProximity();

  /**
   * Sets the Unit's path to the given path.
   *
   * @param path list of MapPoints
   */
  void setPath(List<MapPoint> path);
}

