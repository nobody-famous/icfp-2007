package fuun.dequedna;

import java.util.Iterator;

import fuun.Base;
import fuun.DNA;
import fuun.DNACursor;
import fuun.utils.Buffer;

public class DequeDNA implements fuun.DNA {
    fuun.utils.Buffer[] data = new fuun.utils.Buffer[512];
    int head = 0;
    int tail = 0;

    int wrap(int index) {
        return index & (data.length - 1);
    }

    private void collapse() {
        // System.out.println("Collapsing deque of size " + length());
        // Calculate total size
        var size = 0;
        for (var index = head; index != tail; index = wrap(index + 1)) {
            size += data[index].length();
        }

        // Create single buffer with all data
        var bases = new Base[size];
        var offset = 0;
        for (var index = head; index != tail; index = wrap(index + 1)) {
            var buf = data[index];
            System.arraycopy(buf.data(), buf.first(), bases, offset, buf.length());
            offset += buf.length();
            data[index] = null;
        }

        // Reset deque with single buffer
        head = 0;
        tail = 1;
        data[0] = new Buffer(bases, 0, bases.length - 1);
    }

    private void append(Buffer buf) {
        if (wrap(tail + 1) == head) {
            collapse();
        }

        data[tail] = buf;
        tail = wrap(tail + 1);
    }

    @Override
    public void append(Base[] bases) {
        if (wrap(tail + 1) == head) {
            collapse();
        }

        if (bases.length == 0) {
            return;
        }

        append(new Buffer(bases, 0, bases.length - 1));
    }

    @Override
    public void append(DNA dna) {
        var deque = (DequeDNA) dna;
        var segments = new java.util.ArrayList<Buffer>();

        for (var index = deque.head; index != deque.tail; index = deque.wrap(index + 1)) {
            segments.add(deque.data[index]);
        }

        for (var buf : segments) {
            append(buf);
        }
    }

    @Override
    public int length() {
        var result = 0;

        if (data[head] == null) {
            return 0;
        }

        for (var index = head; index != tail; index = wrap(index + 1)) {
            result += data[index].length();
        }

        return result;
    }

    private void prepend(Buffer buf) {
        if (wrap(head - 1) == tail) {
            collapse();
        }

        head = wrap(head - 1);
        data[head] = buf;
    }

    @Override
    public void prepend(DNA dna) {
        var deque = (DequeDNA) dna;

        if (deque.head == deque.tail) {
            return;
        }

        var segments = new java.util.ArrayList<Buffer>();
        for (var index = deque.head; index != deque.tail; index = deque.wrap(index + 1)) {
            segments.add(deque.data[index]);
        }

        for (var i = segments.size() - 1; i >= 0; i--) {
            prepend(segments.get(i));
        }
    }

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        var startCursor = (Cursor) start;
        var endCursor = (Cursor) end;
        var buf = data[startCursor.segIndex];
        var result = new DequeDNA();

        if (startCursor.segIndex == endCursor.segIndex) {
            if (endCursor.offset > startCursor.offset) {
                result.append(
                        new Buffer(buf.data(), buf.first() + startCursor.offset, buf.first() + endCursor.offset - 1));
            }

            return result;
        }

        result.append(new Buffer(buf.data(), buf.first() + startCursor.offset, buf.last()));
        for (var index = wrap(startCursor.segIndex + 1); index != endCursor.segIndex; index = wrap(index + 1)) {
            buf = data[index];
            result.append(buf);
        }

        if (startCursor.segIndex != endCursor.segIndex && endCursor.offset > 0) {
            buf = data[endCursor.segIndex];
            result.append(new Buffer(buf.data(), buf.first(), buf.first() + endCursor.offset - 1));
        }

        return result;
    }

    @Override
    public void truncate(DNACursor cursor) {
        var iter = (Cursor) cursor;

        for (var index = head; index != iter.segIndex; index = wrap(index + 1)) {
            data[index] = null;
            head = wrap(index + 1);
        }

        if (data[head] == null) {
            return;
        }

        if (data[head].first() + iter.offset > data[head].last()) {
            data[head] = null;
            head = wrap(head + 1);
        } else {
            data[head] = new Buffer(data[head].data(), data[head].first() + iter.offset, data[head].last());
        }
    }

    @Override
    public Iterator<Base> iterator() {
        return new Cursor(this);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        for (var item : this) {
            builder.append(item);
        }

        return builder.toString();
    }
}
