package main.game.model.entity.unit;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import main.game.model.entity.DeadUnit;
import main.game.model.entity.DefaultEntity;
import main.game.model.entity.Direction;
import main.game.view.Renderable;
import main.game.model.entity.StaticEntity;
import main.game.model.entity.Team;
import main.game.model.entity.Unit;
import main.game.model.entity.unit.state.Dying;
import main.game.model.entity.unit.state.Idle;
import main.game.model.entity.unit.state.Target;
import main.game.model.entity.unit.state.Targetable;
import main.game.model.entity.unit.state.UnitState;
import main.game.model.entity.usable.Effect;
import main.game.model.world.World;
import main.game.view.ViewVisitor;
import main.images.GameImage;
import main.images.SpriteSheet;
import main.images.SpriteSheet.Sheet;
import main.images.UnitSpriteSheet;
import main.images.UnitSpriteSheet.Sequence;
import main.util.Config;
import main.util.Event;
import main.util.MapPoint;
import main.util.MapSize;

/**
 * Default Unit implementation.
 * @author paladogabr
 * @author chongdyla (Secondary author)
 */
public class DefaultUnit extends DefaultEntity implements Unit, Targetable {

  private static final long serialVersionUID = 1L;
  private static final double LEVEL_DIVISOR = 10;
  private static final double UNIT_MAX_SPEED = 0.12;
  private static final double UNIT_MAX_SIZE = 1;

  private final UnitSpriteSheet spriteSheet;
  private final Team team;

  private UnitType unitType;
  private UnitState unitState;
  private List<Effect> activeEffects = new ArrayList<>();
  private boolean isDead = false;

  private boolean hasCreatedDeadUnit = false;

  private int level;
  private double health;
  private double speed;
  private MapSize originalSize;

  private final Event<Double> damagedEvent = new Event<>();
  private final Event<Double> healedEvent = new Event<>();

  /**
   * Constructor takes the unit's position, size, and team.
   * Defaults the level to 0
   */
  public DefaultUnit(
      MapPoint position,
      MapSize size,
      Team team,
      UnitSpriteSheet sheet,
      UnitType unitType
  ) {
    this(position, size, team, sheet, unitType, 0);
  }

  /**
   * Constructor takes the unit's position, size, and team.
   */
  public DefaultUnit(
      MapPoint position,
      MapSize size,
      Team team,
      UnitSpriteSheet sheet,
      UnitType unitType,
      int level
  ) {
    super(position, size);
    this.level = level;
    this.originalSize = size;
    this.setSize(new MapSize(this.levelMultiplyer(size.width, UNIT_MAX_SIZE),
        this.levelMultiplyer(size.height, UNIT_MAX_SIZE)));
    this.team = team;
    this.unitType = unitType;
    this.health = this.levelMultiplyer(unitType.getStartingHealth());
    this.speed = unitType.getMovingSpeed();
    this.spriteSheet = sheet;
    this.unitState = new Idle(this);

  }

  /**
   * Buffs the base stats based on what level the unit is on.
   * @param val the value of the base stat
   */
  private double levelMultiplyer(double val) {
    return val * this.getLevelMultiplyer();
  }

  /**
   * Buffs the base stats based on what level  the unit is on.
   * Cannot go above max
   * @param val the value of the base stat
   * @param max the maximum that the stat can be
   */
  private double levelMultiplyer(double val, double max) {
    return Math.min(max, levelMultiplyer(val));
  }

  private double getLevelMultiplyer() {
    return (1 + ((double)this.level / LEVEL_DIVISOR));
  }

  /**
   * Sets the level and adjusts the size of the unit accordingly.
   */
  public void setLevel(int level) {
    this.level = level;
    this.setSize(new MapSize(this.levelMultiplyer(this.originalSize.width, UNIT_MAX_SIZE),
        this.levelMultiplyer(this.originalSize.height, UNIT_MAX_SIZE)));
  }

  /**
   * Sets the Unit's next state to be the given state.
   *
   * @param state to be changed to.
   */
  private void setNextState(UnitState state) {
    unitState.requestState(requireNonNull(state));
  }

  public UnitType getUnitType() {
    return unitType;
  }

  @Override
  public double getLineOfSight() {
    return this.unitType.lineOfSight;
  }

  @Override
  public DeadUnit createDeadUnit() {
    if (!isDead || hasCreatedDeadUnit) {
      throw new IllegalStateException();
    }

    hasCreatedDeadUnit = true;
    GameImage deadImage = spriteSheet.getImagesForSequence(Sequence.DEAD, Direction.DOWN).get(0);
    return new DefaultDeadUnit(getTopLeft(), getSize(), deadImage);
  }

  @Override
  public void tick(long timeSinceLastTick, World world) {
    //update image and state if applicable
    unitState.tick(timeSinceLastTick, world);
    unitState = requireNonNull(this.unitState.updateState());
    tickEffects(timeSinceLastTick);
  }

  @Override
  public GameImage getImage() {
    return unitState.getImage();
  }

  @Override
  public void translatePosition(double dx, double dy) {
    if (isDead) {
      return;
    }
    super.translatePosition(dx, dy);
  }

  @Override
  public void takeDamage(double amount, World world, Unit attacker) {
    if (isDead) {
      return;
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Amount: " + amount);
    }

    unitState.onTakeDamage(amount, world, attacker);

    if (health - amount > 0) {
      // Not dead
      health -= amount;
      this.damagedEvent.broadcast(amount);
    } else {
      isDead = true;
      health = 0;
      unitState = new Dying(Sequence.DYING, this);
      attacker.nextLevel();
    }
  }

  @Override
  public void gainHealth(double amount) {
    if (isDead) {
      return;
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Amount: " + amount);
    }

    health += amount;

    if (health > this.levelMultiplyer(this.unitType.getStartingHealth())) {
      health = this.levelMultiplyer(this.unitType.getStartingHealth());
    }
  }

  @Override
  public void gainHealth(double amt, World world) {
    this.gainHealth(amt);
    this.healedEvent.broadcast(amt);

    // TODO remove
    world.addStaticEntity(
        new StaticEntity(
            this.getTopLeft(),
            this.getSize(),
            Sheet.HEAL_EFFECT.getImagesForSequence(SpriteSheet.Sequence.HEAL),
            false
        )
    );
  }

  /**
   * Returns the team that the Unit belongs to.
   *
   * @return Team Unit is a part of.
   */
  @Override
  public Team getTeam() {
    return team;
  }

  /**
   * Returns the current health of the unit.
   *
   * @return int health of the Unit.
   */
  @Override
  public double getHealth() {
    return health;
  }

  public double getMaxHealth() {
    return this.levelMultiplyer(unitType.getStartingHealth());
  }

  @Override
  public double getHealthPercent() {
    return this.getHealth() / this.getMaxHealth();
  }


  /**
   * Add a new effect and start it.
   */
  @Override
  public void addEffect(Effect effect) {
    if (!effect.isTargetUnit(this)) {
      throw new IllegalArgumentException();
    }

    effect.start();

    // The effect may expire immediately
    if (!effect.isExpired()) {
      activeEffects.add(effect);
    }
  }

  private void tickEffects(long timeSinceLastTick) {
    for (Iterator<Effect> iterator = activeEffects.iterator(); iterator.hasNext(); ) {
      Effect effect = iterator.next();

      effect.tick(timeSinceLastTick);

      // Expire after tick
      if (effect.isExpired()) {
        iterator.remove();
      }
    }
  }

  @Override
  public UnitSpriteSheet getSpriteSheet() {
    return spriteSheet;
  }

  @Override
  public GameImage getIcon() {
    return this.spriteSheet.getImagesForSequence(Sequence.IDLE, Direction.DOWN).get(0);
  }

  /**
   * Gets the direction from the current state.
   */
  @Override
  public Direction getCurrentDirection() {
    if (unitState == null) {
      return Direction.DOWN;
    }
    return unitState.getCurrentDirection();
  }

  @Override
  public void setTarget(Target target) {
    this.setNextState(target.getState());
  }

  @Override
  public boolean contains(MapPoint point) {
    return getRect().contains(point);
  }

  @Override
  public Renderable accept(
      Config config, ViewVisitor viewVisitor
  ) {
    return viewVisitor.makeUnitView(config, this);
  }

  @Override
  public boolean isSameTypeAs(Unit other) {
    return this.unitType.toString().equals(other.getType());
  }

  @Override
  public double getSpeed() {
    return this.levelMultiplyer(speed, UNIT_MAX_SPEED);
  }

  @Override
  public double getAutoAttackDistance() {
    double range = this.getUnitType().baseAttack.getModifiedRange(this);
    if (range < 2) {
      return this.getUnitType().lineOfSight * 0.7;
    } else {
      return range;
    }
  }

  @Override
  public Event<Double> getDamagedEvent() {
    return this.damagedEvent;
  }

  @Override
  public Event<Double> getHealedEvent() {
    return this.healedEvent;
  }

  /**
   * Public for testing only.
   */
  public UnitState _getUnitState() {
    return unitState;
  }

  @Override
  public String getType() {
    return this.unitType.toString();
  }

  @Override
  public void nextLevel() {
    double originalHealth = this.getHealthPercent();
    this.setLevel(this.level + 1);
    // Maintain current level of health
    this.gainHealth(levelMultiplyer(this.unitType.getStartingHealth()) * originalHealth
        - this.health);
  }

  @Override
  public int getLevel() {
    return this.level;
  }

  @Override
  public double getAttackSpeedModifier() {
    return 1;
  }

  @Override
  public double getDamageModifier() {
    double amount = 1;

    for (Effect activeEffect : activeEffects) {
      amount = activeEffect.alterDamageModifier(amount);
    }

    return amount + this.getLevelMultiplyer();
  }

  @Override
  public double getRangeModifier() {
    return 1;
  }

  @Override
  public MapPoint getLocation() {
    return this.getCentre();
  }

  @Override
  public Collection<Unit> getEffectedUnits(World world) {
    return Collections.singleton(this);
  }

  @Override
  public boolean isValidTargetFor(Unit unit) {
    return !this.isDead;
  }
}
