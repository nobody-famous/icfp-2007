package fuun;

public interface DNA extends Iterable<Base> {
    int length();

    void append(fuun.Base[] bases);

    void append(DNA dna);

    void prepend(DNA dna);

    DNA slice(DNACursor start, DNACursor end);

    void truncate(DNACursor cursor);
}
