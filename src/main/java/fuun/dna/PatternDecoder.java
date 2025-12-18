package fuun.dna;

import java.util.ArrayList;
import java.util.List;

import fuun.DNACursor;
import fuun.Finished;

public class PatternDecoder extends Decoder<Pattern> {
    private int level;
    private List<fuun.Base> bases = new ArrayList<>();

    public PatternDecoder() {
        reset();
    }

    @Override
    void reset() {
        level = 0;
        result = new Pattern();
        bases.clear();
    }

    @Override
    void handlePrefix(DNACursor cursor, Prefix prefix) throws Finished {
        switch (prefix) {
            case Prefix.C:
                bases.add(fuun.Base.I);
                break;
            case Prefix.F:
                bases.add(fuun.Base.C);
                break;
            case Prefix.P:
                bases.add(fuun.Base.F);
                break;
            case Prefix.IC:
                bases.add(fuun.Base.P);
                break;
            case Prefix.None:
                throw new Finished();
            default:
                if (!bases.isEmpty()) {
                    result.add(new Pattern.Base(bases.toArray(new fuun.Base[bases.size()])));
                    bases.clear();
                }
                break;
        }

        switch (prefix) {
            case Prefix.C:
            case Prefix.F:
            case Prefix.P:
            case Prefix.IC:
                break;
            case Prefix.IF:
                cursor.skip(1);
                result.add(new Pattern.Search(consts(cursor)));
                break;
            case Prefix.IP:
                result.add(new Pattern.Skip(nat(cursor)));
                break;
            case Prefix.IIC:
            case Prefix.IIF:
                if (level == 0) {
                    setDone(true);
                } else {
                    level -= 1;
                    result.add(new Pattern.Close());
                }
                break;
            case Prefix.IIP:
                level += 1;
                result.add(new Pattern.Open());
                break;
            case Prefix.III:
                extractRNA(cursor);
                break;
            case Prefix.None:
                throw new Finished();
        }
    }
}
