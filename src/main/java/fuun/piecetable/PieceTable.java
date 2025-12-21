package fuun.piecetable;

import java.util.Iterator;

import fuun.Base;
import fuun.DNA;
import fuun.DNACursor;

public class PieceTable implements fuun.DNA {
    protected Segment head = null;
    protected Segment tail = null;

    @Override
    public void append(Base[] bases) {
        if (bases.length == 0) {
            return;
        }
        append(new Buffer(bases, 0, bases.length - 1));
    }

    private Segment collapse(Segment start) {
        var size = 0;
        for (var seg = start; seg != null; seg = seg.getNext()) {
            size += seg.getBuffer().length();
        }

        var bases = new Base[size];
        var index = 0;
        for (var seg = start; seg != null; seg = seg.getNext()) {
            var buf = seg.getBuffer();
            System.arraycopy(buf.data(), buf.first(), bases, index, buf.length());
            index += buf.length();
        }

        return new Segment(new Buffer(bases, 0, bases.length - 1));
    }

    private Segment[] copySegments(Segment start) {
        var first = new Segment(start);
        var last = first;
        var segCount = 1;

        for (var seg = start.getNext(); seg != null; seg = seg.getNext()) {
            segCount++;
            last.setNext(new Segment(seg));
            last = last.getNext();
        }

        if (segCount > fuun.Utils.SEGMENT_LIMIT) {
            var seg = collapse(start);
            return new Segment[] { seg, seg };
        }

        return new Segment[] { first, last };
    }

    @Override
    public void append(DNA dna) {
        var toAppend = (PieceTable) dna;

        if (toAppend.head == null) {
            return;
        }

        var segs = copySegments(toAppend.head);

        if (tail == null) {
            head = segs[0];
            tail = segs[1];
        } else {
            tail.setNext(segs[0]);
            tail = segs[1];
        }
    }

    private void append(Buffer buf) {
        var seg = new Segment(buf);

        if (head == null) {
            head = seg;
        } else {
            tail.setNext(seg);
        }

        tail = seg;
    }

    @Override
    public int length() {
        var result = 0;

        for (var seg = head; seg != null; seg = seg.getNext()) {
            result += seg.getBuffer().length();
        }

        return result;
    }

    @Override
    public void prepend(DNA dna) {
        var toPrepend = (PieceTable) dna;
        if (toPrepend.head == null) {
            return;
        }

        var segs = copySegments(toPrepend.head);

        if (head == null) {
            head = segs[0];
            tail = segs[1];
        } else {
            segs[1].setNext(head);
            head = segs[0];
        }
    }

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        var startCursor = (Cursor) start;
        var endCursor = (Cursor) end;
        var dna = new PieceTable();
        var buf = startCursor.getCurSegment().getBuffer();

        if (startCursor.getCurSegment() == endCursor.getCurSegment()) {
            if (startCursor.getCurIndex() == endCursor.getCurIndex()) {
                return dna;
            }

            var endIndex = endCursor.getCurIndex() - 1;

            dna.append(new Buffer(buf.data(), buf.first() + startCursor.getCurIndex(), buf.first() + endIndex));

            return dna;
        }

        dna.append(new Buffer(buf.data(), buf.first() + startCursor.getCurIndex(), buf.last()));

        var seg = startCursor.getCurSegment().getNext();
        for (seg = startCursor.getCurSegment().getNext(); seg != endCursor.getCurSegment(); seg = seg.getNext()) {
            dna.append(seg.getBuffer());
        }

        if (seg != null && endCursor.getCurIndex() > 0) {
            buf = endCursor.getCurSegment().getBuffer();
            dna.append(new Buffer(buf.data(), buf.first(), buf.first() + endCursor.getCurIndex() - 1));
        }

        return dna;
    }

    @Override
    public Iterator<Base> iterator() {
        return new Cursor(this, head);
    }

    @Override
    public void truncate(DNACursor cursor) {
        var iter = (Cursor) cursor;
        var cursorSeg = iter.getCurSegment();

        if (cursorSeg == null) {
            head = null;
            tail = null;
            return;
        }

        var oldBuf = cursorSeg.getBuffer();
        var newBuf = new Buffer(oldBuf.data(), oldBuf.first() + iter.getCurIndex(), oldBuf.last());
        var seg = new Segment(newBuf);

        seg.setNext(cursorSeg.getNext());
        head = seg;

        if (head.getNext() == null) {
            tail = head;
        }
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        var loopCount = 0;

        for (var item : this) {
            fuun.Utils.checkLoopCount("PieceTable.tostring", loopCount++);
            builder.append(item);
        }

        return builder.toString();
    }
}
