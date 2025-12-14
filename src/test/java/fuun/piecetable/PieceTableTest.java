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
}
