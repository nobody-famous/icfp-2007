package fuun;

public class PieceTable<T> {
    private class Cursor {
    }

    public Cursor getCursor() {
        return new Cursor();
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
