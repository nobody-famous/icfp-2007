package fuun.stringdna;

import fuun.Base;
import fuun.DNA;
import fuun.DNACursor;

public class StringDNA implements DNA {
    private Base[] bases = new Base[0];

    public class Cursor implements DNACursor {
        private int index = 0;

        private boolean isInRange(int offset) {
            return offset >= 0 && offset < bases.length;
        }

        @Override
        public DNACursor copy() {
            var newCursor = new Cursor();

            newCursor.index = this.index;

            return newCursor;
        }

        @Override
        public boolean isValid() {
            return index >= 0 && index <= bases.length;
        }

        @Override
        public Base next() {
            return isInRange(index)
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

            return isInRange(newOffset)
                    ? bases[newOffset]
                    : fuun.Base.None;
        }

        @Override
        public void skip(int offset) {
            index += offset;
        }

        @Override
        public void truncate() {
            if (index >= bases.length) {
                bases = new Base[0];
                return;
            }

            var newLength = bases.length - index;
            var newBases = new Base[newLength];

            System.arraycopy(bases, index, newBases, 0, newLength);
            bases = newBases;
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

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        var startCursor = (Cursor) start;
        var endCursor = (Cursor) end;
        var length = endCursor.index - startCursor.index;
        var newBases = new Base[length];

        System.arraycopy(bases, startCursor.index, newBases, 0, length);

        var dna = new StringDNA();
        dna.bases = newBases;

        return dna;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        for (var base : bases) {
            builder.append(base);
        }

        return builder.toString();
    }
}
