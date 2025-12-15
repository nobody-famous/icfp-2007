package fuun.piecetable;

import fuun.Base;
import fuun.DNACursor;

public class Cursor implements DNACursor {
    private Segment curSegment;
    private int index;

    public Cursor(Cursor copy) {
        this(copy.curSegment, copy.index);
    }

    public Cursor(Segment seg) {
        this(seg, seg != null ? seg.getFirst() : 0);
    }

    public Cursor(Segment seg, int index) {
        curSegment = seg;
        this.index = index;
    }

    Segment getSegment() {
        return curSegment;
    }

    int getIndex() {
        return index;
    }

    @Override
    public DNACursor copy() {
        return new Cursor(this);
    }

    @Override
    public boolean hasNext() {
        return peek() != Base.None;
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

        var seg = curSegment;
        while (seg != null && index + offset > seg.getLast()) {
            offset -= (seg.getLast() - index + 1);
            seg = seg.getNext();
            index = seg != null ? seg.getFirst() : 0;
        }

        return seg != null && index + offset <= seg.getLast() ? seg.get(index + offset) : Base.None;
    }

    @Override
    public void skip(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        if (index + offset <= curSegment.getLast()) {
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
