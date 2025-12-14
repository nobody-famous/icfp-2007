package fuun.piecetable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fuun.Base;

public class PieceTableTest {
    private PieceTable createTestTable() {
        var table = new PieceTable();

        table.append(new Base[] { Base.I, Base.C, Base.F, Base.P });
        table.append(new Base[] { Base.I, Base.C, Base.F, Base.P });
        table.append(new Base[] { Base.I, Base.C, Base.F, Base.P });

        return table;
    }

    @Test
    void testAppend() {
        var table = createTestTable();

        assertEquals("ICFPICFPICFP", table.toString());
    }

    private void runSkip(int offset, Base expected) {
        var table = createTestTable();
        var cursor = (Cursor) table.iterator();

        cursor.skip(offset);

        assertEquals(expected, cursor.peek());
    }

    @Test
    void testSkip() {
        runSkip(2, Base.F);
        runSkip(4, Base.I);
        runSkip(12, Base.None);
        runSkip(15, Base.None);
        runSkip(6, Base.F);
        runSkip(8, Base.I);
        runSkip(11, Base.P);
    }

    private void runSlice(int startSkip, int endSkip, String expected) {
        var table = createTestTable();
        var startCursor = (Cursor) table.iterator();
        var endCursor = (Cursor) table.iterator();

        startCursor.skip(startSkip);
        endCursor.skip(endSkip);

        assertEquals(expected, table.slice(startCursor, endCursor).toString());
    }

    @Test
    void testSlice() {
        runSlice(2, 6, "FPIC");
        runSlice(0, 4, "ICFP");
        runSlice(4, 8, "ICFP");
        runSlice(8, 12, "ICFP");
        runSlice(0, 8, "ICFPICFP");
        runSlice(0, 12, "ICFPICFPICFP");
        runSlice(2, 10, "FPICFPIC");
    }
}
