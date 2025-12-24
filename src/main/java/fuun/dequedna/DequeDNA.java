package fuun.dequedna;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import fuun.Base;
import fuun.DNA;
import fuun.DNACursor;
import fuun.utils.Buffer;

public class DequeDNA implements fuun.DNA {
    private Deque<fuun.utils.Buffer> data = new ArrayDeque<>();

    @Override
    public void append(Base[] bases) {
        data.add(new Buffer(bases, 0, bases.length - 1));
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
