package fuun.dna;

import java.util.List;
import java.util.Stack;

import fuun.DNACursor;

public class Matcher {
    public boolean match(fuun.DNA dna, fuun.dna.Pattern pattern, List<fuun.DNA> env) {
        var cursor = (DNACursor) dna.iterator();
        var cursorStack = new Stack<fuun.DNACursor>();

        for (var item : pattern.getItems()) {
            switch (item) {
                case Pattern.Base baseItem:
                    if (cursor.peek() == baseItem.base()) {
                        cursor.skip(1);
                    } else {
                        return false;
                    }
                    break;
                case Pattern.Skip skipItem:
                    cursor.skip(skipItem.offset());
                    if (!cursor.isValid()) {
                        return false;
                    }
                    break;
                case Pattern.Search searchItem: {
                    var newCursor = findPostfix(cursor, searchItem.srch());
                    if (newCursor == cursor) {
                        return false;
                    }
                    cursor = newCursor;
                    break;
                }
                case Pattern.Open openItem:
                    cursorStack.push(cursor.copy());
                    break;
                case Pattern.Close closeItem:
                    env.add(dna.slice(cursorStack.pop(), cursor));
                    break;
                default:
                    break;
            }
        }

        cursor.truncate();

        return true;
    }

    private int[] buildShiftTable(fuun.Base[] toFind) {
        var table = new int[toFind.length];
        var i = 1;
        var j = 0;
        var loopCount = 0;

        table[0] = 1;

        while (i + j < toFind.length) {
            fuun.Utils.checkLoopCount("buildShiftTable", loopCount++);

            if (toFind[i + j] == toFind[j]) {
                table[i + j] = 1;
                j += 1;
            } else {
                if (j == 0) {
                    table[i] = i + 1;
                }

                if (j > 0) {
                    i += table[j - 1];
                    j = Math.max(0, j - table[j - 1]);
                } else {
                    i += 1;
                    j = 0;
                }
            }
        }

        return table;
    }

    private fuun.DNACursor findPostfix(fuun.DNACursor cursor, fuun.Base[] toFind) {
        var shiftTable = buildShiftTable(toFind);
        var j = 0;
        var iCursor = cursor.copy();
        var jCursor = cursor.copy();
        var innerLoopCount = 0;
        var outerLoopCount = 0;

        while (iCursor.hasNext()) {
            fuun.Utils.checkLoopCount("findPostfix", outerLoopCount++);

            while (jCursor.peek() == toFind[j]) {
                fuun.Utils.checkLoopCount("findPostfix", innerLoopCount++);

                j += 1;
                jCursor.next();

                if (j >= toFind.length) {
                    return jCursor;
                }
            }

            if (j > 0) {
                iCursor.skip(shiftTable[j - 1]);

                j = Math.max(0, j - shiftTable[j - 1]);
                jCursor = iCursor.copy();
                jCursor.skip(j);
            } else {
                iCursor.next();
                jCursor = iCursor.copy();
            }
        }

        return cursor;
    }
}
