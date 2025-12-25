package fuun;

import java.util.Iterator;

public interface DNACursor extends Iterator<Base> {
    DNACursor copy();

    Base peek();

    Base peek(int count);

    Base next();

    void skip(int count);

    boolean isValid();

    @Override
    boolean hasNext();
}
