package fuun.dequedna;

import fuun.Base;
import fuun.DNACursor;

public class Cursor implements fuun.DNACursor {
    DequeDNA dna;
    int segIndex;
    int offset;

    Cursor(DequeDNA dna) {
        this.dna = dna;
        this.segIndex = dna.head;
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
        return dna.data[segIndex] != null && dna.data[segIndex].get(offset) != fuun.Base.None;
    }

    @Override
    public boolean isValid() {
        var curValid = dna.data[segIndex] != null && dna.data[segIndex].get(segIndex) != fuun.Base.None;
        var prevValid = offset == 0 && dna.data[dna.wrap(segIndex - 1)] != null;

        return curValid || prevValid;
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

        while (dna.data[curSegIndex] != null
                && dna.data[curSegIndex].first() + curOffset + count > dna.data[curSegIndex].last()) {
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

        System.out.println("***** SKIP BEFORE " + segIndex + " " + count + " " + offset);
        while (dna.data[segIndex] != null && dna.data[segIndex].get(offset + count) == Base.None) {
            fuun.Utils.checkLoopCount("skip", loopCount++);

            count -= (dna.data[segIndex].length() - offset);
            segIndex = dna.wrap(segIndex + 1);
            offset = dna.data[segIndex] != null ? dna.data[segIndex].first() : 0;
            System.out.println("***** SKIP LOOP " + segIndex + " " + count + " " + offset);
        }

        offset += count;
        System.out.println("***** SKIP AFTER " + segIndex + " " + count + " " + offset);
    }
}
