package fuun.dna;

import java.util.ArrayList;
import java.util.List;

import fuun.DNACursor;
import fuun.Finished;

public class TemplateDecoder extends Decoder<Template> {
    private List<fuun.Base> bases = new ArrayList<>();

    @Override
    void reset() {
        result = new Template();
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
                    result.add(new Template.Base(bases.toArray(new fuun.Base[bases.size()])));
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
