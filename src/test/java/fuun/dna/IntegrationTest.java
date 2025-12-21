package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class IntegrationTest {
    @Test
    void testFirstIter() {
        var patternInput = "IPPIIPIPIICPIIPIPICPIIFIPIICPIIFIIC";
        var templateInput = "IFPPIFPCPCICCICIIC";
        var dnaInput = "IIIICCFFFF";
        var input = "IIICCCCCCC" + patternInput + templateInput + dnaInput;
        var dna = fuun.Utils.createDNA(fuun.Utils.stringToBases(input));
        var pattern = new PatternDecoder().decode(dna);
        var template = new TemplateDecoder().decode(dna);

        assertEquals("!0(!4(!2)!4)", pattern.toString());
        assertEquals("[0_0][1_0]IPIP", template.toString());

        var env = new ArrayList<fuun.DNA>();
        var matched = new Matcher().match(dna, pattern, env);
        assertTrue(matched);
        assertEquals("CC", env.get(0).toString());
        assertEquals("IIIICCFFFF", env.get(1).toString());

        var toPrepend = new Replacer().replace(template, env);
        dna.prepend(toPrepend);

        assertEquals("CCIIIICCFFFFIPIP", dna.toString());
    }
}
