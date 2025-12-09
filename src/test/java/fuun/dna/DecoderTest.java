package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fuun.Finished;

abstract class DecoderTest<T> {
    void runTest(Decoder<T> decoder, String startDNA, String expected) throws Exception {
        var dna = fuun.Utils.createDNA(fuun.Utils.stringToBases(startDNA));
        var cursor = dna.getCursor();
        var pattern = decoder.decode(cursor);

        assertEquals(expected, pattern.toString());
    }

    void testBases(Decoder<T> decoder) throws Exception {
        assertThrows(Finished.class, () -> runTest(decoder, "CFPIC", "ICFP"));
        runTest(decoder, "CFPICIIC", "ICFP");
    }

    void testRNA(Decoder<T> decoder) throws Exception {
        runTest(decoder, "IIIIIIIIIIIIICCCCCCCIIIFFFFFFFIIIPPPPPPPIIC", "");
        assertEquals(4, decoder.getRNA().size());
        assertEquals("IIIIIII", fuun.Utils.basesToString(decoder.getRNA().get(0)));
        assertEquals("CCCCCCC", fuun.Utils.basesToString(decoder.getRNA().get(1)));
        assertEquals("FFFFFFF", fuun.Utils.basesToString(decoder.getRNA().get(2)));
        assertEquals("PPPPPPP", fuun.Utils.basesToString(decoder.getRNA().get(3)));
    }
}
