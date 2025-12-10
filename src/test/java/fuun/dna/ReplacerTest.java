package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ReplacerTest {
    private void runTest(fuun.dna.Template template, List<fuun.DNA> env, String expected) {
        var result = new Replacer().replace(template, env);

        assertEquals(expected, result.toString());
    }

    @Test
    void testBases() {
        runTest(new Template()
                .add(new Template.Base(fuun.Base.I))
                .add(new Template.Base(fuun.Base.C))
                .add(new Template.Base(fuun.Base.F))
                .add(new Template.Base(fuun.Base.P)),
                List.of(),
                "ICFP");
    }
}
