package fuun.dna;

import fuun.DNACursor;
import fuun.Finished;

public class PatternDecoder extends Decoder<Pattern> {
    private int level;
    private Pattern pattern;
    private boolean done;

    public PatternDecoder() {
        this.pattern = new Pattern();
        this.level = 0;
        this.done = false;
    }

    @Override
    public Pattern decode(DNACursor cursor) throws Finished {
        var loopCount = 0;

        while (!done) {
            fuun.Utils.checkLoopCount("PatternDecoder.decode", loopCount++);

            var prefix = parsePrefix(cursor);
            handlePrefix(cursor, prefix);
        }

        return pattern;
    }

    private void handlePrefix(DNACursor cursor, Prefix prefix) throws Finished {
        switch (prefix) {
            case Prefix.C:
                pattern.add(new Pattern.Base(fuun.Base.I));
                break;
            case Prefix.F:
                pattern.add(new Pattern.Base(fuun.Base.C));
                break;
            case Prefix.P:
                pattern.add(new Pattern.Base(fuun.Base.F));
                break;
            case Prefix.IC:
                pattern.add(new Pattern.Base(fuun.Base.P));
                break;
            case Prefix.IF:
                cursor.skip(1);
                pattern.add(new Pattern.Search(consts(cursor)));
                break;
            case Prefix.IP:
                pattern.add(new Pattern.Skip(nat(cursor)));
                break;
            case Prefix.IIC:
            case Prefix.IIF:
                if (level == 0) {
                    done = true;
                } else {
                    level -= 1;
                    pattern.add(new Pattern.Close());
                }
                break;
            case Prefix.IIP:
                level += 1;
                pattern.add(new Pattern.Open());
                break;
            case Prefix.III:
                extractRNA(cursor);
                break;
            default:
                throw new Finished();
        }
    }
}
