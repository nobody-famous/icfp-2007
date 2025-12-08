package fuun;

public interface DNA {
    int length();

    void append(fuun.Base[] bases);

    DNACursor getCursor();
}
