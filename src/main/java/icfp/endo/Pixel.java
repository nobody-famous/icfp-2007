package icfp.endo;

public class Pixel {
  public Pixel(int[] rgb, int t) {
    color = rgb;
    trans = t;
  }

  public boolean equals(Pixel p) {
    if (p.trans != this.trans) {
      return false;
    }

    for (int i = 0; i < 3; i += 1) {
      if (p.color[i] != this.color[i]) {
        return false;
      }
    }

    return true;
  }

  public int[] getColor() {
    return color;
  }

  public void setColor(int[] color) {
    this.color = color;
  }

  public int getTrans() {
    return trans;
  }

  public void setTrans(int trans) {
    this.trans = trans;
  }

  private int[] color;
  private int trans;
}
