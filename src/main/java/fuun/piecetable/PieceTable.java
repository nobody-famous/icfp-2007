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
        throw new RuntimeException("PieceTable.length not done yet");
    }

    @Override
    public void prepend(DNA dna) {
        throw new RuntimeException("PieceTable.prepend not done yet");
    }

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        var dna = new PieceTable();
        var startCursor = (Cursor) start;
        var endCursor = (Cursor) end;
        var curSeg = startCursor.getSegment();

        dna.append(new Segment(
                curSeg.getBuffer(),
                startCursor.getIndex(),
                startCursor.getSegment() == endCursor.getSegment() ? endCursor.getIndex() - 1 : curSeg.getLast()));

        if (startCursor.getSegment() == endCursor.getSegment()) {
            return dna;
        }

        curSeg = curSeg.getNext();
        while (curSeg != null && curSeg != endCursor.getSegment()) {
            dna.append(new Segment(curSeg));
            curSeg = curSeg.getNext();
        }

        if (curSeg != null && curSeg == endCursor.getSegment()) {
            dna.append(new Segment(curSeg.getBuffer(), curSeg.getFirst(), endCursor.getIndex() - 1));
        }

        return dna;
    }

    @Override
    public Iterator<Base> iterator() {
        return new Cursor(head);
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
