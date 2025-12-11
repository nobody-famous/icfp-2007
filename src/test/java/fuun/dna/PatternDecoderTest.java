package fuun.dna;

import org.junit.jupiter.api.Test;

public class PatternDecoderTest extends DecoderTest<Pattern> {
    @Test
    public void basesTest() throws Exception {
        testBases(new PatternDecoder());
    }

    @Test
    public void searchTest() throws Exception {
        runTest(new PatternDecoder(), "IFFCFPICIIC", "<ICFP>");
    }

    @Test
    public void skipTest() throws Exception {
        runTest(new PatternDecoder(), "IPCICPIIC", "!5");
        runTest(new PatternDecoder(), "IPPIIF", "!0");
    }

    @Test
    public void captureTest() throws Exception {
        runTest(new PatternDecoder(), "IIPIPPIICIIC", "(!0)");
        runTest(new PatternDecoder(), "IIPIPICPIIPIPCCPIICIPIICPIICIIF", "(!2(!3)!4)");
    }

    @Test
    public void rnaTest() throws Exception {
        testRNA(new PatternDecoder());
    }
}
