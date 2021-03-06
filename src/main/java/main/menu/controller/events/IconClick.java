package main.menu.controller.events;

/**
 * Parent for dataclasses when an icon is clicked.
 *
 * @author Andrew McGhie
 */
public interface IconClick {

  boolean wasShiftDown();

  boolean wasCtrlDown();

  boolean wasLeftClick();
}
