package fuun.dna;

import java.util.List;

public class Matcher {
    public boolean match(fuun.DNA dna, fuun.dna.Pattern pattern, List<fuun.DNA> env) {
        var cursor = dna.getCursor();

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
                    if (!findPostfix(cursor, searchItem.srch())) {
                        return false;
                    }
                    break;
                }
                default:
                    break;
            }
        }

        cursor.truncate();

        return true;
    }

    private boolean findPostfix(fuun.DNACursor cursor, fuun.Base[] toFind) {
        return false;
    }
}
