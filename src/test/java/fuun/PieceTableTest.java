package fuun;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;

public class PieceTableTest {
    private Character[] stringToChars(String str) {
        var chars = new Character[str.length()];

        for (var index = 0; index < str.length(); index += 1) {
            chars[index] = str.charAt(index);
        }

        return chars;
    }

    private PieceTable<Character> createTestTable() {
        var table = new PieceTable<Character>();

        table.append(stringToChars("abc"));
        table.append(stringToChars("def"));
        table.append(stringToChars("ghi"));

        return table;
    }

    private void runTruncate(int skip, String expected) {
        var table = createTestTable();
        var cursor = (PieceTable<Character>.Cursor) table.iterator();

        cursor.skip(skip);
        cursor.truncate();

        assertArrayEquals(stringToChars(expected), table.toList().toArray());
    }

    private void runSkip(int skip, Character expected) {
        var table = createTestTable();
        var cursor = (PieceTable<Character>.Cursor) table.iterator();

        cursor.skip(skip);

        assertEquals(expected, cursor.peek(0));
    }

    private void runSlice(int start, int end, String expected) {
        var table = createTestTable();
        var startCursor = (PieceTable<Character>.Cursor) table.iterator();
        var endCursor = (PieceTable<Character>.Cursor) table.iterator();

        startCursor.skip(start);
        endCursor.skip(end);

        var slice = table.slice(startCursor, endCursor);

        assertArrayEquals(stringToChars(expected), slice.toList().toArray());
    }

    @Test
    void appendTest() {
        var table = createTestTable();

        assertArrayEquals(stringToChars("abcdefghi"), table.toList().toArray());
    }

    @Test
    void skipTest() {
        runSkip(2, 'c');
        runSkip(3, 'd');
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> runSkip(9, '\0'));
        runSkip(5, 'f');
    }

    @Test
    void sliceTest() {
        runSlice(1, 6, "bcdef");
        runSlice(1, 1, "");
        runSlice(1, 2, "b");
        runSlice(0, 9, "abcdefghi");
        runSlice(0, 12, "abcdefghi");
        runSlice(5, 1, "fghi");
    }

    @Test
    void truncateTest() {
        runTruncate(2, "cdefghi");
        runTruncate(3, "defghi");
        runTruncate(9, "");
        runTruncate(5, "fghi");
    }
}
