package icfp.endo;

public class PatternItem {
  public enum Type {
    BASE, JUMP, SEARCH, OPEN, CLOSE
  };

  public PatternItem(Type type) {
    this.type = type;
  }

  public PatternItem(Type type, int value) {
    this(type);
    this.value = value;
    this.search = null;
  }

  public PatternItem(Type type, String search) {
    this(type);
    this.value = 0;
    this.search = search;
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

  public void setSearch(String search) {
    this.search = search;
  }

  public String getSearch() {
    return search;
  }

  private Type type;
  private int value;
  private String search;
}
