package fuun;

import java.util.ArrayList;

public final class Utils {
    private Utils() {
    }

    private static final int MAX_LOOP_COUNT = 100_000;

    public static DNA createDNA() {
        return new TableDNA();
    }

    public static DNA createDNA(Base[] bases) {
        var dna = createDNA();

        dna.append(bases);

        return dna;
    }

    public static Base[] stringToBases(String input) {
        var bases = new ArrayList<Base>();

        for (var ch : input.toCharArray()) {
            if (ch == 'I') {
                bases.add(Base.I);
            } else if (ch == 'C') {
                bases.add(Base.C);
            } else if (ch == 'F') {
                bases.add(Base.F);
            } else if (ch == 'P') {
                bases.add(Base.P);
            } else {
                throw new RuntimeException("stringToBases invalid char: " + ch);
            }
        }

        return bases.toArray(new Base[bases.size()]);
    }

    public static DNA stringToDNA(String input) {
        return createDNA(stringToBases(input));
    }

    public static String basesToString(Base[] bases) {
        var builder = new StringBuilder();

        for (var base : bases) {
            builder.append(base);
        }

        return builder.toString();
    }

    public static String dnaToString(fuun.DNA dna) {
        var builder = new StringBuilder();
        var cursor = dna.getCursor();

        builder.append("(" + dna.length() + ") ");
        for (var count = 0; count < 20 && cursor.peek() != fuun.Base.None; count += 1) {
            builder.append(cursor.next());
        }

        return builder.toString();
    }

    public static void checkLoopCount(String label, int count) {
        if (count >= MAX_LOOP_COUNT) {
            throw new RuntimeException(label + ": Infinite loop detected");
        }
    }
}
