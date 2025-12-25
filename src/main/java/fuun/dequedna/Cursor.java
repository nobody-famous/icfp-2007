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
    public Base peek(int count) {
        var curOffset = this.offset;
        var curSegIndex = segIndex;

        while (dna.data[curSegIndex] != null && curOffset + count > dna.data[curSegIndex].length()) {
            count -= dna.data[curSegIndex].length() - curOffset;
            curOffset = 0;
            curSegIndex = dna.wrap(curSegIndex + 1);
        }

        return dna.data[curSegIndex] != null
                ? dna.data[curSegIndex].get(curOffset + count)
                : Base.None;
    }

    @Override
    public void skip(int count) {
        var loopCount = 0;

        while (dna.data[segIndex] != null && offset + count > dna.data[segIndex].length()) {
            fuun.Utils.checkLoopCount("skip", loopCount++);

            count -= dna.data[segIndex].length() - offset;
            offset = 0;
            segIndex = dna.wrap(segIndex + 1);
        }

        offset += count;
    }
}
