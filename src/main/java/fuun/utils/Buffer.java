package fuun.utils;

import fuun.Base;

public record Buffer(Base[] data, int first, int last) {
    public Buffer {
        if (data == null || first < 0 || last < 0 || last < first) {
            throw new IllegalArgumentException();
        }
    }

    public Base get(int index) {
        var offset = index + first;
        return (offset >= first && offset <= last) ? data[offset] : Base.None;
    }

    public int length() {
        return last - first + 1;
    }
}
