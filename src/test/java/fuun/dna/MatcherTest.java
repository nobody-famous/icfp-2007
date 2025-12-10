package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fuun.Base;

public class MatcherTest {
    private void runTest(String input, Pattern pattern, boolean shouldMatch, String dnaResult,
            List<fuun.DNA> expectedEnv) {
        var dna = fuun.Utils.createDNA(fuun.Utils.stringToBases(input));
        var matcher = new Matcher();
        var env = new ArrayList<fuun.DNA>();
        var matched = matcher.match(dna, pattern, env);

        assertEquals(matched, shouldMatch);
        assertEquals(dna.toString(), dnaResult);
        assertEquals(expectedEnv.size(), env.size());
        for (var index = 0; index < env.size(); index += 1) {
            assertEquals(expectedEnv.get(index).toString(), env.get(index).toString());
        }
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

    @Test
    void testSearch() {
        runTest("ICFP", new Pattern().add(new Pattern.Search(new Base[] { fuun.Base.F, fuun.Base.P })), true, "",
                new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Search(new Base[] { fuun.Base.I, fuun.Base.C, fuun.Base.F })),
                true, "P", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Search(new Base[] { fuun.Base.I })), true, "CFP",
                new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Search(new Base[] { fuun.Base.F, fuun.Base.F })), false, "ICFP",
                new ArrayList<>());
    }

    @Test
    void testEnv() {
        runTest("ICFP", new Pattern().add(new Pattern.Open()).add(new Pattern.Skip(1)).add(new Pattern.Close()), true,
                "CFP", List.of(fuun.Utils.stringToDNA("I")));
        runTest("ICFP",
                new Pattern().add(new Pattern.Skip(1)).add(new Pattern.Open()).add(new Pattern.Skip(2))
                        .add(new Pattern.Close()),
                true, "P", List.of(fuun.Utils.stringToDNA("CF")));
    }
}
