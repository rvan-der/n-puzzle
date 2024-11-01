public class NPuzzleHeuristics {

    static int manhattanHeuristic(NPuzzleSolution solution, NPuzzleState state) {
        int acc = 0;
        for (int y = 0; y < state.size; ++y)
            for (int x = 0; x < state.size; ++x) {
                int i = solution.indices.get(state.pieces[x + y * state.size]);
                acc += Math.abs(x - i % state.size) + Math.abs(y - i / state.size);
            }
        return acc;
    }

}
