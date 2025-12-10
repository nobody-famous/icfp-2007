package fuun.dna;

import java.util.ArrayList;
import java.util.List;

public class Template {
    public static sealed interface Item permits Base, Protect, Length {
    }

    public static record Base(fuun.Base base) implements Item {
        @Override
        public final String toString() {
            return "" + base;
        }
    }

    public static record Protect(int reference, int level) implements Item {
        @Override
        public final String toString() {
            return "[" + reference + "_" + level + "]";
        }
    }

    public static record Length(int length) implements Item {
        @Override
        public final String toString() {
            return "|" + length + "|";
        }
    }

    private List<Item> items = new ArrayList<>();

    public Template add(Item item) {
        items.add(item);
        return this;
    }

    public List<Item> getItems() {
        return items;
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
