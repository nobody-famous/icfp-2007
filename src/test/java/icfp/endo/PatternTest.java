package icfp.endo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class PatternTest {
  @Test
  public void firstExample() {
    DNAConverter endo = new DNAConverter("CIIC");

    List<PatternItem> pattern = endo.pattern();
    assertEquals("I", endo.patternToString(pattern));
  }

  @Test
  public void secondExample() {
    DNAConverter endo = new DNAConverter("IIPIPICPIICICIIF");

    List<PatternItem> pattern = endo.pattern();
    assertEquals("(!2)P", endo.patternToString(pattern));
  }

  @Test
  public void consts() {
    DNAConverter endo = new DNAConverter("IIPIFPFCPIIFFCPIIF");

    List<PatternItem> pattern = endo.pattern();
    assertEquals("([?CIF])CIF", endo.patternToString(pattern));

    endo = new DNAConverter("IIPIFCFCICPIIFFCPIIF");

    pattern = endo.pattern();
    assertEquals("([?CIPF])CIF", endo.patternToString(pattern));
  }

  @Test
  public void rna() {
    DNAConverter endo = new DNAConverter("IIIPIPIIPCFCPIIC");

    List<PatternItem> pattern = endo.pattern();
    assertEquals("CIF", endo.patternToString(pattern));
  }
}