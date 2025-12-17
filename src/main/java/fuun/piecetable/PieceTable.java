package fuun.piecetable;

import java.util.Iterator;

import fuun.Base;
import fuun.DNA;
import fuun.DNACursor;

public class PieceTable implements fuun.DNA {
    private Segment head = null;
    private Segment tail = null;

    @Override
    public void append(Base[] bases) {
        append(new Segment(bases, 0, bases.length - 1));
    }

    @Override
    public void append(DNA dna) {
        var toAppend = (PieceTable) dna;

        if (toAppend.head == null) {
            return;
        }

        if (head == null) {
            head = toAppend.head;
        } else {
            tail.setNext(toAppend.head);
        }

        tail = toAppend.tail;

        toAppend.head = null;
        toAppend.tail = null;
    }

    private void append(Segment seg) {
        if (head == null) {
            head = seg;
            tail = seg;
            return;
        }

        seg.setPrev(tail);
        tail.setNext(seg);
        tail = seg;
    }

    @Override
    public int length() {
        var result = 0;

        for (var seg = head; seg != null; seg = seg.getNext()) {
            result += (seg.getLast() - seg.getFirst() + 1);
        }

        return result;
    }

    @Override
    public void prepend(DNA dna) {
        var toPrepend = (PieceTable) dna;
        if (toPrepend.head == null) {
            return;
        }

        toPrepend.tail.setNext(head);
        head = toPrepend.head;
        if (tail == null) {
            tail = toPrepend.tail;
        }

        toPrepend.head = null;
        toPrepend.tail = null;
    }

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        var dna = new PieceTable();
        var startCursor = (Cursor) start;
        var endCursor = (Cursor) end;
        var curSeg = startCursor.getCurSegment();
        var endSeg = endCursor.getPrevSegment();
        var endIndex = endCursor.getPrevIndex();

        if (curSeg == null) {
            return dna;
        }

        dna.append(new Segment(
                curSeg.getBuffer(),
                startCursor.getCurIndex(),
                curSeg == endSeg ? endIndex : curSeg.getLast()));

        if (curSeg == endSeg) {
            return dna;
        }

        curSeg = curSeg.getNext();
        while (curSeg != null && curSeg != endSeg) {
            dna.append(new Segment(curSeg));
            curSeg = curSeg.getNext();
        }

        if (curSeg != null && curSeg == endSeg) {
            dna.append(new Segment(endSeg.getBuffer(), endSeg.getFirst(), endIndex));
        }

        return dna;
    }

    @Override
    public Iterator<Base> iterator() {
        return new Cursor(head);
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

        var seg = new Segment(cursorSeg.getBuffer(), iter.getCurIndex(), cursorSeg.getLast());

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
