package icfp.endo;

/***********************************************************************
 * String implementation of a DNA chunk
 ***********************************************************************/
public class StringChunk implements DNAChunk {
  public StringChunk(String str) {
    this.str = str;
    this.start = 0;
    this.end = str.length();
  }

  public int size() {
    return end - this.start;
  }

  @Override
  public void trunc(int size) {
    this.start += size;
  }

  @Override
  public DNAChunk copy(int start, int end) {
    StringChunk copy = new StringChunk(this.str);

    copy.start = this.start + start;
    copy.end = this.start + end;

    return copy;
  }

  @Override
  public char pop() {
    char ch = get(0);

    trunc(1);

    return ch;
  }

  @Override
  public char get(int ndx) {
    return (start + ndx < str.length()) ? str.charAt(start + ndx) : '\0';
  }

  @Override
  public String substring(int ndx) {
    if (start + ndx > end) {
      return "";
    }

    return str.substring(start + ndx, end);
  }

  @Override
  public String substring(int s, int e) {
    int begin = this.start + s;
    int stop = (this.start + e) > this.end ? this.end : this.start + e;

    return str.substring(begin, stop);
  }

  @Override
  public void setEnd(int end) {
    this.end = end;
  }

  @Override
  public void setStart(int start) {
    this.start = start;
  }

  private String str;
  private int start;
  private int end;
}
