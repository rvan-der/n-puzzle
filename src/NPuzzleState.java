import java.util.Arrays;

public class NPuzzleState {
    public final int[] pieces;
    public final int size;
    private final int hash;

    public NPuzzleState(int[] pieces, int size) {
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
        return Arrays.toString(pieces);
    }
}
