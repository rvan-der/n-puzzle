import java.util.Arrays;

public class NPuzzleState {
    public final short[] pieces;
    public final byte size;
    private final int hash;

    public NPuzzleState(byte size) {
        this.pieces = new short[size * size];
        this.size = size;
        this.hash = Arrays.hashCode(pieces);
    }

    public NPuzzleState(short[] pieces, byte size) {
        this.pieces = pieces.clone();
        this.size = size;
        this.hash = Arrays.hashCode(pieces);
    }

    @Override
    public int hashCode() { return hash; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof NPuzzleState o)
            return hash == o.hash && Arrays.equals(pieces, o.pieces);
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j)
                sb.append(String.format("%" + (String.valueOf(size * size).length() + 1) + "d", pieces[j + i * size]));
            if (i != size - 1)
                sb.append("\n");
        }
        return sb.toString();
    }
}
