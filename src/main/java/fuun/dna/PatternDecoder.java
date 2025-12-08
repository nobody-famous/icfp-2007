package fuun.dna;

import fuun.DNACursor;
import fuun.Finished;

public class PatternDecoder extends Decoder {
    private DNACursor cursor;
    private int level;
    private Pattern pattern;
    private boolean done;

    public PatternDecoder(DNACursor cursor) {
        this.cursor = cursor;
        this.pattern = new Pattern();
        this.level = 0;
        this.done = false;
    }

    public Pattern decode() throws Finished {
        var loopCount = 0;

        while (!done) {
            fuun.Utils.checkLoopCount("PatternDecoder.decode", loopCount++);

            handlePrefix(parsePrefix(cursor));
        }

        return pattern;
    }

    private void handlePrefix(Prefix prefix) throws Finished {
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
                break;
            case Prefix.IP:
                break;
            case Prefix.IIC:
            case Prefix.IIF:
                if (level == 0) {
                    done = true;
                } else {
                    throw new RuntimeException("IIC/F level > 0 not handled");
                }
                break;
            case Prefix.IIP:
                break;
            case Prefix.III:
                break;
            default:
                throw new Finished();
        }
    }
}
