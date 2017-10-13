package test.game.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import main.common.GameController;
import main.game.controller.DefaultGameController;
import main.game.model.DefaultGameModel;
import main.game.model.world.World;
import main.game.model.world.pathfinder.DefaultPathFinder;
import main.game.view.events.MouseClick;
import main.common.util.Events.MainGameTick;
import main.common.util.MapPoint;
import org.junit.Test;
import test.game.model.world.WorldTestUtils;

/**
 * Tests for the GameController API.
 *
 * @author Hrshikesh Arora
 * @author Eric Diputado (External Tester)
 */
public class GameControllerTest {

  DefaultGameModel defaultGameModel = null;
  GameController controller = null;

  @Test
  public void checkSelectOneUnit() {
    defaultGameModel = new DefaultGameModel(new World(
        WorldTestUtils.createLevels(WorldTestUtils.createLevelWith(
            WorldTestUtils.createDefaultUnit(new MapPoint(1, 0)),
            WorldTestUtils.createDefaultUnit(new MapPoint(1, 0))
        )),
        WorldTestUtils.createDefaultHeroUnit(new MapPoint(1,0)),
        new DefaultPathFinder()),
        new MainGameTick());
    controller = new DefaultGameController(defaultGameModel);
    controller.onMouseEvent(new MouseClick() {
      @Override
      public boolean wasLeft() {
        return true;
      }

      @Override
      public boolean wasShiftDown() {
        return false;
      }

      @Override
      public boolean wasCtrlDown() {
        return false;
      }

      @Override
      public MapPoint getLocation() {
        return new MapPoint(1, 0);
      }
    });
    assertEquals(1, defaultGameModel.getUnitSelection().size());
  }

  @Test
  public void checkSelectNoUnit() {
    defaultGameModel = new DefaultGameModel(new World(
        WorldTestUtils.createLevels(WorldTestUtils.createLevelWith(
            WorldTestUtils.createDefaultUnit(new MapPoint(1, 0)),
            WorldTestUtils.createDefaultUnit(new MapPoint(1, 0))
        )),
        WorldTestUtils.createDefaultHeroUnit(new MapPoint(1,0)),
        new DefaultPathFinder()),
        new MainGameTick());
    controller = new DefaultGameController(defaultGameModel);
    controller.onMouseEvent(new MouseClick() {
      @Override
      public boolean wasLeft() {
        return true;
      }

      @Override
      public boolean wasShiftDown() {
        return false;
      }

      @Override
      public boolean wasCtrlDown() {
        return false;
      }

      @Override
      public MapPoint getLocation() {
        return new MapPoint(20, 0);
      }
    });
    assertEquals(0, defaultGameModel.getUnitSelection().size());
  }



}
