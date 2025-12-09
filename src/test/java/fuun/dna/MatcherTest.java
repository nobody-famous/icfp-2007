package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class MatcherTest {
    private void runTest(String input, Pattern pattern, boolean shouldMatch, String dnaResult,
            List<fuun.DNA> expectedEnv) {
        var dna = fuun.Utils.createDNA(fuun.Utils.stringToBases(input));
        var matcher = new Matcher();
        var matched = matcher.match(dna, pattern, expectedEnv);

        assertEquals(matched, shouldMatch);
        assertEquals(dna.toString(), dnaResult);
    }

    @Test
    void testBases() {
        runTest("ICFP",
                new Pattern()
                        .add(new Pattern.Base(fuun.Base.I))
                        .add(new Pattern.Base(fuun.Base.C))
                        .add(new Pattern.Base(fuun.Base.F))
                        .add(new Pattern.Base(fuun.Base.P)),
                true, "", new ArrayList<>());
        runTest("ICFP",
                new Pattern()
                        .add(new Pattern.Base(fuun.Base.I))
                        .add(new Pattern.Base(fuun.Base.C))
                        .add(new Pattern.Base(fuun.Base.P))
                        .add(new Pattern.Base(fuun.Base.I)),
                false,
                "ICFP",
                new ArrayList<>());
    }

    @Test
    void testSkip() {
        runTest("ICFP", new Pattern().add(new Pattern.Skip(1)), true, "CFP", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Skip(3)), true, "P", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Skip(4)), true, "", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Skip(5)), false, "ICFP", new ArrayList<>());
    }
}
