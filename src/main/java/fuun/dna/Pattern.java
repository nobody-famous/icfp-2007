package fuun.dna;

import java.util.ArrayList;
import java.util.List;

public class Pattern {
    public static sealed interface Item permits Base, Skip, Search, Open, Close {
    }

    public static record Base(fuun.Base base) implements Item {
        @Override
        public final String toString() {
            return "" + base;
        }
    }

    public static record Skip(int offset) implements Item {
        @Override
        public final String toString() {
            return "!" + offset;
        }
    }

    public static record Search(fuun.Base[] srch) implements Item {
        @Override
        public final String toString() {
            var builder = new StringBuilder();

            for (var base : srch) {
                builder.append(base);
            }

            return "<" + builder.toString() + ">";
        }
    }

    public static record Open() implements Item {
        @Override
        public final String toString() {
            return "(";
        }
    }

    public static record Close() implements Item {
        @Override
        public final String toString() {
            return ")";
        }
    }

    private List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(item);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        for (Item item : items) {
            builder.append(item);
        }

        return builder.toString();
    }
}
