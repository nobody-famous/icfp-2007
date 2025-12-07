package fuun.dna;

import java.util.ArrayList;
import java.util.List;

import fuun.DNACursor;

public class Pattern {
    public static sealed interface Item permits Base, Skip, Search {
    }

    public static record Base(char base) implements Item {
    }

    public static record Skip(int offset) implements Item {
    }

    public static record Search(String srch) implements Item {
    }

    private DNACursor cursor;

    public Pattern(DNACursor cursor) {
        this.cursor = cursor;
    }

    public List<Item> decode() {
        var items = new ArrayList<Item>();

        return items;
    }
}
