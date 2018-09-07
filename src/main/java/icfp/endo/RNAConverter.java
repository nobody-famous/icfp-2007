package icfp.endo;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/***********************************************************************
 * Convert RNA into an image
 ***********************************************************************/
public class RNAConverter {
  public static final int BLACK = 0;
  public static final int RED = 0xff0000;
  public static final int GREEN = 0x00ff00;
  public static final int YELLOW = 0xffff00;
  public static final int BLUE = 0x0000ff;
  public static final int MAGENTA = 0xff00ff;
  public static final int CYAN = 0x00ffff;
  public static final int WHITE = 0xffffff;

  public static final int HEIGHT = 600;
  public static final int WIDTH = 600;
  public static final int X = 0;
  public static final int Y = 1;
  public static final int TRANSPARENT = 0;
  public static final int OPAQUE = 0xff000000;

  public RNAConverter(String rna, String outFileName) {
    this.rna = new DNA();
    this.rna.prepend(new StringChunk(rna));
    this.outFileName = outFileName;

    map.put("PIPIIIC", RNAToken.ADD_BLACK);
    map.put("PIPIIIP", RNAToken.ADD_RED);
    map.put("PIPIICC", RNAToken.ADD_GREEN);
    map.put("PIPIICF", RNAToken.ADD_YELLOW);
    map.put("PIPIICP", RNAToken.ADD_BLUE);
    map.put("PIPIIFC", RNAToken.ADD_MAGENTA);
    map.put("PIPIIFF", RNAToken.ADD_CYAN);
    map.put("PIPIIPC", RNAToken.ADD_WHITE);
    map.put("PIPIIPF", RNAToken.ADD_TRANS);
    map.put("PIPIIPP", RNAToken.ADD_OPAQUE);
    map.put("PIIPICP", RNAToken.EMPTY_BUCKET);
    map.put("PIIIIIP", RNAToken.MOVE);
    map.put("PCCCCCP", RNAToken.TURN_CCW);
    map.put("PFFFFFP", RNAToken.TURN_CW);
    map.put("PCCIFFP", RNAToken.MARK);
    map.put("PFFICCP", RNAToken.LINE);
    map.put("PIIPIIP", RNAToken.TRYFILL);
    map.put("PCCPFFP", RNAToken.ADD_BITMAP);
    map.put("PFFPCCP", RNAToken.COMPOSE);
    map.put("PFFICCF", RNAToken.CLIP);
  }

  /***********************************************************************
   * Build the image, per the docs
   ***********************************************************************/
  public void build() {
    dir = Direction.E;
    position = new int[] { 0, 0 };
    mark = null;
    bucket = new ArrayList<>();
    transBucket = new ArrayList<>();
    bitmaps = new LinkedList<>();

    imageNumber = 0;

    bitmaps.add(new Bitmap());

    while (true) {
      String next = next();
      if (next == null) {
        break;
      }
      RNAToken token = map.get(next);

      if (token == null) {
        continue;
      }

      if (rna.isEmpty()) {
        writeFile();
        break;
      }

      switch (token) {
      case ADD_WHITE:
        bucket.add(WHITE);
        break;
      case ADD_CYAN:
        bucket.add(CYAN);
        break;
      case ADD_MAGENTA:
        bucket.add(MAGENTA);
        break;
      case ADD_BLUE:
        bucket.add(BLUE);
        break;
      case ADD_YELLOW:
        bucket.add(YELLOW);
        break;
      case ADD_GREEN:
        bucket.add(GREEN);
        break;
      case ADD_RED:
        bucket.add(RED);
        break;
      case ADD_BLACK:
        bucket.add(BLACK);
        break;
      case ADD_TRANS:
        transBucket.add(TRANSPARENT);
        break;
      case ADD_OPAQUE:
        transBucket.add(OPAQUE);
        break;
      case TURN_CCW:
        dir = turnCounterClockwise(dir);
        break;
      case TURN_CW:
        dir = turnClockwise(dir);
        break;
      case MOVE:
        move(dir);
        break;
      case MARK:
        if (mark == null) {
          mark = new int[2];
        }
        mark[X] = position[X];
        mark[Y] = position[Y];
        break;
      case LINE:
        line(position, mark);
        break;
      case ADD_BITMAP:
        addBitmap(new Bitmap());
        break;
      case EMPTY_BUCKET:
        bucket.clear();
        break;
      case TRYFILL:
        tryFill(bitmaps.get(0));
        break;
      case COMPOSE:
        compose(bitmaps);
        break;
      default:
        throw new RuntimeException("build unhandled token " + token);
      }
    }
  }

  private void writeFile() {
    BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    Bitmap bm = bitmaps.get(0);

    for (int x = 0; x < WIDTH; x += 1) {
      for (int y = 0; y < HEIGHT; y += 1) {
        img.setRGB(x, y, (bm.map[x][y] & 0xffffff));
      }
    }

    try {
      File outFile = new File(outFileName);
      ImageIO.write(img, "jpg", outFile);
    } catch (Exception ex) {
      System.out.println("Failed to write image file");
      ex.printStackTrace();
    }
  }

  private void compose(List<Bitmap> bmList) {
    if (bmList.size() < 2) {
      return;
    }

    Bitmap a = bmList.get(0);
    Bitmap b = bmList.get(1);
    for (int x = 0; x < WIDTH; x += 1) {
      for (int y = 0; y < HEIGHT; y += 1) {
        int aPixel = a.map[x][y];
        int bPixel = a.map[x][y];

        int aTrans = (aPixel & 0xff000000) >>> 24;
        int bTrans = (bPixel & 0xff000000) >>> 24;
        int aRed = (aPixel & 0xff0000) >>> 16;
        int bRed = (bPixel & 0xff0000) >>> 16;
        int aGreen = (aPixel & 0x00ff00) >>> 8;
        int bGreen = (bPixel & 0x00ff00) >>> 8;
        int aBlue = (aPixel & 0xff);
        int bBlue = (bPixel & 0xff);

        int pixel = 0;
        pixel += (aRed + (int) Math.floor(bRed * (255 - aTrans) / 255)) << 16;
        pixel += (aGreen + (int) Math.floor(bGreen * (255 - aTrans) / 255)) << 8;
        pixel += aBlue + (int) Math.floor(bBlue * (255 - aTrans) / 255);
      }

    }

    bmList.remove(0);
  }

  private void tryFill(Bitmap bm) {
    int x = position[0];
    int y = position[1];
    int target = bm.map[x][y];
    int replace = currentPixel(bucket, transBucket);

    Filler filler = new Filler(WIDTH, HEIGHT, new Filler.Fillable() {
      public void setValue(int x, int y) {
        bm.map[x][y] = replace;
      }

      public boolean isTarget(int x, int y) {
        return bm.map[x][y] == target;
      }

      public boolean isFilled(int x, int y) {
        return bm.map[x][y] == replace;
      }
    });

    filler.fill(x, y);
  }

  private void addBitmap(Bitmap bm) {
    if (bitmaps.size() < 10) {
      bitmaps.add(0, bm);
    }
  }

  private void line(int[] a, int[] b) {
    int deltax = b[X] - a[X];
    int deltay = b[Y] - a[Y];
    int d = Math.max(Math.abs(deltax), Math.abs(deltay));
    int c = (deltax * deltay) <= 0 ? 1 : 0;
    int x = (a[X] * d) + (int) Math.floor((d - c) / 2);
    int y = (a[Y] * d) + (int) Math.floor((d - c) / 2);

    int current = currentPixel(bucket, transBucket);
    current = WHITE;
    for (int loop = 0; loop < d; loop += 1) {
      setPixel((int) Math.floor(x / d), (int) Math.floor(y / d), current);
      x += deltax;
      y += deltay;
    }

    setPixel(b[X], b[Y], current);
  }

  private int nextImageNumber() {
    int number = imageNumber;
    imageNumber += 1;
    return number;
  }

  private void setPixel(int x, int y, int current) {
    Bitmap bitmap = bitmaps.get(0);

    bitmap.map[x][y] = current;
  }

  public int currentPixel(List<Integer> b, List<Integer> tb) {
    int red = 0;
    int green = 0;
    int blue = 0;
    int trans = 0;
    int numColors = b.size();
    int numTrans = tb.size();

    for (int item : b) {
      red += (0xff0000 & item) >>> 16;
      green += (0x00ff00 & item) >>> 8;
      blue += 0x0000ff & item;
    }

    for (int item : tb) {
      trans += (0xff000000 & item) >>> 24;
    }

    red = (numColors > 0) ? (int) Math.floor(red / numColors) : 0;
    green = (numColors > 0) ? (int) Math.floor(green / numColors) : 0;
    blue = (numColors > 0) ? (int) Math.floor(blue / numColors) : 0;
    trans = (numTrans > 0) ? (int) Math.floor(trans / numTrans) : 255;

    int pixel = 0;
    pixel = (int) Math.floor((blue * trans) / 255);
    pixel |= ((int) Math.floor((green * trans) / 255)) << 8;
    pixel |= ((int) Math.floor((red * trans) / 255)) << 16;
    pixel |= trans << 24;

    return pixel;
  }

  private void move(Direction d) {
    switch (d) {
    case N:
      position[Y] = position[Y] == 0 ? HEIGHT - 1 : (position[Y] - 1) % HEIGHT;
      break;
    // return new int[] { p[X], (p[Y] - 1) % HEIGHT };
    case E:
      position[X] = (position[X] + 1) % WIDTH;
      break;
    // return new int[] { (p[X] + 1) % WIDTH, p[Y] };
    case S:
      position[Y] = (position[Y] + 1) % HEIGHT;
      break;
    // return new int[] { p[X], (p[Y] + 1) % HEIGHT };
    case W:
      position[X] = position[X] == 0 ? WIDTH - 1 : (position[X] - 1) % WIDTH;
      break;
    // return new int[] { (p[X] - 1) % WIDTH, p[Y] };
    default:
      throw new RuntimeException("build move direction " + d);
    }

    if ((position[X] < 0) || (position[Y] < 0)) {
      throw new RuntimeException("Invalid move to " + position[X] + "," + position[Y]);
    }

    if ((position[X] >= WIDTH) || (position[Y] >= HEIGHT)) {
      throw new RuntimeException("Invalid move to " + position[X] + "," + position[Y]);
    }
  }

  private Direction turnCounterClockwise(Direction d) {
    switch (d) {
    case N:
      return Direction.W;
    case S:
      return Direction.E;
    case E:
      return Direction.N;
    case W:
      return Direction.S;
    default:
      throw new RuntimeException("Unknown direction " + d);
    }
  }

  private Direction turnClockwise(Direction d) {
    switch (d) {
    case N:
      return Direction.E;
    case S:
      return Direction.W;
    case E:
      return Direction.S;
    case W:
      return Direction.N;
    default:
      throw new RuntimeException("Unknown direction " + d);
    }
  }

  private String next() {
    DNAChunk next = rna.substring(0, 7);

    rna.trunc(7);

    return next != null ? next.substring(0) : null;
  }

  private int imageNumber;
  private String outFileName;
  private DNA rna;
  private Map<String, RNAToken> map = new HashMap<>();
  private Map<String, int[]> colors = new HashMap<>();
  private List<Integer> bucket;
  private List<Integer> transBucket;
  private List<Bitmap> bitmaps;
  private Direction dir;
  private int[] position;
  private int[] mark;

  private enum Direction {
    N, S, E, W
  };

  private class Bitmap {
    public Bitmap() {
      map = new int[WIDTH][HEIGHT];
      for (int x = 0; x < WIDTH; x += 1) {
        for (int y = 0; y < HEIGHT; y += 1) {
          map[x][y] = BLACK + TRANSPARENT;
        }
      }
    }

    int[][] map;
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Need to specify RNA file and output JPG file");
      System.exit(0);
    }

    File inRNA = new File(args[0]);

    BufferedInputStream in = new BufferedInputStream(new FileInputStream(inRNA));
    byte[] buffer = new byte[(int) inRNA.length()];
    in.read(buffer);
    in.close();

    String rna = new String(buffer);

    RNAConverter endo = new RNAConverter(rna, args[1]);
    endo.build();
  }
}
