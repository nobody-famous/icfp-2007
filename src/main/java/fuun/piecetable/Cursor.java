package fuun.piecetable;

import fuun.Base;
import fuun.DNACursor;

public class Cursor implements DNACursor {
    private Segment curSegment;
    private int curIndex;
    private Segment prevSegment;
    private int prevIndex;

    public Cursor(Cursor copy) {
        this(copy.curSegment, copy.curIndex, copy.prevSegment, copy.prevIndex);
    }

    public Cursor(Segment seg) {
        this(seg, seg != null ? seg.getFirst() : 0, null, 0);
    }

    public Cursor(Segment curSeg, int curIndex, Segment prevSeg, int prevIndex) {
        this.curSegment = curSeg;
        this.curIndex = curIndex;
        this.prevSegment = prevSeg;
        this.prevIndex = prevIndex;
    }

    Segment getCurSegment() {
        return curSegment;
    }

    int getCurIndex() {
        return curIndex;
    }

    Segment getPrevSegment() {
        return prevSegment;
    }

    int getPrevIndex() {
        return prevIndex;
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
        var curValid = curSegment != null && curIndex >= curSegment.getFirst() && curIndex <= curSegment.getLast();
        var prevValid = prevSegment != null && prevIndex >= prevSegment.getFirst()
                && prevIndex <= prevSegment.getLast();
        return curValid || prevValid;
    }

    @Override
    public Base next() {
        var result = peek(0);

        if (result != Base.None) {
            if (curIndex + 1 > curSegment.getLast()) {
                prevSegment = curSegment;
                prevIndex = prevSegment.getLast();
                curSegment = curSegment.getNext();
                curIndex = (curSegment != null) ? curSegment.getFirst() : 0;
            } else {
                prevIndex = curIndex;
                curIndex++;
            }
        } else {
            prevSegment = null;
            prevIndex = 0;
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
        while (seg != null && curIndex + offset > seg.getLast()) {
            offset -= (seg.getLast() - curIndex + 1);
            seg = seg.getNext();
            curIndex = seg != null ? seg.getFirst() : 0;
        }

        return seg != null && curIndex + offset <= seg.getLast() ? seg.get(curIndex + offset) : Base.None;
    }

    @Override
    public void skip(int offset) {
        if (offset < 0 || curSegment == null) {
            throw new IllegalArgumentException();
        }

        if (curIndex + offset <= curSegment.getLast()) {
            curIndex += offset;
            prevIndex = curIndex - 1;
            return;
        }

        while (curSegment != null && curIndex + offset > curSegment.getLast()) {
            offset -= (curSegment.getLast() - curIndex + 1);
            prevSegment = curSegment;
            prevIndex = prevSegment.getLast();
            curSegment = curSegment.getNext();
            curIndex = curSegment != null ? curSegment.getFirst() : 0;
        }

        if (curSegment != null) {
            curIndex = curSegment.getFirst() + offset;
            if (curIndex > 0) {
                prevSegment = curSegment;
                prevIndex = curIndex - 1;
            } else {
                prevIndex = prevSegment.getLast();
            }
        }
    }
}
