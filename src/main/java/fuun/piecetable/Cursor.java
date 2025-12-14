package fuun.piecetable;

import fuun.Base;
import fuun.DNACursor;

public class Cursor implements DNACursor {
    private Segment curSegment;
    private int index;

    public Cursor(Segment seg) {
        curSegment = seg;
        index = seg != null ? seg.getFirst() : 0;
    }

    Segment getSegment() {
        return curSegment;
    }

    int getIndex() {
        return index;
    }

    @Override
    public DNACursor copy() {
        throw new RuntimeException("Cursor.copy not done yet");
    }

    @Override
    public boolean hasNext() {
        return peek(0) != Base.None;
    }

    @Override
    public boolean isValid() {
        return curSegment != null && index >= curSegment.getFirst() && index <= curSegment.getLast();
    }

    @Override
    public Base next() {
        var result = peek(0);

        if (result != Base.None) {
            if (index + 1 > curSegment.getLast()) {
                curSegment = curSegment.getNext();
                index = (curSegment != null) ? curSegment.getFirst() : 0;
            } else {
                index++;
            }
        }

        return result;
    }

    @Override
    public Base peek() {
        return peek(0);
    }

    @Override
    public Base peek(int offset) {
        if (!isValid()) {
            return Base.None;
        }

        if (offset > 0 || index + offset > curSegment.getLast()) {
            throw new RuntimeException("Cursor.peek not done yet");
        }

        return isValid() ? curSegment.get(index + offset) : Base.None;
    }

    @Override
    public void skip(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        if (index + offset < curSegment.getLast()) {
            index += offset;
            return;
        }

        while (curSegment != null && index + offset > curSegment.getLast()) {
            offset -= (curSegment.getLast() - index + 1);
            curSegment = curSegment.getNext();
            index = curSegment != null ? curSegment.getFirst() : 0;
        }

        if (curSegment != null) {
            index = curSegment.getFirst() + offset;
        }
    }
}
