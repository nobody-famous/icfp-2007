package fuun.dna;

import org.junit.jupiter.api.Test;

public class TemplateDecoderTest extends DecoderTest<Template> {
    @Test
    public void basesTest() throws Exception {
        testBases(new TemplateDecoder());
    }

    @Test
    public void protectTest() throws Exception {
        runTest(new TemplateDecoder(), "IFPPIIC", "[0_0]");
        runTest(new TemplateDecoder(), "IFICPCCPIIC", "[3_2]");
        runTest(new TemplateDecoder(), "IPCCPCCCPIIC", "[7_3]");
    }

    @Test
    public void lengthTest() throws Exception {
        runTest(new TemplateDecoder(), "IIPCCPIIC", "|3|");
    }

    @Test
    public void rnaTest() throws Exception {
        testRNA(new TemplateDecoder());
    }
}
