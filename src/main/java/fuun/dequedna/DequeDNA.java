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

        for (var buffer : data) {
            result += buffer.length();
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
        throw new RuntimeException("truncate not done yet");
    }

    @Override
    public Iterator<Base> iterator() {
        return new Cursor(this);
    }
}
