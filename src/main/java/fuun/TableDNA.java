package fuun;

public class TableDNA implements fuun.DNA {
    private PieceTable<Base> table = new PieceTable<>();

    @Override
    public void append(Base[] bases) {
        table.append(bases);
    }

    @Override
    public DNACursor getCursor() {
        throw new RuntimeException("TableDNA.getCursor not done yet");
    }

    @Override
    public int length() {
        throw new RuntimeException("TableDNA.length not done yet");
    }

    @Override
    public void prepend(DNA dna) {
        throw new RuntimeException("TableDNA.prepend not done yet");
    }

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        throw new RuntimeException("TableDNA.slice not done yet");
    }
}
