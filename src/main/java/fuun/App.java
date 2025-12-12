package fuun;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import fuun.dna.Matcher;
import fuun.dna.PatternDecoder;
import fuun.dna.Replacer;
import fuun.dna.TemplateDecoder;

public class App {
    private static final int MAX_ITERATIONS = 1;
    private static final int DEBUG_INTERVAL = 1;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new RuntimeException("Need to give a file name");
        }

        var contents = readFile(args[0]);
        var dna = fuun.Utils.stringToDNA(contents);

        execute(dna);
    }

    private static boolean doDebugPrint(int iteration) {
        return (iteration % DEBUG_INTERVAL == 0);
    }

    private static String readFile(String name) throws Exception {
        try (var reader = new BufferedReader(new FileReader(name))) {
            var builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        }
    }

    private static void execute(fuun.DNA dna) {
        try {
            for (var iteration = 0; iteration < MAX_ITERATIONS; iteration += 1) {
                if (doDebugPrint(iteration)) {
                    System.out.println("Iteration: " + iteration);
                    System.out.println("DNA: " + Utils.dnaToString(dna));
                }

                var pattern = new PatternDecoder().decode(dna);
                var template = new TemplateDecoder().decode(dna);

                if (doDebugPrint(iteration)) {
                    System.out.println("Pattern " + pattern);
                    System.out.println("Template " + template);
                }

                var env = new ArrayList<fuun.DNA>();
                if (new Matcher().match(dna, pattern, env)) {
                    if (doDebugPrint(iteration)) {
                        for (var index = 0; index < env.size(); index += 1) {
                            System.out.println("Env[" + index + "] " + Utils.dnaToString(env.get(index)));
                        }
                    }

                    var newDNA = new Replacer().replace(template, env);
                    dna.prepend(newDNA);
                }

                if (doDebugPrint(iteration)) {
                    System.out.println("--------------------");
                }
            }
        } catch (Finished ex) {
            System.out.println("Finished called " + ex);
        }
    }
}
