package fuun;

import java.util.Iterator;

public interface DNACursor extends Iterator<Base> {
    DNACursor copy();

    Base peek();

    Base peek(int offset);

    Base next();

    void skip(int offset);

    boolean isValid();

    @Override
    boolean hasNext();
}
