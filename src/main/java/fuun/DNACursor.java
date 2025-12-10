package fuun;

public interface DNACursor {
    DNACursor copy();

    Base peek();

    Base peek(int offset);

    Base next();

    void skip(int offset);

    void truncate();

    boolean isValid();
}
