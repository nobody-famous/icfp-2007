package fuun;

public class TableDNA implements fuun.DNA {
    private PieceTable<Base> table;

    private class TableCursor implements fuun.DNACursor {
        private PieceTable<Base>.Cursor cursor;

        TableCursor(PieceTable<Base>.Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public DNACursor copy() {
            return new TableCursor(cursor.copy());
        }

        @Override
        public boolean isValid() {
            return cursor.isValid();
        }

        @Override
        public Base next() {
            try {
                return cursor.next();
            } catch (IndexOutOfBoundsException ex) {
                return Base.None;
            }
        }

        @Override
        public Base peek() {
            return peek(0);
        }

        @Override
        public Base peek(int offset) {
            try {
                return cursor.peek(offset);
            } catch (IndexOutOfBoundsException ex) {
                return Base.None;
            }
        }

        @Override
        public void skip(int offset) {
            cursor.skip(offset);
        }

        @Override
        public void truncate() {
            cursor.truncate();
        }

    }

    public TableDNA() {
        this(new PieceTable<>());
    }

    public TableDNA(PieceTable<Base> table) {
        this.table = table;
    }

    @Override
    public void append(Base[] bases) {
        table.append(bases);
    }

    @Override
    public DNACursor getCursor() {
        return new TableCursor(table.getCursor());
    }

    @Override
    public int length() {
        return table.length();
    }

    @Override
    public void prepend(DNA dna) {
        throw new RuntimeException("TableDNA.prepend not done yet");
    }

    @Override
    public DNA slice(DNACursor start, DNACursor end) {
        var startCurser = (TableCursor) start;
        var endCurser = (TableCursor) end;
        var result = table.slice(startCurser.cursor, endCurser.cursor);

        return new TableDNA(result);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        var cursor = getCursor();

        while (cursor.peek() != fuun.Base.None) {
            builder.append(cursor.next());
        }

        return builder.toString();
    }
}
