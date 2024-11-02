public class NPuzzleSolution {
    final int[] indices;
    final NPuzzleState state;

    NPuzzleSolution(NPuzzleState state) {
        this.state = state;
        indices = new int[state.size * state.size];
        for (int i = 0; i < state.size * state.size; ++i)
            indices[state.pieces[i]] = i;
    }

    NPuzzleSolution(int size) {
        short[] result = new short[size * size];
        int x, y, iy, ix, offsetR, offsetL, offsetU, offsetD;
        x = y = iy = offsetR = offsetL = offsetU = offsetD = 0;
        ix = 1;
        for (short i = 1; i < size * size; i++) {
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
        indices = new int[size * size];
        for (int i = 0; i < size * size; ++i)
            indices[result[i]] = i;
        state = new NPuzzleState(result, (byte)size);
    }

    boolean isSolvable(NPuzzleState state) {
        int acc = 0;
        short size = (short)(state.size * state.size);
        for (short i = 0; i < size; ++i) {
            int m = state.pieces[indices[(i + 1) % size]];
            if (m == 0)
                continue;
            for (int j = i + 1; j < size; ++j) {
                int t = state.pieces[indices[(j + 1) % size]];
                if (t == 0)
                    continue;
                if (m > t)
                    ++acc;
            }
        }
        return acc % 2 == 0;
    }

    boolean isSolved(NPuzzleState state) {
        return state.equals(this.state);
    }
}
