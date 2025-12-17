package fuun.piecetable;

import fuun.Base;

public class Segment {
    private Base[] buffer;
    private int first;
    private int last;
    private Segment prev;
    private Segment next;

    public Segment(Segment copy) {
        this(copy.buffer, copy.first, copy.last);
    }

    public Segment(Base[] bases, int start, int end) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException();
        }

        buffer = bases;
        first = start;
        last = end;
    }

    public Base get(int index) {
        return (index >= first && index <= last) ? buffer[index] : Base.None;
    }

    public Base[] getBuffer() {
        return buffer;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public Segment getPrev() {
        return prev;
    }

    public void setPrev(Segment prev) {
        this.prev = prev;
    }

    public Segment getNext() {
        return next;
    }

    public void setNext(Segment next) {
        this.next = next;
    }
}
