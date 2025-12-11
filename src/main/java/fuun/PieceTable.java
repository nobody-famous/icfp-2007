package fuun;

public class PieceTable<T> {
    public class Cursor {
        private Segment seg = null;
        private int index;

        public Cursor(Segment seg) {
            this(seg, 0);
        }

        public Cursor(Segment seg, int index) {
            this.seg = seg;
            this.index = index;
        }

        public Cursor copy() {
            return new Cursor(seg, index);
        }

        public boolean isValid() {
            return seg != null && index >= 0 && index <= seg.data.length;
        }

        public T peek(int offset) {
            var newOffset = index + offset;

            if (seg == null || newOffset < 0 || newOffset > seg.data.length) {
                throw new RuntimeException("Peek invalid cursor");
            }

            return seg.data[newOffset];
        }

        public T next() {
            var item = peek(0);

            index += 1;

            return item;
        }

        public void skip(int count) {
            while (index + count > seg.data.length) {
                count -= (seg.data.length - index);
                index = 0;
                seg = seg.next;
            }

            index += count;
        }

        public void truncate() {
            if (index == seg.data.length) {
                index = 0;
                seg = seg.next;
            }

            if (head == null) {
                return;
            } else if (seg == null) {
                head = null;
                tail = null;
                return;
            }

            seg.next = head.next;

            if (tail == head) {
                tail = seg;
            }
            head = seg;
        }
    }

    public Cursor getCursor() {
        return new Cursor(head);
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
}
