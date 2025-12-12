package fuun;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PieceTable<T> implements Iterable<T> {
    public class Cursor implements Iterator<T> {
        private PieceTable<T> table = null;
        private Segment seg = null;
        private int index;

        public Cursor(PieceTable<T> table, Segment seg) {
            this(table, seg, seg == null ? 0 : seg.start);
        }

        public Cursor(PieceTable<T> table, Segment seg, int index) {
            this.table = table;
            this.seg = seg;
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return seg != null && seg.data != null && index >= 0 && index <= seg.end;
        }

        public Cursor copy() {
            return new Cursor(table, seg, index);
        }

        public boolean isValid() {
            return seg != null && index >= 0 && index <= seg.end;
        }

        public T peek(int offset) {
            if (seg == null || seg.data == null) {
                throw new IndexOutOfBoundsException();
            }

            var segOffset = index + offset;

            var loopCounter = 0;
            while (seg != null && segOffset > seg.end) {
                fuun.Utils.checkLoopCount("peek", loopCounter++);

                segOffset -= (seg.end - index);
                seg = seg.next;
            }

            if (seg == null || seg.data == null || segOffset >= seg.data.length) {
                throw new IndexOutOfBoundsException();
            }

            return seg.data[segOffset];
        }

        public T next() {
            var item = peek(0);

            index += 1;
            if (index > seg.end) {
                seg = seg.next;
                index = seg.start;
            }

            return item;
        }

        public void skip(int count) {
            var segOffset = index + count;

            while (seg != null && segOffset > seg.end) {
                segOffset -= (seg.end - index + 1);
                seg = seg.next;
                index = seg == null ? 0 : seg.start;
            }

            index = segOffset;
        }

        public void truncate() {
            if (seg == null || seg.data == null) {
                head = null;
                tail = null;
                return;
            }

            seg.start += index;
            head = seg;

            if (seg.next == null || seg.next.data == null) {
                tail = seg;
                seg.next = new Segment(null, 0, 0);
            }
        }
    }

    private class Segment {
        public T[] data;
        public int start;
        public int end;
        public Segment next;

        public Segment(T[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
            this.next = null;
        }

        public Segment copy() {
            return new Segment(this.data, this.start, this.end);
        }
    }

    private Segment head;
    private Segment tail;

    @Override
    public Iterator<T> iterator() {
        return new Cursor(this, head);
    }

    public int length() {
        var result = 0;
        var seg = head;

        while (seg != null && seg.data != null) {
            result += (seg.end - seg.start + 1);
            seg = seg.next;
        }

        return result;
    }

    public void append(T[] items) {
        var segment = new Segment(items, 0, items.length - 1);

        segment.next = new Segment(null, 0, 0);

        if (head == null) {
            head = segment;
            tail = segment;
            return;
        }

        tail.next = segment;
        tail = segment;
    }

    public PieceTable<T> slice(Cursor start, Cursor end) {
        var newTable = new PieceTable<T>();
        var curSeg = start.seg;
        var loopCounter = 0;

        while (curSeg != null) {
            fuun.Utils.checkLoopCount("slice", loopCounter++);

            if (curSeg == start.seg) {
                newTable.head = curSeg.copy();
                newTable.head.start += start.index;
                newTable.tail = newTable.head;
            }

            if (curSeg == end.seg) {
                newTable.tail.end = end.index - 1;
            }

            curSeg = curSeg.next;

            if (curSeg != null) {
                newTable.tail.next = curSeg.copy();
                newTable.tail = newTable.tail.next;
            }
        }

        return newTable;
    }

    public List<T> toList() {
        var items = new ArrayList<T>();

        for (var item : this) {
            items.add(item);
        }

        return items;
    }
}
