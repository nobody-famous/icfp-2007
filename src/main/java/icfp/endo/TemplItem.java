package icfp.endo;

public class TemplItem {
  public enum Type {
    BASE, PROT, LEN
  };

  public TemplItem(Type type) {
    this(type, 0, 0);
  }

  public TemplItem(Type type, int value) {
    this(type, value, 0);
  }

  public TemplItem(Type type, int value, int protLevel) {
    this.type = type;
    this.value = value;
    this.protLevel = protLevel;
  }

  public Type getType() {
    return type;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public void setProtLevel(int level) {
    this.protLevel = level;
  }

  public int getProtLevel() {
    return protLevel;
  }

  private Type type;
  private int value;
  private int protLevel;
}
