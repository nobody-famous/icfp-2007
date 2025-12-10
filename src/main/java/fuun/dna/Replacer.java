package fuun.dna;

import java.util.List;

public class Replacer {
    public fuun.DNA replace(fuun.dna.Template template, List<fuun.DNA> env) {
        var result = fuun.Utils.createDNA();

        for (var item : template.getItems()) {
            switch (item) {
                case Template.Base baseItem:
                    result.append(new fuun.Base[] { baseItem.base() });
                    break;
                default:
                    throw new RuntimeException("Unhandled item " + item);
            }
        }

        return result;
    }
}
