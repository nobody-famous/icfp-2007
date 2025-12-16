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

        table = new PieceTable();

        table.append(fuun.Utils.stringToDNA("IIII"));
        table.append(fuun.Utils.stringToDNA("CCCC"));
        table.append(fuun.Utils.stringToDNA("FFFF"));
        table.append(fuun.Utils.stringToDNA("PPPP"));
        table.append(createTestTable());

        assertEquals("IIIICCCCFFFFPPPPICFPICFPICFP", table.toString());
    }

    @Test
    void testPrepend() {
        var table = new PieceTable();

        table.prepend(fuun.Utils.stringToDNA("PPPP"));
        table.prepend(fuun.Utils.stringToDNA("FFFF"));
        table.prepend(fuun.Utils.stringToDNA("CCCC"));
        table.prepend(fuun.Utils.stringToDNA("IIII"));

        assertEquals("IIIICCCCFFFFPPPP", table.toString());

        table = new PieceTable();
        table.prepend(fuun.Utils.stringToDNA("ICFP"));

        assertEquals("ICFP", table.toString());
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

    private void runTruncate(int offset, String expected) {
        var table = new PieceTable();

        table.append(new Base[] { Base.I, Base.I, Base.I, Base.I });
        table.append(new Base[] { Base.C, Base.C, Base.C, Base.C });
        table.append(new Base[] { Base.F, Base.F, Base.F, Base.F });
        table.append(new Base[] { Base.P, Base.P, Base.P, Base.P });

        var cursor = (Cursor) table.iterator();

        cursor.skip(offset);
        table.truncate(cursor);

        assertEquals(expected, table.toString());
    }

    @Test
    void testTruncate() {
        runTruncate(1, "IIICCCCFFFFPPPP");
        runTruncate(4, "CCCCFFFFPPPP");
        runTruncate(6, "CCFFFFPPPP");
        runTruncate(8, "FFFFPPPP");
        runTruncate(10, "FFPPPP");
        runTruncate(12, "PPPP");
        runTruncate(14, "PP");
        runTruncate(16, "");
        runTruncate(18, "");
    }
}
