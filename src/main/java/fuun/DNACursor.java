package fuun;

public interface DNACursor {
    Base peek();

    Base peek(int offset);

    Base next();

    void skip(int offset);
}
