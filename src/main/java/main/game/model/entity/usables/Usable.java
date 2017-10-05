package main.game.model.entity.usables;

import java.io.Serializable;
import java.util.Collection;
import main.game.model.entity.Unit;
import main.images.GameImage;
import main.util.TickTimer;

/**
 * An usable {@link Item} or {@link Ability} - these have some effect on the unit (e.g. instant
 * health increase or a damage increase for a certain amount of time).
 */
public interface Usable extends Serializable {

  double READY = 0;

  /**
   * Creates an {@link Effect} for each unit and applies it to each {@link Unit}.
   *
   * @throws IllegalStateException When this is not ready to be used yet (e.g. cool-down).
   */
  default void useOnUnits(Collection<Unit> units) {
    if (!isReadyToBeUsed()) {
      throw new IllegalStateException("Not ready");
    }

    for (Unit unit : units) {
      Effect effect = _createEffectForUnit(unit);
      unit.addEffect(effect);
    }

    _startCoolDown();
  }

  /**
   * False if currently in a cool-down state.
   */
  default boolean isReadyToBeUsed() {
    return getCoolDownProgress() == READY;
  }

  /**
   * Should update any cool-down timers. This is not called 'tick' because there is already
   * a method called 'tick' in {@link main.game.model.entity.Entity}.
   */
  void usableTick(long timeSinceLastTick);

  /**
   * Returns the GameImage of this Ability.
   *
   * @return GameImage of the Ability.
   */
  GameImage getIconImage();

  /**
   * Returns a string description of the Ability.
   *
   * @return String describing the Ability
   */
  String getDescription();

  /**
   * 0 if just used, 1 if ready to use.
   */
  double getCoolDownProgress();

  /**
   * PROTECTED - DON"T USE FROM OUTSIDE THIS CLASS! Starts the cooldown period.
   */
  void _startCoolDown();

  /**
   * PROTECTED - DON"T USE FROM OUTSIDE THIS CLASS! Creates a new effect. Does not need to check
   * {@link Usable#isReadyToBeUsed()}.
   */
  Effect _createEffectForUnit(Unit unit);

  /**
   * Is created by the {@link Usable} to actually do the work. JavaDoc below tells which method
   * are useful to override.
   */
  abstract class Effect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Unit targetUnit;
    private final TickTimer expiryTimer;

    /**
     * Default constructor.
     * @param durationSeconds Number of seconds before this expires. Set to 0 for one-shot effects.
     */
    public Effect(Unit targetUnit, double durationSeconds) {
      this.targetUnit = targetUnit;
      this.expiryTimer = TickTimer.withPeriodInSeconds(durationSeconds);
    }

    /**
     * Optionally apply something to the unit (when the effect starts). Override and call super.
     */
    public void start() {
      expiryTimer.restart();
    }

    /**
     * Maybe does something to it's {@link Unit}, or maybe doesn't do anything. If expired, does
     * nothing. If it becomes expired, optionally do some cleanup on the {@link Unit}. If you
     * override make sure to call super.
     */
    public void tick(long timeSinceLastTick) {
      expiryTimer.tick(timeSinceLastTick);
    }

    public boolean isApplyingTo(Unit unit) {
      return unit == this.targetUnit;
    }

    public boolean isExpired() {
      return expiryTimer.isFinished();
    }

    // Methods that consistently affect properties of the Unit if not expired.
    // These methods can do nothing by not overriding.

    public int getDamageAmount(int currentDamageAmount) {
      return currentDamageAmount;
    }

  }
}
