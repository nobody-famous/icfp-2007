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
        var seg = new Segment(bases, 0, bases.length - 1);

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
        throw new RuntimeException("PieceTable.slice not done yet");
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
