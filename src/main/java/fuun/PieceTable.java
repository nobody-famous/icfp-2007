package fuun;

public class PieceTable<T> {
    public class Cursor {
        private PieceTable<T> table = null;
        private Segment seg = null;
        private int index;

        public Cursor(PieceTable<T> table, Segment seg) {
            this(table, seg, 0);
        }

        public Cursor(PieceTable<T> table, Segment seg, int index) {
            this.table = table;
            this.seg = seg;
            this.index = index;
        }

        public Cursor copy() {
            return new Cursor(table, seg, index);
        }

        public boolean isValid() {
            return seg != null && index >= 0 && index <= seg.end;
        }

        public T peek(int offset) {
            if (seg == null) {
                throw new IndexOutOfBoundsException();
            }

            var newOffset = seg.start + index + offset;

            if (newOffset < 0 || newOffset > seg.end) {
                throw new IndexOutOfBoundsException();
            }

            return seg.data[newOffset];
        }

        public T next() {
            var item = peek(0);

            index += 1;

            return item;
        }

        public void skip(int count) {
            var loopCounter = 0;

            while (index + count > seg.end) {
                fuun.Utils.checkLoopCount("skip", loopCounter++);
                count -= (seg.end - index);
                seg = seg.next;

                if (seg == null) {
                    index = 0;
                    return;
                }

                index = seg.start;
            }

            index += count;
        }

        public void truncate() {
            if (seg != null && index == seg.end + 1) {
                seg = seg.next;
                index = (seg != null) ? seg.start : 0;
            }

            if (table.head == null) {
                return;
            } else if (seg == null) {
                table.head = null;
                table.tail = null;
                return;
            }

            seg.start = index;
            seg.next = table.head.next;

            if (table.tail == table.head) {
                table.tail = seg;
            }

            table.head = seg;
        }
    }

    public Cursor getCursor() {
        return new Cursor(this, head);
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

    public int length() {
        var result = 0;
        var seg = head;

        while (seg != null) {
            result += (seg.end - seg.start + 1);
            seg = seg.next;
        }

        return result;
    }

    public void append(T[] items) {
        var segment = new Segment(items, 0, items.length - 1);

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
}
