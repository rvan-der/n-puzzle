public class NPuzzleHeuristics {

    static double manhattanHeuristic(NPuzzleSolution solution, NPuzzleState state) {
        double acc = 0;
        for (int y = 0; y < state.size; ++y)
            for (int x = 0; x < state.size; ++x) {
                int i = solution.indices[state.pieces[x + y * state.size]];
                acc += Math.abs(x - i % state.size) + Math.abs(y - i / state.size);
            }
        return acc;
    }

    static double euclidianHeuristic(NPuzzleSolution solution, NPuzzleState state) {
        double acc = 0;
        for (int y = 0; y < state.size; ++y)
            for (int x = 0; x < state.size; ++x) {
                int i = solution.indices[state.pieces[x + y * state.size]];
                int dx = x - i % state.size;
                int dy = y - i / state.size;
                acc += Math.sqrt(dx * dx + dy * dy);
            }
        return acc;
    }

    static double diffHeuristic(NPuzzleSolution solution, NPuzzleState state) {
        double acc = 0;
        for (int y = 0; y < state.size; ++y)
            for (int x = 0; x < state.size; ++x)
                if (solution.indices[state.pieces[x + y * state.size]] != x + y * state.size)
                    ++acc;
        return acc;
    }
}
