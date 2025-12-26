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

    private void append(Buffer buf) {
        if (wrap(tail + 1) == head) {
            throw new RuntimeException("append needs to resize");
        }

        data[tail] = buf;
        tail = wrap(tail + 1);
    }

    @Override
    public void append(Base[] bases) {
        if (wrap(tail + 1) == head) {
            throw new RuntimeException("append needs to resize");
        }

        if (bases.length == 0) {
            return;
        }

        append(new Buffer(bases, 0, bases.length - 1));
    }

    @Override
    public void append(DNA dna) {
        var deque = (DequeDNA) dna;

        for (var index = deque.head; index != deque.tail; index = deque.wrap(index + 1)) {
            append(deque.data[index]);
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
            throw new RuntimeException("prepend needs to resize");
        }

        System.out.println("***** PREPEND BUF BEFORE " + this + " " + buf + " " + head);
        head = wrap(head - 1);
        data[head] = buf;
        System.out.println("***** PREPEND BUF AFTER " + this + " " + head + " " + data[511]);
    }

    @Override
    public void prepend(DNA dna) {
        var deque = (DequeDNA) dna;

        System.out.println("***** PREPEND " + dna);
        for (var index = deque.wrap(deque.tail - 1); index != deque.head; index = deque.wrap(index - 1)) {
            prepend(deque.data[index]);
        }

        prepend(deque.data[deque.head]);
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
        for (var index = wrap(startCursor.segIndex + 1); index != tail; index = wrap(index + 1)) {
            buf = data[index];

            if (index == wrap(tail - 1)) {
                result.append(new Buffer(buf.data(), buf.first(), buf.first() + endCursor.offset));
            } else {
                result.append(buf);
            }
        }

        return result;
    }

    @Override
    public void truncate(DNACursor cursor) {
        var iter = (Cursor) cursor;

        for (var index = head; index != iter.segIndex; index = wrap(index + 1)) {
            data[index] = null;
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
