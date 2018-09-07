package icfp.endo;

import icfp.endo.rope.Rope;

/***********************************************************************
 * Parser for the Endo DNA
 ***********************************************************************/
public class DNAParser {
    protected enum State {
        DONE, FAILED, EMPTY, I, II
    };

    public DNAParser(DNAConverter endo, Rope dna) {
        this.endo = endo;
        this.curState = State.EMPTY;
        this.dna = dna;
    }

    /***********************************************************************
     * Process the next character in the DNA
     * 
     * The language never needs to backtrack, so can be processed O(n)
     ***********************************************************************/
    public DNAToken step() {
        char ch = next();
        DNAToken token = DNAToken.DONE;

        switch (curState) {
        case DONE:
            return DNAToken.DONE;
        case FAILED:
            return DNAToken.FAILED;
        case EMPTY:
            return stepEmpty(ch);
        case I:
            return stepI(ch);
        case II:
            return stepII(ch);
        }

        return token;
    }

    /***********************************************************************
     * Read in a string constant, per the docs
     ***********************************************************************/
    public String consts() {
        StringBuilder builder = new StringBuilder();
        boolean done = false;

        while (!done) {
            if (dna.isEmpty()) {
                done = true;
            } else if (dna.get(0) == 'I') {
                if ((dna.getLength() == 1) || (dna.get(1) != 'C')) {
                    done = true;
                } else {
                    dna.trunc(2);
                    builder.append('P');
                }
            } else {
                switch (dna.pop()) {
                case 'C':
                    builder.append('I');
                    break;
                case 'F':
                    builder.append('C');
                    break;
                case 'P':
                    builder.append('F');
                    break;
                }
            }
        }

        return builder.toString();
    }

    /***********************************************************************
     * Read in a natural number, per the docs
     * 
     * The numbers are stored in binary, least significant bit first, with I or F
     * for 0's and C for 1's. The sequence ends with a P.
     ***********************************************************************/
    public int nat() {
        int n = 0;
        int shift = 0;

        boolean done = false;
        while (!done) {
            char ch = next();

            switch (ch) {
            case 'I':
            case 'F':
                shift += 1;
                break;
            case 'C':
                n += (1 << shift);
                shift += 1;
                break;
            case 'P':
                done = true;
                break;
            default:
                endo.finish();
                throw new RuntimeException("EndoParser.nat finish() was supposed to exit");
            }
        }

        return n;
    }

    public char next() {
        if (dna.isEmpty()) {
            curState = State.DONE;
            return '\0';
        }

        if ((dna.get(0) != 'C') && (dna.get(0) != 'I') && (dna.get(0) != 'F') && (dna.get(0) != 'P')) {
            System.out.println("Invalid character: " + dna.get(0));
            System.out.println("dna: " + dna.substring(0, 10).substring(0));
        }

        char ch = dna.pop();

        if ((ch != 'C') && (ch != 'I') && (ch != 'F') && (ch != 'P')) {
            System.out.println("Invalid character: " + ch);
            System.out.println("dna: " + dna.substring(0, 10).substring(0));
        }

        return ch;
    }

    protected State curState;

    private DNAConverter endo;
    private Rope dna;

    private DNAToken stepEmpty(char ch) {
        switch (ch) {
        case 'C':
            return DNAToken.C;
        case 'P':
            return DNAToken.P;
        case 'F':
            return DNAToken.F;
        case 'I':
            curState = State.I;
            return DNAToken.CONT;
        }

        System.out.println("DNAParser stepEmpty failed on " + ch);
        curState = State.FAILED;
        return DNAToken.FAILED;
    }

    private DNAToken stepI(char ch) {
        switch (ch) {
        case 'I':
            curState = State.II;
            return DNAToken.CONT;
        case 'C':
            curState = State.EMPTY;
            return DNAToken.IC;
        case 'P':
            curState = State.EMPTY;
            return DNAToken.IP;
        case 'F':
            curState = State.EMPTY;
            return DNAToken.IF;
        }

        System.out.println("DNAParser stepI failed on " + ch);
        curState = State.FAILED;
        return DNAToken.FAILED;
    }

    private DNAToken stepII(char ch) {
        switch (ch) {
        case 'I':
            curState = State.EMPTY;
            return DNAToken.III;
        case 'C':
            curState = State.EMPTY;
            return DNAToken.IIC;
        case 'P':
            curState = State.EMPTY;
            return DNAToken.IIP;
        case 'F':
            curState = State.EMPTY;
            return DNAToken.IIF;
        }

        System.out.println("DNAParser stepII failed on " + ch);
        curState = State.FAILED;
        return DNAToken.FAILED;
    }
}
