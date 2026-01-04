package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fuun.Utils;

public class MatcherTest {
    private void runTest(String input, Pattern pattern, boolean shouldMatch, String expectedDNA,
            List<fuun.DNA> expectedEnv) {
        var dna = fuun.Utils.createDNA(fuun.Utils.stringToBases(input));
        var env = new ArrayList<fuun.DNA>();
        var matched = new Matcher().match(dna, pattern, env);

        assertEquals(shouldMatch, matched);
        assertEquals(expectedDNA, dna.toString());
        assertEquals(expectedEnv.size(), env.size());
        for (var index = 0; index < env.size(); index += 1) {
            assertEquals(expectedEnv.get(index).toString(), env.get(index).toString());
        }
    }

    @Test
    void testBases() {
        runTest("ICFP", new Pattern().add(new Pattern.Base(Utils.stringToBases("ICFP"))), true, "", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Base(Utils.stringToBases("IFFP"))), false, "ICFP",
                new ArrayList<>());
    }

    @Test
    void testSkip() {
        runTest("ICFP", new Pattern().add(new Pattern.Skip(1)), true, "CFP", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Skip(3)), true, "P", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Skip(4)), true, "", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Skip(5)), false, "ICFP", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Skip(6)), false, "ICFP", new ArrayList<>());
    }

    @Test
    void testSearch() {
        runTest("ICFP", new Pattern().add(new Pattern.Search(Utils.stringToBases("FP"))), true, "", new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Search(Utils.stringToBases("ICF"))), true, "P",
                new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Search(Utils.stringToBases("I"))), true, "CFP",
                new ArrayList<>());
        runTest("ICFP", new Pattern().add(new Pattern.Search(Utils.stringToBases("FF"))), false, "ICFP",
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
        runTest("ICFP", new Pattern().add(new Pattern.Open()).add(new Pattern.Close()), true,
                "ICFP", List.of(fuun.Utils.stringToDNA("")));
    }
}
