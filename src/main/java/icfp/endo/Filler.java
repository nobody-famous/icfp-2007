package icfp.endo;

import java.util.LinkedList;
import java.util.Queue;

/***********************************************************************
 * Flood fill algorithm
 ***********************************************************************/
public class Filler {
  public interface Fillable {
    boolean isTarget(int x, int y);

    boolean isFilled(int x, int y);

    void setValue(int x, int y);
  }

  public enum Direction {
    NORTH, SOUTH, EAST, WEST,
  };

  public Filler(int width, int height, Fillable fillable) {
    this.height = height;
    this.width = width;
    this.fillable = fillable;
  }

  public void fill(int x, int y) {
    State state = new State();

    state.x = state.startX = x;
    state.y = state.startY = y;
    state.nodes.clear();

    if (fillable.isFilled(x, y)) {
      return;
    }

    state.nodes.add(new int[] { state.x, state.y });
    while (!state.nodes.isEmpty()) {
      int[] node = state.nodes.poll();
      state.x = node[0];
      state.y = node[1];

      fillLine(state, state.x, state.y, Direction.EAST);
      fillLine(state, state.x - 1, state.y, Direction.WEST);
    }
  }

  private void fillLine(State state, int x, int y, Direction d) {
    if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
      return;
    }

    while (fillable.isTarget(x, y)) {
      fillable.setValue(x, y);
      switch (d) {
      case EAST:
        if ((y + 1 < height) && (fillable.isTarget(x, y + 1))) {
          state.nodes.add(new int[] { x, y + 1 });
        }
        if ((y - 1 >= 0) && (fillable.isTarget(x, y - 1))) {
          state.nodes.add(new int[] { x, y - 1 });
        }
        x += 1;
        break;
      case WEST:
        if ((y + 1 < height) && (fillable.isTarget(x, y + 1))) {
          state.nodes.add(new int[] { x, y + 1 });
        }
        if ((y - 1 >= 0) && (fillable.isTarget(x, y - 1))) {
          state.nodes.add(new int[] { x, y - 1 });
        }
        x -= 1;
        break;
      case NORTH:
        if ((x + 1 < width) && (fillable.isTarget(x + 1, y))) {
          state.nodes.add(new int[] { x + 1, y });
        }
        if ((x - 1 >= 0) && (fillable.isTarget(x - 1, y))) {
          state.nodes.add(new int[] { x - 1, y });
        }
        y += 1;
        break;
      case SOUTH:
        if ((x + 1 < width) && (fillable.isTarget(x + 1, y))) {
          state.nodes.add(new int[] { x + 1, y });
        }
        if ((x - 1 >= 0) && (fillable.isTarget(x - 1, y))) {
          state.nodes.add(new int[] { x - 1, y });
        }
        y -= 1;
        break;
      }

      // In case we walk off the map
      if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
        return;
      }
    }
  }

  private Fillable fillable;
  private int height;
  private int width;

  private class State {
    public State() {
      x = startX = 0;
      y = startY = 0;
      nodes = new LinkedList<>();
    }

    int x;
    int y;
    int startX;
    int startY;
    Queue<int[]> nodes;
  }
}
