public class NPuzzleNode implements Comparable<NPuzzleNode> {
    final int depth;
    final int heuristic;
    final NPuzzleNode prev;
    final NPuzzleState state;

    NPuzzleNode(NPuzzleNode prev, int depth, NPuzzleState state, int heuristic) {
        this.prev = prev;
        this.depth = depth;
        this.state = state;
        this.heuristic = heuristic;
    }

    @Override
    public int compareTo(NPuzzleNode o) {
//        if (depth != o.depth) return depth - o.depth;
//        return heuristic - o.heuristic;
        int cmp = Integer.compare(depth + heuristic, o.depth + o.heuristic);
        if (cmp == 0)
            cmp = hashCode() - o.hashCode();
        if (cmp == 0)
            cmp = -1;
        return cmp;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }
}
