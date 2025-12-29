package fuun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Utils {
    private Utils() {
    }

    private static final int MAX_LOOP_COUNT = 100_000_000;
    // private static final int MAX_LOOP_COUNT = 100;
    public static final int SEGMENT_LIMIT = 500;

    public static DNA createDNA() {
        // return new TableDNA();
        return new fuun.piecetable.PieceTable();
        // return new StringDNA();
        // return new DequeDNA();
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
        var cursor = (DNACursor) dna.iterator();
        var size = dna.length();

        builder.append("(" + size + ") ");
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

    private static Map<String, Long> times = new HashMap<>();

    public static void updateTime(String label, long start) {
        times.put(label, times.getOrDefault(label, 0L) + (System.nanoTime() - start));
    }

    public static <T> T timer(String label, Supplier<T> fn) {
        var startTime = System.nanoTime();

        try {
            return fn.get();
        } finally {
            updateTime(label, startTime);
        }
    }

    public static void timer(String label, Runnable fn) {
        var startTime = System.nanoTime();

        try {
            fn.run();
        } finally {
            updateTime(label, startTime);
        }
    }

    public static void printTimes() {
        if (times.size() == 0) {
            return;
        }

        System.out.println("Times");
        for (var entry : times.entrySet()) {
            var value = String.format("%.3f", (entry.getValue() / 1_000_000_000.0));
            System.out.println("  " + entry.getKey() + ": " + value + " s");
        }
    }
}
