package fuun;

public interface DNA {
    int length();

    void append(fuun.Base[] bases);

    void prepend(DNA dna);

    DNA slice(DNACursor start, DNACursor end);

    DNACursor getCursor();
}
