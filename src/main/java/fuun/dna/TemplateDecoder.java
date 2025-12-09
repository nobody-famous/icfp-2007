package fuun.dna;

import fuun.DNACursor;
import fuun.Finished;

public class TemplateDecoder extends Decoder<Template> {
    @Override
    void reset() {
        result = new Template();
    }

    @Override
    void handlePrefix(DNACursor cursor, Prefix prefix) throws Finished {
        switch (prefix) {
            case Prefix.C:
                result.add(new Template.Base(fuun.Base.I));
                break;
            case Prefix.F:
                result.add(new Template.Base(fuun.Base.C));
                break;
            case Prefix.P:
                result.add(new Template.Base(fuun.Base.F));
                break;
            case Prefix.IC:
                result.add(new Template.Base(fuun.Base.P));
                break;
            case Prefix.IF:
            case Prefix.IP: {
                var level = nat(cursor);
                var ref = nat(cursor);
                result.add(new Template.Protect(ref, level));
                break;
            }
            case Prefix.IIC:
            case Prefix.IIF:
                setDone(true);
                break;
            case Prefix.IIP:
                result.add(new Template.Length(nat(cursor)));
                break;
            case Prefix.III:
                extractRNA(cursor);
                break;
            case Prefix.None:
                throw new Finished();
        }
    }
}
