package fuun.dna;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class DecoderTest<T> {
    protected void runTest(Decoder<T> decoder, String startDNA, String expected) throws Exception {
        var dna = fuun.Utils.createDNA(fuun.Utils.stringToBases(startDNA));
        var cursor = dna.getCursor();
        var pattern = decoder.decode(cursor);

        assertEquals(expected, pattern.toString());
    }

    protected void testRNA(Decoder<T> decoder) throws Exception {
        runTest(decoder, "IIIIIIIIIIIIICCCCCCCIIIFFFFFFFIIIPPPPPPPIIC", "");
        assertEquals(4, decoder.getRNA().size());
        assertEquals("IIIIIII", fuun.Utils.basesToString(decoder.getRNA().get(0)));
        assertEquals("CCCCCCC", fuun.Utils.basesToString(decoder.getRNA().get(1)));
        assertEquals("FFFFFFF", fuun.Utils.basesToString(decoder.getRNA().get(2)));
        assertEquals("PPPPPPP", fuun.Utils.basesToString(decoder.getRNA().get(3)));
    }
}
