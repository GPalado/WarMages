package main.game.model.entity;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import main.common.Unit;
import main.common.images.GameImage;
import main.common.images.UnitSpriteSheet;
import main.common.images.UnitSpriteSheet.Sequence;
import main.common.util.MapPoint;
import main.common.util.MapRect;
import main.common.util.MapSize;
import main.common.Effect;
import main.game.model.world.World;

public class DefaultUnit implements Unit {

  private static final long serialVersionUID = 1L;

  public static final int LEEWAY_FOR_PATH = 5; // TODO don't hard code
  private final UnitSpriteSheet spriteSheet;
  private final Team team;

  private Unit target;
  private UnitType unitType;
  private UnitState unitState;
  private List<Effect> activeEffects = new ArrayList<>();

  private boolean isDead = false;
  private boolean hasCreatedDeadUnit = false;

  private int health;
  private int damageAmount;

  private MapPoint position;
  private MapSize size;

  protected Queue<MapPoint> path = new LinkedList<>();
  protected double speed;
  private static final double LEEWAY = 0.2;

  /**
   * Constructor takes the unit's position, size, and team.
   */
  public DefaultUnit(
      MapPoint position,
      MapSize size,
      Team team,
      UnitSpriteSheet sheet,
      UnitType unitType
  ) {
    this.position = position;
    this.size = size;
    //    super(position, size, unitType.getMovingSpeed()); todo fix
    this.team = team;
    this.unitType = unitType;
    this.health = unitType.getStartingHealth();
    this.spriteSheet = sheet;
    this.unitState = new IdleUnitState(this);
    setDamageAmount(unitType.getBaselineDamage());
  }

  /**
   * Sets the Unit's next state to be the given state.
   *
   * @param state to be changed to.
   */
  @Override
  public void setNextState(UnitState state) {
    unitState.requestState(requireNonNull(state));
  }

  @Override
  public UnitType getUnitType() {
    return unitType;
  }

  @Override
  public double getLineOfSight() {
    return this.unitType.lineOfSight;
  }

  /**
   * Returns a DeadUnit to replace the current Unit when it dies.
   *
   * @return DeadUnit to represent dead current Unit.
   */
  @Override
  public DeadUnit createDeadUnit() {
    if (!isDead || hasCreatedDeadUnit) {
      throw new IllegalStateException();
    }

    hasCreatedDeadUnit = true;
    GameImage deadImage = spriteSheet.getImagesForSequence(Sequence.DEAD, Direction.DOWN).get(0);
    return new DeadUnit(position, size, deadImage);
  }

  @Override
  public void tick(long timeSinceLastTick, World world) {
    //update image and state if applicable
    unitState.tick(timeSinceLastTick, world);
    unitState = requireNonNull(unitState.updateState());
    //update path in case there is a target and it has moved.
    updatePath(world);
    //update position
    tickPosition(timeSinceLastTick, world);
    //check if has target and target is within attacking proximity. Request state change.
    if (target != null && targetWithinProximity()) {
      attack();
    } else {
      //if no target, check if unit reached destination and change to idle if so
      if (this.path.size() == 0) {
        setNextState(new IdleUnitState(this));
      }
    }
    tickEffects(timeSinceLastTick);
  }

  @Override
  public MapPoint getPreviousTopLeft() {
    return null;
  }

  @Override
  public void setImage(GameImage image) {

  }

  @Override
  public GameImage getImage() {
    return unitState.getImage();
  }

  @Override
  public void attack() {
    if (isDead) {
      throw new IllegalStateException("Is dead");
    }
    if (target == null) {
      throw new IllegalStateException(
          "No target to attack. Check if there is a target before calling attack"
      );
    }

    setNextState(new AttackingUnitState(this));
  }

  @Override
  public MapPoint getTopLeft() {
    return null;
  }

  @Override
  public MapPoint getCentre() {
    return null;
  }

  @Override
  public MapSize getSize() {
    return null;
  }

  @Override
  public MapRect getRect() {
    return null;
  }

  @Override
  public void translatePosition(double dx, double dy) {
    if (isDead) {
      return;
    }
    position = new MapPoint(position.x + dx, position.y + dy);
  }

  @Override
  public void takeDamage(int amount, World world) {
    if (isDead) {
      return;
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Amount: " + amount);
    }

    if (health - amount >= 0) {
      // Not dead
      health -= amount;
    } else {
      isDead = true;
      health = 0;
      setNextState(new DyingState(Sequence.DYING, this));
    }
  }

  @Override
  public void gainHealth(int amount) {
    if (isDead) {
      return;
    }
    if (amount < 0) {
      throw new IllegalArgumentException("Amount: " + amount);
    }

    health += amount;
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
  public int getHealth() {
    return health;
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

  @Override
  public int getDamageAmount() {
    int amount = damageAmount;

    for (Effect activeEffect : activeEffects) {
      amount = activeEffect.alterDamageAmount(amount);
    }

    return amount;
  }

  @Override
  public void tickEffects(long timeSinceLastTick) {
    for (Iterator<Effect> iterator = activeEffects.iterator(); iterator.hasNext(); ) {
      Effect effect = iterator.next();

      effect.tick(timeSinceLastTick);

      // Expire after tick
      if (effect.isExpired()) {
        iterator.remove();
      }
    }
  }

  public Unit getTarget() {
    return target;
  }

  @Override
  public UnitSpriteSheet getSpriteSheet() {
    return spriteSheet;
  }

  /**
   * Gets the direction from the current state.
   */
  @Override
  public Direction getCurrentDirection() {
    return unitState.getCurrentDirection();
  }

  @Override
  public void setTarget(Unit target, World world) {
    this.target = Objects.requireNonNull(target);
    updatePath(world);
  }

  @Override
  public void clearTarget() {
    this.target = null;
  }

  /**
   * Updates the path in case target has moved.
   */
  protected void updatePath(World world) {
    if (target == null) {
      return;
    }
    setPath(world.findPath(getCentre(), target.getCentre()));
  }

  @Override
  public void setDamageAmount(int amount) {
    if (amount <= 0 || amount >= 100) {
      throw new IllegalArgumentException("Invalid damage: " + amount);
    }

    damageAmount = amount;
  }

  @Override
  public boolean targetWithinProximity() {
    if (target == null) {
      throw new IllegalStateException("No target set");
    }
    return target.getCentre().distanceTo(getCentre()) < LEEWAY;
  }

  @Override
  public void setPath(List<MapPoint> path) {
    this.path = new LinkedList<>(path);
  }

  @Override
  public void tickPosition(long timeSinceLastTick, World world) {
    if (path == null || path.isEmpty()) {
      return;
    }
    MapPoint target = this.path.peek();
    double distance = getCentre().distanceTo(target);
    if (distance < LEEWAY_FOR_PATH + Math.max(getSize().width / 2, getSize().height / 2)) {
      this.path.poll();
      if (this.path.size() == 0) {
        return;
      }
      target = this.path.peek();
    }

    double dx = target.x - getTopLeft().x;
    double dy = target.y - getTopLeft().y;
    double mx = (Math.min(speed / Math.hypot(dx, dy), 1)) * dx;
    double my = (Math.min(speed / Math.hypot(dx, dy), 1)) * dy;
    assert speed + 0.001 > Math.hypot(mx, my) : "the unit tried to move faster than its speed";
    translatePosition(mx, my);
  }
}
