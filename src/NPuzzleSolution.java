import java.util.HashMap;

public class NPuzzleSolution {
    final HashMap<Integer, Integer> indices = new HashMap<>();
    final NPuzzleState state;

    NPuzzleSolution(int size) {
        int[] result = new int[size * size];
        int x, y, iy, ix, offsetR, offsetL, offsetU, offsetD;
        x = y = iy = offsetR = offsetL = offsetU = offsetD = 0;
        ix = 1;
        for (int i = 1; i < size * size; i++) {
            result[x + size * y] = i;
            if (ix == 1 && x == size - offsetR - 1) {
                ix = 0;
                iy = 1;
                offsetU += 1;
            }
            else if (ix == -1 && x == offsetL) {
                ix = 0;
                iy = -1;
                offsetD += 1;
            }
            else if (iy == 1 && y == size - offsetD - 1) {
                ix = -1;
                iy = 0;
                offsetR += 1;
            }
            else if (iy == -1 && y == offsetU) {
                ix = 1;
                iy = 0;
                offsetL += 1;
            }
            x += ix;
            y += iy;
        }
        result[x + size * y] = 0;
        for (int i = 0; i < size * size; ++i)
            indices.put(result[i], i);
        state = new NPuzzleState(result, size);
    }

    boolean isSolved(NPuzzleState state) {
        return state.equals(this.state);
    }
}
