package fuun.piecetable;

import fuun.Base;
import fuun.DNACursor;

public class Cursor implements DNACursor {
    private PieceTable table;
    private Segment curSegment;
    private int curIndex;
    private Segment prevSegment;

    public Cursor(Cursor copy) {
        this(copy.table, copy.curSegment, copy.curIndex, copy.prevSegment);
    }

    public Cursor(PieceTable table, Segment seg) {
        this(table, seg, 0, null);
    }

    public Cursor(PieceTable table, Segment curSeg, int curIndex, Segment prevSeg) {
        this.table = table;
        this.curSegment = curSeg;
        this.curIndex = curIndex;
        this.prevSegment = prevSeg;
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
        var curValid = curSegment != null && curSegment.get(curIndex) != Base.None;
        var prevValid = prevSegment == table.tail;
        return curValid || prevValid;
    }

    @Override
    public Base next() {
        var result = peek(0);

        if (result != Base.None) {
            if (curSegment.get(curIndex + 1) == Base.None) {
                prevSegment = curSegment;
                curSegment = curSegment.getNext();
                curIndex = 0;
            } else {
                curIndex++;
            }
        } else {
            prevSegment = null;
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
        var index = curIndex;
        while (seg != null && seg.get(index + offset) == Base.None) {
            offset -= (seg.getBuffer().length() - index);
            seg = seg.getNext();
            index = 0;
        }

        return seg != null ? seg.get(index + offset) : Base.None;
    }

    @Override
    public void skip(int offset) {
        if (offset < 0 || curSegment == null) {
            throw new IllegalArgumentException();
        }

        if (curIndex + offset < curSegment.getBuffer().length()) {
            curIndex += offset;
            prevSegment = curSegment;
            return;
        }

        while (curSegment != null && curSegment.get(curIndex + offset) == Base.None) {
            offset -= (curSegment.getBuffer().length() - curIndex);
            prevSegment = curSegment;
            curSegment = curSegment.getNext();
            curIndex = 0;
        }

        if (curSegment == null) {
            if (offset > 0) {
                prevSegment = null;
            }

            return;
        }

        curIndex = offset;
        if (curIndex > 0) {
            prevSegment = curSegment;
        }
    }
}
