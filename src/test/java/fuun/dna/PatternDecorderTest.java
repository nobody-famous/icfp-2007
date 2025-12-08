package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PatternDecorderTest {
    private void runTest(String startDNA, String expected) throws Exception {
        var dna = fuun.Utils.createDNA(fuun.Utils.stringToBases(startDNA));
        var cursor = dna.getCursor();
        var pattern = new PatternDecoder(cursor).decode();

        assertEquals(expected, pattern.toString());
    }

    @Test
    public void basesTest() throws Exception {
        runTest("CFPICIIC", "ICFP");
    }
}
