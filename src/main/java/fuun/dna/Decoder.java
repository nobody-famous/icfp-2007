package fuun.dna;

import java.util.ArrayList;
import java.util.List;

import fuun.DNACursor;
import fuun.Finished;

abstract class Decoder<T> {
    enum Prefix {
        C, F, P, IC, IF, IP, IIC, IIF, IIP, III, None
    }

    private List<fuun.Base[]> rna = new ArrayList<>();
    private boolean done = false;
    T result;

    abstract void handlePrefix(DNACursor cursor, Prefix prefix) throws Finished;

    abstract void reset();

    public List<fuun.Base[]> getRNA() {
        return rna;
    }

    public T decode(fuun.DNA dna) throws Finished {
        rna = new ArrayList<>();
        done = false;
        reset();

        var cursor = (DNACursor) dna.iterator();
        var loopCount = 0;

        while (!done) {
            fuun.Utils.checkLoopCount("PatternDecoder.decode", loopCount++);

            var prefix = parsePrefix(cursor);
            handlePrefix(cursor, prefix);
        }

        cursor.truncate();

        return result;
    }

    protected void setDone(boolean value) {
        done = value;
    }

    protected int nat(DNACursor cursor) {
        var result = 0;
        var base = 1;
        var loopCount = 0;

        for (var item = cursor.next(); item != fuun.Base.None && item != fuun.Base.P; item = cursor.next()) {
            fuun.Utils.checkLoopCount("nat", loopCount++);

            if (item == fuun.Base.C) {
                result += base;
            }

            base *= 2;
        }

        return result;
    }

    protected fuun.Base[] consts(DNACursor cursor) {
        var result = new ArrayList<fuun.Base>();
        var loopCount = 0;
        var done = false;

        for (var item = cursor.peek(); !done; item = cursor.peek()) {
            fuun.Utils.checkLoopCount("consts", loopCount++);

            switch (item) {
                case fuun.Base.C:
                    result.add(fuun.Base.I);
                    cursor.skip(1);
                    break;
                case fuun.Base.F:
                    result.add(fuun.Base.C);
                    cursor.skip(1);
                    break;
                case fuun.Base.P:
                    result.add(fuun.Base.F);
                    cursor.skip(1);
                    break;
                case fuun.Base.I:
                    if (cursor.peek(1) == fuun.Base.C) {
                        result.add(fuun.Base.P);
                        cursor.skip(2);
                    } else {
                        done = true;
                    }
                    break;
                case fuun.Base.None:
                    done = true;
                    break;
            }
        }

        return result.toArray(new fuun.Base[result.size()]);
    }

    protected void extractRNA(DNACursor cursor) {
        var item = new fuun.Base[7];

        for (var index = 0; index < item.length; index += 1) {
            item[index] = cursor.next();
        }

        rna.add(item);
    }

    protected Prefix parsePrefix(DNACursor cursor) {
        return step(cursor);
    }

    private Prefix stepII(DNACursor cursor) {
        switch (cursor.next()) {
            case fuun.Base.C:
                return Prefix.IIC;
            case fuun.Base.F:
                return Prefix.IIF;
            case fuun.Base.P:
                return Prefix.IIP;
            case fuun.Base.I:
                return Prefix.III;
            case fuun.Base.None:
                return Prefix.None;
        }

        return Prefix.None;
    }

    private Prefix stepI(DNACursor cursor) {
        switch (cursor.next()) {
            case fuun.Base.C:
                return Prefix.IC;
            case fuun.Base.F:
                return Prefix.IF;
            case fuun.Base.P:
                return Prefix.IP;
            case fuun.Base.I:
                return stepII(cursor);
            case fuun.Base.None:
                return Prefix.None;
        }

        return Prefix.None;
    }

    private Prefix step(DNACursor cursor) {
        switch (cursor.next()) {
            case fuun.Base.C:
                return Prefix.C;
            case fuun.Base.F:
                return Prefix.F;
            case fuun.Base.P:
                return Prefix.P;
            case fuun.Base.I:
                return stepI(cursor);
            case fuun.Base.None:
                return Prefix.None;
        }

        return Prefix.None;
    }
}
