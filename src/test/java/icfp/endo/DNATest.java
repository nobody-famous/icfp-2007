package icfp.endo;

import static org.junit.Assert.assertEquals;

import java.util.List;

public class DNATest {
  public static void init() {
    DNA dna = new DNA();
    StringChunk input = new StringChunk("IIP");

    dna.prepend(input);
    assertEquals("I", "" + dna.pop());
    assertEquals("I", "" + dna.pop());
    assertEquals("P", "" + dna.pop());
    assertEquals(0, dna.pop());
  }

  public static void chunks() {
    DNA dna = new DNA();

    dna.prepend(new StringChunk("P"));
    dna.prepend(new StringChunk("I"));
    dna.prepend(new StringChunk("I"));

    assertEquals(3, dna.length());

    assertEquals("I", "" + dna.pop());
    assertEquals(2, dna.length());

    assertEquals("I", "" + dna.pop());
    assertEquals(1, dna.length());

    assertEquals("P", "" + dna.pop());
    assertEquals(0, dna.length());

    assertEquals(0, dna.pop());

    dna.prepend(new StringChunk("IJKLMN"));
    dna.prepend(new StringChunk("H"));
    dna.prepend(new StringChunk("ABCDEFG"));

    assertEquals("H", "" + dna.get(7));
    assertEquals("K", "" + dna.get(10));
  }

  public static void prependAfterRead() {
    DNA dna = new DNA();

    dna.prepend(new StringChunk("PP"));
    dna.prepend(new StringChunk("CC"));
    dna.prepend(new StringChunk("II"));

    assertEquals("I", "" + dna.pop());
    assertEquals(5, dna.length());

    dna.prepend(new StringChunk("FF"));
    assertEquals("FFICCPP", dna.toString());

    assertEquals("F", "" + dna.pop());
    assertEquals("F", "" + dna.pop());
    assertEquals("I", "" + dna.pop());
    assertEquals("C", "" + dna.pop());

    assertEquals("CPP", dna.toString());
  }

  public static void substring() {
    DNA dna = new DNA();

    dna.prepend(new StringChunk("EF"));
    dna.prepend(new StringChunk("CD"));
    dna.prepend(new StringChunk("AB"));

    assertEquals("BCDE", dna.substring(1, 5).substring(0));
    assertEquals("DEF", dna.substring(3, 6).substring(0));
    assertEquals("EF", dna.substring(4, 6).substring(0));

    dna = new DNA();

    dna.prepend(new StringChunk("IJKL"));
    dna.prepend(new StringChunk("EFGH"));
    dna.prepend(new StringChunk("ABCD"));

    assertEquals("BC", dna.substring(1, 3).substring(0));
    assertEquals("GH", dna.substring(6, 8).substring(0));
    assertEquals("GHIJ", dna.substring(6, 10).substring(0));
  }

  public static void trunc() {
    DNA dna = new DNA();

    dna.prepend(new StringChunk("IJKL"));
    dna.prepend(new StringChunk("EFGH"));
    dna.prepend(new StringChunk("ABCD"));

    dna.trunc(2);
    assertEquals("CDEFGHIJKL", dna.toString());

    dna.trunc(2);
    assertEquals("EFGHIJKL", dna.toString());

    dna.trunc(6);
    assertEquals("KL", dna.toString());
  }

  public static void copy() {
    String pref = "IIPIFFCPICICIICPIICIPPPICIIC";
    DNA dna = new DNA();

    dna.prepend(new StringChunk(pref));

    assertEquals("I", "" + dna.pop());
    assertEquals("I", "" + dna.pop());
    assertEquals("P", "" + dna.pop());

    DNAChunk copy = dna.substring(2, 6);
    assertEquals("FCPI", copy.substring(0));
  }

  public static void prefix() {
    String pref = "IIPIFFCPICICIICPIICIPPPICIIC";
    DNAConverter endo = new DNAConverter(pref);
    List<PatternItem> pattern;
    List<TemplItem> templ;

    pattern = endo.pattern();
    templ = endo.template();

    System.out.println("pattern: " + endo.patternToString(pattern));
    System.out.println("template: " + endo.templateToString(templ));
  }

  public static void main(String[] args) {
    // init();
    chunks();
    // prependAfterRead();
    // substring();
    // trunc();
    // copy();
    // prefix();
  }
}
