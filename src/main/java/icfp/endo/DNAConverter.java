package icfp.endo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import icfp.endo.rope.Rope;

/***********************************************************************
 * DNA Converter
 ***********************************************************************/
public class DNAConverter {
    public DNAConverter(String dna) {
        this.dna = new Rope(dna);

        this.rna = new Rope();
        this.parser = new DNAParser(this, this.dna);
    }

    /***********************************************************************
     * Run the machine, per the docs
     ***********************************************************************/
    public void execute() {
        int loop = 0;

        while (true) {
            List<PatternItem> pattern;
            List<TemplItem> template;

            if (loop % 1000 == 0) {
                System.out.println("Loop " + loop);
                System.out.println(" rna: " + rna.getLength());
            }

            long start = System.currentTimeMillis();
            pattern = pattern();
            System.out.println("pattern time: " + (System.currentTimeMillis() - start) / 1000.0);
            start = System.currentTimeMillis();
            template = template();
            System.out.println("template time: " + (System.currentTimeMillis() - start) / 1000.0);

            System.out.println("pattern: " + patternToString(pattern));
            System.out.println("template: " + templateToString(template));

            matchreplace(pattern, template);

            loop += 1;
        }
    }

    /***********************************************************************
     * Specify the file to write the final RNA to
     ***********************************************************************/
    public void setOutputFile(File out) {
        rnaFile = out;
    }

    /***********************************************************************
     * Parse a patter, per the docs
     ***********************************************************************/
    public List<PatternItem> pattern() {
        List<PatternItem> out = new ArrayList<>();
        int level = 0;
        int n = 0;
        String c;

        boolean done = false;
        while (!done) {
            DNAToken token = parser.step();

            switch (token) {
            case CONT:
                break;
            case C:
                out.add(new PatternItem(PatternItem.Type.BASE, 'I'));
                break;
            case F:
                out.add(new PatternItem(PatternItem.Type.BASE, 'C'));
                break;
            case P:
                out.add(new PatternItem(PatternItem.Type.BASE, 'F'));
                break;
            case IC:
                out.add(new PatternItem(PatternItem.Type.BASE, 'P'));
                break;
            case IP:
                n = parser.nat();
                out.add(new PatternItem(PatternItem.Type.JUMP, n));
                break;
            case IF:
                parser.next(); // Consume one more character for no good reason
                c = parser.consts();
                out.add(new PatternItem(PatternItem.Type.SEARCH, c));
                break;
            case IIP:
                level += 1;
                out.add(new PatternItem(PatternItem.Type.OPEN));
                break;
            case IIF:
            case IIC:
                if (level == 0) {
                    done = true;
                } else {
                    level -= 1;
                    out.add(new PatternItem(PatternItem.Type.CLOSE));
                }
                break;
            case III:
                rna.concat(dna.substring(0, 7));
                dna.trunc(7);
                break;
            default:
                finish();
            }
        }

        return out;
    }

    /***********************************************************************
     * Parse a template, per the docs
     ***********************************************************************/
    public List<TemplItem> template() {
        List<TemplItem> items = new ArrayList<>();
        int l;
        int n;

        boolean done = false;
        while (!done) {
            DNAToken token = parser.step();

            switch (token) {
            case CONT:
                break;
            case C:
                items.add(new TemplItem(TemplItem.Type.BASE, 'I'));
                break;
            case F:
                items.add(new TemplItem(TemplItem.Type.BASE, 'C'));
                break;
            case P:
                items.add(new TemplItem(TemplItem.Type.BASE, 'F'));
                break;
            case IC:
                items.add(new TemplItem(TemplItem.Type.BASE, 'P'));
                break;
            case IP:
            case IF:
                l = parser.nat();
                n = parser.nat();
                items.add(new TemplItem(TemplItem.Type.PROT, n, l));
                break;
            case IIP:
                n = parser.nat();
                items.add(new TemplItem(TemplItem.Type.LEN, n));
                break;
            case IIF:
            case IIC:
                done = true;
                break;
            case III:
                rna.concat(dna.substring(0, 7));
                dna.trunc(7);
                break;
            case DONE:
                done = true;
                break;
            default:
                finish();
            }
        }

        return items;
    }

    /***********************************************************************
     * Protect a string, per the docs
     ***********************************************************************/
    private Rope protect(int lvl, Rope d) {
        if (lvl == 0) {
            return d.substring(0);
        }

        int size = d.getLength();
        int bufSize = size * 2;

        char[] scratch = new char[bufSize];
        char[] out = new char[bufSize];
        for (int ndx = 0; ndx < d.getLength(); ndx += 1) {
            out[ndx] = d.get(ndx);
        }

        for (int loop = 0; loop < lvl; loop += 1) {
            int outNdx = 0;

            char[] tmp = scratch;
            scratch = out;
            out = tmp;

            if (out.length < scratch.length) {
                out = new char[bufSize];
            }

            for (int ndx = 0; ndx < size; ndx += 1) {
                if (scratch[ndx] == 'I') {
                    out[outNdx] = 'C';
                    outNdx += 1;
                } else if (scratch[ndx] == 'C') {
                    out[outNdx] = 'F';
                    outNdx += 1;
                } else if (scratch[ndx] == 'F') {
                    out[outNdx] = 'P';
                    outNdx += 1;
                } else if (scratch[ndx] == 'P') {
                    out[outNdx] = 'I';
                    outNdx += 1;
                    out[outNdx] = 'C';
                    outNdx += 1;
                }
            }

            size = outNdx;
            if (bufSize < (size * 2)) {
                bufSize *= 2;
                scratch = new char[bufSize];
            }
        }

        return new Rope(new String(out).trim());
    }

    /***********************************************************************
     * Encode the given number, per the docs
     ***********************************************************************/
    private String asnat(int n) {
        char ch;

        if (n == 0) {
            return "IP";
        }

        // Encode the number as binary, least significant digit first, with I for 0's
        // and C for 1's. Terminates with a P
        // i.e. 6 => ICCP
        int bitCount = 0;
        for (int tmp = 1; tmp <= n; tmp <<= 1) {
            bitCount += 1;
        }

        // This whole thing is memory intensive, so try to use as little as possible.
        // This could be done with a StringBuilder, but don't want to risk the memory
        // usage. Ergo, create a buffer with enough slots for all the digits and fill
        // that buffer as we go.
        char[] buffer = new char[bitCount + 1];
        int ndx = 0;
        while (n > 0) {
            ch = ((n & 1) == 1) ? 'C' : 'I';

            buffer[ndx] = ch;
            ndx += 1;

            n /= 2;
        }
        buffer[ndx] = 'P';

        String nat = new String(buffer);
        return nat;
    }

    /***********************************************************************
     * Use the template and given environment list to replace parts of the DNA, per
     * the docs
     ***********************************************************************/
    public void replace(List<TemplItem> template, List<Rope> env) {
        Rope r = new Rope();
        // StringBuilder bases = new StringBuilder();
        // Stack<DNAChunk> r = new Stack<>();
        int ndx;
        int len;

        for (TemplItem item : template) {
            switch (item.getType()) {
            case BASE:
                // bases.append((char) item.getValue());
                // r.push(new StringChunk("" + (char) item.getValue()));
                r.concat(new Rope("" + (char) item.getValue()));
                break;
            case PROT:
                // if (bases.length() > 0) {
                // r.add(new StringChunk(bases.toString()));
                // bases = new StringBuilder();
                // }
                ndx = item.getValue();
                if (ndx < env.size()) {
                    if (item.getProtLevel() == 0) {
                        Rope envRope = env.get(item.getValue());
                        r.concat(envRope);
                        // r.append(env.get(item.getValue()).substring(0));
                        // r.add(env.get(item.getValue()));
                    } else {
                        Rope envStr = protect(item.getProtLevel(), env.get(item.getValue()).substring(0));
                        // System.out.println(
                        // " prot(" + item.getProtLevel() + ") " + env.get(item.getValue()).substring(0)
                        // + " => " + envStr);

                        r.concat(envStr);
                        // r.add(new StringChunk(envStr));
                    }
                    // if (item.getProtLevel() < 1) {
                    // r.add(env.get(item.getValue()));
                    // } else {
                    // System.out.println("Protection level " + item.getProtLevel());
                    // DNAChunk envStr = env.get(item.getValue());
                    // String prot = protect(item.getProtLevel(), envStr.substring(0));
                    // r.add(new StringChunk(prot));
                    // }
                }
                break;
            case LEN:
                // if (bases.length() > 0) {
                // r.add(new StringChunk(bases.toString()));
                // bases = new StringBuilder();
                // }
                ndx = item.getValue();
                if (ndx < env.size()) {
                    Rope envStr = env.get(item.getValue()).substring(0);
                    len = envStr.getLength();
                } else {
                    len = 0;
                }
                // r.add(new StringChunk(asnat(len)));
                r.concat(new Rope(asnat(len)));
                break;
            default:
                throw new RuntimeException("replace unhandled template " + item.getType());
            }
        }
        // if (bases.length() > 0) {
        // r.add(new StringChunk(bases.toString()));
        // }

        if (r.getLength() > 0) {
            dna.prepend(r);
        }
        // while (!r.empty()) {
        // DNAChunk chunk = r.pop();

        // if (chunk.size() > 10) {
        // System.out.println(" prepend " + chunk.substring(0, 10) + "...");
        // } else {
        // System.out.println(" prepend " + chunk.substring(0));
        // }
        // dna.prepend(chunk);
        // }
    }

    /***********************************************************************
     * Match the DNA against the given pattern and then call replace with the given
     * template, per the docs
     ***********************************************************************/
    public void matchreplace(List<PatternItem> pattern, List<TemplItem> template) {
        int i = 0;
        int ndx = 0;
        int srch = 0;
        Rope matched;
        List<Rope> env = new ArrayList<>();
        Stack<Integer> c = new Stack<>();

        for (PatternItem item : pattern) {
            switch (item.getType()) {
            case BASE:
                if (dna.get(i) == item.getValue()) {
                    i += 1;
                } else {
                    return;
                }
                break;
            case JUMP:
                i += item.getValue();
                if (!dna.isValidIndex(i - 1)) {
                    return;
                }
                break;
            case OPEN:
                c.push(i);
                break;
            case CLOSE:
                ndx = c.pop();
                matched = dna.substring(ndx, i);
                env.add(matched);
                break;
            case SEARCH:
                srch = dna.search(item.getSearch());
                i = srch;
                break;
            default:
                // The docs don't say what to do here, so bail out
                throw new RuntimeException("matchreplace unhandled pattern " + item.getType());
            }
        }

        dna.trunc(i);
        replace(template, env);
    }

    /***********************************************************************
     * Finish processing. Write the RNA to a file and exit, per the docs
     ***********************************************************************/
    public void finish() {
        if (rnaFile != null) {
            try {
                FileWriter writer = new FileWriter(rnaFile);
                writer.write(rna.toString());
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        System.exit(0);
    }

    /***********************************************************************
     * Get the current DNA as a string. Useful for testing purposes.
     ***********************************************************************/
    public String getDNA() {
        return dna.toString();
    }

    /***********************************************************************
     * Helper function to print a template in an easier format
     ***********************************************************************/
    public String templateToString(List<TemplItem> list) {
        StringBuilder builder = new StringBuilder();

        for (TemplItem item : list) {
            switch (item.getType()) {
            case BASE:
                builder.append((char) item.getValue());
                break;
            case PROT:
                builder.append('[').append(item.getValue()).append('_').append(item.getProtLevel()).append(']');
                break;
            case LEN:
                builder.append('|').append(item.getValue()).append('|');
                break;
            }
        }

        return builder.toString();
    }

    /***********************************************************************
     * Helper function to print a pattern in a more readable format
     ***********************************************************************/
    public String patternToString(List<PatternItem> list) {
        StringBuilder builder = new StringBuilder();

        for (PatternItem item : list) {
            switch (item.getType()) {
            case BASE:
                builder.append((char) item.getValue());
                break;
            case JUMP:
                builder.append('!').append(item.getValue());
                break;
            case SEARCH:
                builder.append('[').append('?').append(item.getSearch()).append(']');
                break;
            case OPEN:
                builder.append('(');
                break;
            case CLOSE:
                builder.append(')');
                break;
            }
        }

        return builder.toString();
    }

    private Rope dna;
    private Rope rna;
    private File rnaFile;
    private DNAParser parser;

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Need to specify three files: in.dna in.suffix rna.out");
            System.exit(0);
        }

        File inDNA = new File(args[0]);
        File inPrefix = new File(args[1]);
        File outRNA = new File(args[2]);

        StringBuilder builder = new StringBuilder();

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(inDNA));
        BufferedInputStream inPref = new BufferedInputStream(new FileInputStream(inPrefix));

        byte[] prefBuffer = new byte[(int) inPrefix.length()];
        inPref.read(prefBuffer);
        byte[] buffer = new byte[(int) inDNA.length()];
        in.read(buffer);

        inPref.close();
        in.close();

        builder.append(new String(prefBuffer).trim());
        builder.append(new String(buffer).trim());
        String dna = builder.toString();

        DNAConverter endo = new DNAConverter(dna);

        endo.setOutputFile(outRNA);
        endo.execute();
    }
}
