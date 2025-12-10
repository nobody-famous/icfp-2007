package fuun;

public interface DNA {
    int length();

    void append(fuun.Base[] bases);

    DNA slice(DNACursor start, DNACursor end);

    DNACursor getCursor();
}
