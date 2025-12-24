package fuun.dequedna;

import fuun.Base;
import fuun.DNACursor;

public class Cursor implements fuun.DNACursor {
    private DequeDNA dna;
    private int segIndex;
    private int offset;

    Cursor(DequeDNA dna) {
        this.dna = dna;
        this.segIndex = 0;
        this.offset = 0;
    }

    Cursor(DequeDNA dna, int segIndex, int offset) {
        this.dna = dna;
        this.segIndex = segIndex;
        this.offset = offset;
    }

    @Override
    public DNACursor copy() {
        return new Cursor(dna, segIndex, offset);
    }

    @Override
    public boolean hasNext() {
        throw new RuntimeException("hasNext not done yet");
    }

    @Override
    public boolean isValid() {
        throw new RuntimeException("isValid not done yet");
    }

    @Override
    public Base next() {
        var result = peek();

        skip(1);

        return result;
    }

    @Override
    public Base peek() {
        return peek(0);
    }

    @Override
    public Base peek(int offset) {
        throw new RuntimeException("peek not done yet");
    }

    @Override
    public void skip(int offset) {
        throw new RuntimeException("skip not done yet");
    }
}
