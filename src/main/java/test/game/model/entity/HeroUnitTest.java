package test.game.model.entity;

import static main.images.GameImageResource.GOLDEN_HERO_SPRITE_SHEET;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import main.game.model.GameModel;
import main.game.model.entity.HeroUnit;
import main.game.model.entity.UnitType;
import main.game.model.entity.exceptions.ItemNotInRangeException;
import main.game.model.entity.usables.Ability;
import main.game.model.entity.usables.Item;
import main.game.model.world.World;
import main.images.UnitSpriteSheet;
import main.util.MapPoint;
import main.util.MapSize;
import org.junit.Test;
import test.game.model.world.WorldTestUtils;

public class HeroUnitTest {
  private HeroUnit heroUnit = WorldTestUtils.createHeroUnit();

  @Test
  public void addingAnItemToTheInventoryShouldWorkWhenItemInRange() {
    Item item = WorldTestUtils.createStubItem(heroUnit.getTopLeft().shiftedBy(0.001, 0.001));
    heroUnit.pickUp(item);
    assertTrue(heroUnit.getItemInventory().contains(item));
  }

  @Test(expected = ItemNotInRangeException.class)
  public void addingAnItemToTheInventoryShouldNotWorkWhenItemIsFarAway() {
    Item item = WorldTestUtils.createStubItem(heroUnit.getCentre().shiftedBy(100, 100));
    heroUnit.pickUp(item);
  }

  @Test
  public void heroUnitShouldTickAbilities() {
    // Given an ability
    Ability mockAbility = mock(Ability.class);
    // and a hero
    HeroUnit heroUnit = new HeroUnit(
        new MapPoint(1, 1),
        new MapSize(1, 1),
        new UnitSpriteSheet(GOLDEN_HERO_SPRITE_SHEET),
        UnitType.SWORDSMAN,
        Arrays.asList(mockAbility)
    );
    long delay = GameModel.DELAY;

    // when I call tick
    heroUnit.tick(delay, mock(World.class));

    // then ability's tick should have been called
    verify(mockAbility, times(1)).usableTick(delay);
  }
}
