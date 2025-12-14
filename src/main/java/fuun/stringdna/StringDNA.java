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
        public boolean hasNext() {
            return index >= 0 && index < bases.length;
        }

        @Override
        public Base next() {
            var base = isInRange(index) ? bases[index] : fuun.Base.None;
            index += 1;
            return base;
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
    }

    @Override
    public DNACursor iterator() {
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
    public void prepend(DNA dna) {
        var stringDNA = (StringDNA) dna;
        var newBases = new fuun.Base[stringDNA.bases.length + bases.length];

        System.arraycopy(stringDNA.bases, 0, newBases, 0, stringDNA.length());
        System.arraycopy(bases, 0, newBases, stringDNA.length(), bases.length);

        bases = newBases;
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
    public void truncate(DNACursor cursor) {
        var iter = (Cursor) cursor;

        if (iter.index >= bases.length) {
            bases = new Base[0];
            return;
        }

        var newLength = bases.length - iter.index;
        var newBases = new Base[newLength];

        System.arraycopy(bases, iter.index, newBases, 0, newLength);
        bases = newBases;
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
