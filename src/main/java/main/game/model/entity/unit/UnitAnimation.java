package main.game.model.entity.unit;

import java.io.Serializable;
import main.common.entity.Unit;
import main.common.images.UnitSpriteSheet.Sequence;
import main.images.Animation;

/**
 * Handles what image should be shown as time progresses.
 * TODO could do with a factory rather than having an empty class
 * @author paladogabr
 * @author chongdyla (Secondary author)
 */
public class UnitAnimation extends Animation implements Serializable {

  private static final long serialVersionUID = 1L;
  private final Unit unit;
  private final Sequence sequence;

  /**
   * Constructor takes the sequence relevant to the unit's state, the sprite sheet for the unit, and
   * the direction of the unit.
   */
  public UnitAnimation(
      Unit unit,
      Sequence sequence,
      int length
  ) {
    super(unit.getSpriteSheet().getImagesForSequence(sequence, unit.getCurrentDirection()),
        length);
    this.unit = unit;
    this.sequence = sequence;
  }

  public void updateDirection() {
    this.setImages(unit.getSpriteSheet().getImagesForSequence(sequence, unit.getCurrentDirection()));
  }

}
