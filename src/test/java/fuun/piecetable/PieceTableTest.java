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
}
