package icfp.endo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class BasicTest {
  @Test
  public void firstTest() {
    String dna = "IIPIPICPIICICIIFICCIFPPIICCFPC";
    List<PatternItem> pattern;
    List<TemplItem> templ;
    DNAConverter endo = new DNAConverter(dna);

    pattern = endo.pattern();
    templ = endo.template();

    System.out.println("pattern " + endo.patternToString(pattern));
    System.out.println("template " + endo.templateToString(templ));
    endo.matchreplace(pattern, templ);

    System.out.println("DNA " + endo.getDNA());
    assertEquals("PICFC", endo.getDNA());
  }

  @Test
  public void secondTest() {
    String dna = "IIPIPICPIICICIIFICCIFCCCPPIICCFPC";
    List<PatternItem> pattern;
    List<TemplItem> templ;
    DNAConverter endo = new DNAConverter(dna);

    pattern = endo.pattern();
    templ = endo.template();

    endo.matchreplace(pattern, templ);

    assertEquals("PIICCFCFFPC", endo.getDNA());
  }

  @Test
  public void thirdTest() {
    String dna = "IIPIPIICPIICIICCIICFCFC";
    List<PatternItem> pattern;
    List<TemplItem> templ;
    DNAConverter endo = new DNAConverter(dna);

    pattern = endo.pattern();
    templ = endo.template();

    endo.matchreplace(pattern, templ);

    assertEquals("I", endo.getDNA());
  }

  @Test
  public void env() {
    String dna = "IIPIPIICPIICIICCIICFCFC";
    List<PatternItem> pattern;
    List<TemplItem> templ;
    DNAConverter endo = new DNAConverter(dna);

    pattern = endo.pattern();
    templ = endo.template();

    endo.matchreplace(pattern, templ);

    assertEquals("I", endo.getDNA());
  }

  @Test
  public void search() {
    // String dna = "IIPIFFCPICICIICPIICIPPPICIICCFPCFPIFPPFIIPIPIPIICIIC";
    // List<PatternItem> pattern;
    // List<TemplItem> templ;
    // Endo endo = new Endo(dna);

    // pattern = endo.pattern();
    // templ = endo.template();

    // System.out.println("pattern " + endo.patternToString(pattern));
    // System.out.println("template " + endo.templateToString(templ));
    // endo.matchreplace(pattern, templ);

    // assertEquals("I", endo.getDNA());
  }
}
