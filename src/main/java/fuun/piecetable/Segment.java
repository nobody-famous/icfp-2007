package fuun.piecetable;

import fuun.Base;
import fuun.utils.Buffer;

public class Segment {
    private Buffer buffer;
    private Segment prev;
    private Segment next;

    public Segment(Segment copy) {
        this(copy.buffer);
    }

    public Segment(Buffer buffer) {
        this.buffer = buffer;
    }

    public Base get(int index) {
        return buffer.get(index);
    }

    public Buffer getBuffer() {
        return buffer;
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
