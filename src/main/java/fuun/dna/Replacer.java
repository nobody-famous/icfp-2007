package fuun.dna;

import java.util.ArrayList;
import java.util.List;

import fuun.DNACursor;

public class Replacer {
    public fuun.DNA replace(fuun.dna.Template template, List<fuun.DNA> env) {
        var result = fuun.Utils.createDNA();

        for (var item : template.getItems()) {
            switch (item) {
                case Template.Base baseItem:
                    result.append(new fuun.Base[] { baseItem.base() });
                    break;
                case Template.Protect protectItem: {
                    var envItem = getEnvItem(env, protectItem.reference());
                    if (envItem != null) {
                        result.append(protect(envItem, protectItem.level()));
                    }
                    break;
                }
                case Template.Length lengthItem: {
                    var envItem = getEnvItem(env, lengthItem.reference());
                    result.append(asnat(envItem == null ? 0 : envItem.length()));
                    break;
                }
                default:
                    throw new RuntimeException("Unhandled item " + item);
            }
        }

        return result;
    }

    private fuun.DNA getEnvItem(List<fuun.DNA> env, int index) {
        try {
            return env.get(index);
        } catch (Exception ex) {
            return null;
        }
    }

    private fuun.Base[][][] protectLevels = new fuun.Base[][][] {
            { { fuun.Base.I },
                    { fuun.Base.C },
                    { fuun.Base.F },
                    { fuun.Base.P },
                    { fuun.Base.I, fuun.Base.C },
                    { fuun.Base.C, fuun.Base.F },
                    { fuun.Base.F, fuun.Base.P },
                    { fuun.Base.P, fuun.Base.I, fuun.Base.C } },
            { { fuun.Base.C },
                    { fuun.Base.F },
                    { fuun.Base.P },
                    { fuun.Base.I, fuun.Base.C },
                    { fuun.Base.C, fuun.Base.F },
                    { fuun.Base.F, fuun.Base.P },
                    { fuun.Base.P, fuun.Base.I, fuun.Base.C },
                    { fuun.Base.I, fuun.Base.C, fuun.Base.C, fuun.Base.F } },
            { { fuun.Base.F },
                    { fuun.Base.P },
                    { fuun.Base.I, fuun.Base.C },
                    { fuun.Base.C, fuun.Base.F },
                    { fuun.Base.F, fuun.Base.P },
                    { fuun.Base.P, fuun.Base.I, fuun.Base.C },
                    { fuun.Base.I, fuun.Base.C, fuun.Base.C, fuun.Base.F },
                    { fuun.Base.C, fuun.Base.F, fuun.Base.F, fuun.Base.P } },
            { { fuun.Base.P },
                    { fuun.Base.I, fuun.Base.C },
                    { fuun.Base.C, fuun.Base.F },
                    { fuun.Base.F, fuun.Base.P },
                    { fuun.Base.P, fuun.Base.I, fuun.Base.C },
                    { fuun.Base.I, fuun.Base.C, fuun.Base.C, fuun.Base.F },
                    { fuun.Base.C, fuun.Base.F, fuun.Base.F, fuun.Base.P },
                    { fuun.Base.F, fuun.Base.P, fuun.Base.P, fuun.Base.I, fuun.Base.C } },
    };

    private int getProtectIndex(fuun.Base base) {
        switch (base) {
            case fuun.Base.I:
                return 0;
            case fuun.Base.C:
                return 1;
            case fuun.Base.F:
                return 2;
            case fuun.Base.P:
                return 3;
            case fuun.Base.None:
                throw new RuntimeException("getBaseIndex got base None");
        }

        return 0;
    }

    private fuun.Base[] protect(fuun.DNA toProtect, int level) {
        var bases = new ArrayList<fuun.Base>();
        var cursor = (DNACursor) toProtect.iterator();
        var loopCount = 0;

        while (cursor.peek() != fuun.Base.None) {
            fuun.Utils.checkLoopCount("protect", loopCount++);

            var base = cursor.next();
            var index = getProtectIndex(base);
            bases.addAll(List.of(protectLevels[index][level]));
        }

        return bases.toArray(new fuun.Base[bases.size()]);
    }

    private fuun.Base[] asnat(int num) {
        var bases = new ArrayList<fuun.Base>();
        var loopCount = 0;

        while (num > 0) {
            fuun.Utils.checkLoopCount("asnat", loopCount++);

            if ((num & 1) == 1) {
                bases.add(fuun.Base.C);
            } else {
                bases.add(fuun.Base.I);
            }

            num /= 2;
        }

        bases.add(fuun.Base.P);

        return bases.toArray(new fuun.Base[bases.size()]);
    }
}
