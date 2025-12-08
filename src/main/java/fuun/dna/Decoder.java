package fuun.dna;

import fuun.DNACursor;

abstract class Decoder {
    enum Prefix {
        C, F, P, IC, IF, IP, IIC, IIF, IIP, III, None
    }

    protected Prefix parsePrefix(DNACursor cursor) {
        return step(cursor);
    }

    private Prefix stepII(DNACursor cursor) {
        switch (cursor.Next()) {
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
        switch (cursor.Next()) {
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
        switch (cursor.Next()) {
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
