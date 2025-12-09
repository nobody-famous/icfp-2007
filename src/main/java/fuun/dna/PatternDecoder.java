package fuun.dna;

import fuun.DNACursor;
import fuun.Finished;

public class PatternDecoder extends Decoder<Pattern> {
    private int level;

    public PatternDecoder() {
        reset();
    }

    @Override
    void reset() {
        level = 0;
        result = new Pattern();
    }

    @Override
    void handlePrefix(DNACursor cursor, Prefix prefix) throws Finished {
        switch (prefix) {
            case Prefix.C:
                result.add(new Pattern.Base(fuun.Base.I));
                break;
            case Prefix.F:
                result.add(new Pattern.Base(fuun.Base.C));
                break;
            case Prefix.P:
                result.add(new Pattern.Base(fuun.Base.F));
                break;
            case Prefix.IC:
                result.add(new Pattern.Base(fuun.Base.P));
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
