package fuun.dequedna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import fuun.Base;
import fuun.Utils;
import fuun.DNACursor;

public class DequeDNATest {

    @Test
    public void testAppendCollapse() {
        var dna = new DequeDNA();
        // data.length is 512. head=0, tail=0.
        // We can append 511 times before it needs to collapse.
        for (int i = 0; i < 511; i++) {
            dna.append(new Base[]{Base.I});
        }
        assertEquals(511, dna.length());
        
        // This 512th append should trigger collapse
        dna.append(new Base[]{Base.C});
        assertEquals(512, dna.length());
        
        var result = Utils.dnaToString(dna);
        // dnaToString prints (size) and first 20 bases
        assertEquals("(512) IIIIIIIIIIIIIIIIIIII", result);
    }

    @Test
    public void testPrependCollapse() {
        var dna = new DequeDNA();
        for (int i = 0; i < 511; i++) {
            var other = new DequeDNA();
            other.append(new Base[]{Base.I});
            dna.prepend(other);
        }
        assertEquals(511, dna.length());
        
        // This 512th prepend should trigger collapse
        var other = new DequeDNA();
        other.append(new Base[]{Base.C});
        dna.prepend(other);
        assertEquals(512, dna.length());
        
        var result = Utils.dnaToString(dna);
        assertEquals("(512) CIIIIIIIIIIIIIIIIIII", result);
    }

    @Test
    public void testMultipleCollapses() {
        var dna = new DequeDNA();
        // Append 2000 times, should trigger collapse ~4 times (511 * 4 = 2044)
        for (int i = 0; i < 2000; i++) {
            dna.append(new Base[]{Base.I});
        }
        assertEquals(2000, dna.length());
        
        // Prepend 2000 times, should trigger collapse ~4 times
        for (int i = 0; i < 2000; i++) {
            var other = new DequeDNA();
            other.append(new Base[]{Base.C});
            dna.prepend(other);
        }
        assertEquals(4000, dna.length());

        var result = Utils.dnaToString(dna);
        // Should start with 2000 Cs and then 2000 Is
        assertEquals("(4000) CCCCCCCCCCCCCCCCCCCC", result);
    }

    @Test
    public void testTruncate() {
        var dna = new DequeDNA();
        dna.append(new Base[]{Base.I, Base.C, Base.F, Base.P});
        var cursor = (DNACursor) dna.iterator();
        cursor.skip(2); // Point to F
        dna.truncate(cursor);
        assertEquals(2, dna.length());
        assertEquals("(2) FP", Utils.dnaToString(dna));
    }

    @Test
    public void testSliceAndCollapse() {
        var dna = new DequeDNA();
        for (int i = 0; i < 1000; i++) {
            dna.append(new Base[]{Base.I});
        }
        
        // Slice a part of it
        var cursor1 = (DNACursor) dna.iterator();
        cursor1.skip(100);
        var cursor2 = (DNACursor) dna.iterator();
        cursor2.skip(900);
        var slice = dna.slice(cursor1, cursor2);
        assertEquals(800, slice.length());
        
        // Append the slice back to a new DNA many times to trigger collapse in the new DNA
        var result = new DequeDNA();
        for (int i = 0; i < 10; i++) {
            result.append(slice);
        }
        assertEquals(8000, result.length());
        
        var resultStr = Utils.dnaToString(result);
        assertEquals("(8000) IIIIIIIIIIIIIIIIIIII", resultStr);
    }

    @Test
    public void testSelfAppend() {
        var dna = new DequeDNA();
        for (int i = 0; i < 300; i++) {
            dna.append(new Base[]{Base.I});
        }
        assertEquals(300, dna.length());
        
        // This should trigger collapse during the loop
        dna.append(dna);
        assertEquals(600, dna.length());
        
        var resultStr = Utils.dnaToString(dna);
        assertEquals("(600) IIIIIIIIIIIIIIIIIIII", resultStr);
    }
}
