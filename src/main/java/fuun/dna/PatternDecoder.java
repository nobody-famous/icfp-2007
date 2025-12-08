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
            case Prefix.I:
                break;
            case Prefix.C:
                break;
            case Prefix.F:
                break;
            case Prefix.P:
                break;
            case Prefix.IC:
                break;
            case Prefix.IF:
                break;
            case Prefix.IP:
                break;
            case Prefix.IIC:
                break;
            case Prefix.IIF:
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
