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

    @Override
    public void append(Base[] bases) {
        if (wrap(tail + 1) == head) {
            throw new RuntimeException("append needs to resize");
        }

        data[tail] = new Buffer(bases, 0, bases.length - 1);
        tail = wrap(tail + 1);
    }

    @Override
    public void append(DNA dna) {
        throw new RuntimeException("append DNA not done yet");
    }

    @Override
    public int length() {
        var result = 0;

        for (var index = head; index != tail; index = wrap(index + 1)) {
            result += data[index].length();
        }

        return result;
    }

    @Override
    public void prepend(DNA dna) {
        throw new RuntimeException("prepend DNA not done yet");
    }

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        throw new RuntimeException("slice not done yet");
    }

    @Override
    public void truncate(DNACursor cursor) {
        var iter = (Cursor) cursor;

        for (var index = head; index != iter.segIndex; index = wrap(index + 1)) {
            data[index] = null;
        }

        if (iter.offset > data[head].last()) {
            data[head] = null;
            head = wrap(head + 1);
        } else {
            data[head] = new Buffer(data[head].data(), iter.offset, data[head].last());
        }
    }

    @Override
    public Iterator<Base> iterator() {
        return new Cursor(this);
    }
}
