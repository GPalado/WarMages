package main.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a point on the Map.
 */
public class MapPoint {

  public final double x;
  public final double y;

  public MapPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public MapPoint(Point p) {
    this.x = p.x;
    this.y = p.y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MapPoint mapPoint = (MapPoint) o;

    if (Double.compare(mapPoint.x, x) != 0) {
      return false;
    }
    return Double.compare(mapPoint.y, y) == 0;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public double distance(MapPoint other) {
    return Math.hypot(this.x - other.x, this.y - other.y);
  }

  public List<MapPoint> getNeighbours() {
    return new ArrayList<MapPoint>(
        Arrays.asList(new MapPoint(this.x-1, this.y), //left
                      new MapPoint(this.x+1, this.y), //right
                      new MapPoint(this.x, this.y-1), //top
                      new MapPoint(this.x, this.y+1), //bottom
                      new MapPoint(this.x-1, this.y-1), //top-left
                      new MapPoint(this.x+1, this.y-1), //top-right
                      new MapPoint(this.x-1, this.y+1), //bottom-left
                      new MapPoint(this.x+1, this.y+1) //bottom-right
        ));
  }
}
