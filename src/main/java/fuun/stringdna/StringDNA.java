package fuun.stringdna;

import fuun.Base;
import fuun.DNA;
import fuun.DNACursor;

public class StringDNA implements DNA {
    private Base[] bases = new Base[0];

    public class Cursor implements DNACursor {
        private int index = 0;

        private boolean isValidOffset(int offset) {
            return offset >= 0 && offset < bases.length;
        }

        @Override
        public Base next() {
            return isValidOffset(index)
                    ? bases[index++]
                    : Base.None;
        }

        @Override
        public Base peek() {
            return peek(0);
        }

        @Override
        public Base peek(int offset) {
            var newOffset = this.index + offset;

            return isValidOffset(newOffset)
                    ? bases[newOffset]
                    : fuun.Base.None;
        }

        @Override
        public void skip(int offset) {
            index += offset;
        }

    }

    @Override
    public DNACursor getCursor() {
        return new Cursor();
    }

    @Override
    public int length() {
        return this.bases.length;
    }

    @Override
    public void append(Base[] toAppend) {
        var newBases = new Base[this.bases.length + toAppend.length];

        System.arraycopy(this.bases, 0, newBases, 0, this.bases.length);
        System.arraycopy(toAppend, 0, newBases, this.bases.length, toAppend.length);

        this.bases = newBases;
    }
}
