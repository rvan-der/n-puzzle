public class NPuzzleNode implements Comparable<NPuzzleNode> {
    final double depth;
    final double heuristic;
    final NPuzzleNode prev;
    final NPuzzleState state;

    NPuzzleNode(NPuzzleNode prev, double depth, NPuzzleState state, double heuristic) {
        this.prev = prev;
        this.depth = depth;
        this.state = state;
        this.heuristic = heuristic;
    }

    double getScore() { return depth + heuristic; }

    @Override
    public int compareTo(NPuzzleNode o) {
//        if (depth != o.depth) return depth - o.depth;
//        return heuristic - o.heuristic;
        double cmp = Double.compare(depth + heuristic, o.depth + o.heuristic);
        if (cmp < 0)
            cmp = -1;
        else if (cmp > 0)
            cmp = 1;
        if ((int)cmp == 0)
            cmp = hashCode() - o.hashCode();
        if ((int)cmp == 0)
            cmp = -1;
        return (int)cmp;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }
}
