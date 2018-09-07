package icfp.endo.rope;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RopeTest {
    @Test
    public void concat() {
        Rope rope = createAlphabet();

        assertEquals(alphabet, rope.toString());

        rope = createAlphabetChunked(4);
        assertEquals(alphabet, rope.toString());
    }

    @Test
    public void trunc() {
        Rope rope = new Rope();

        rope.concat("a");
        rope.trunc(1);
        assertEquals("", rope.toString());

        rope = createAlphabet();
        rope.trunc(2);
        assertEquals(alphabet.substring(2), rope.toString());

        rope = createAlphabetChunked(4);
        rope.trunc(2);
        assertEquals(alphabet.substring(2), rope.toString());
        rope.trunc(2);
        assertEquals(alphabet.substring(4), rope.toString());
    }

    @Test
    public void performance() {
        Rope rope = new Rope();
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i += 1) {
            rope.concat("a");
        }
        System.out.println("concat " + (System.currentTimeMillis() - start) / 1000.0);

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i += 1) {
            rope.trunc(1);
        }
        System.out.println("trunc " + (System.currentTimeMillis() - start) / 1000.0);
    }

    private static Rope createAlphabet() {
        Rope rope = new Rope();

        for (int ndx = 0; ndx < alphabet.length(); ndx += 1) {
            rope.concat("" + alphabet.charAt(ndx));
        }

        return rope;
    }

    private static Rope createAlphabetChunked(int chunkSize) {
        Rope rope = new Rope();

        for (int ndx = 0; ndx < alphabet.length(); ndx += chunkSize) {
            String str = (ndx + chunkSize) > alphabet.length() ? alphabet.substring(ndx)
                    : alphabet.substring(ndx, ndx + chunkSize);

            rope.concat(str);
        }

        return rope;
    }

    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";

}
