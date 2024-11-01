import java.util.*;
import java.util.function.BiFunction;

public class NPuzzleSolver {
    final NPuzzleSolution solution;
    final NPuzzleOpenNodes openNodes = new NPuzzleOpenNodes();
    final HashMap<NPuzzleState, NPuzzleNode> closedNodes = new HashMap<>();
    boolean finished = false;
    BiFunction<NPuzzleSolution, NPuzzleState, Integer> heuristicFunction;
    int concurentMemoryUsage = 0;

    NPuzzleSolver(NPuzzleState start, BiFunction<NPuzzleSolution, NPuzzleState, Integer> heuristicFunction) {
        this.heuristicFunction = heuristicFunction;
        this.solution = new NPuzzleSolution(start.size);
        NPuzzleNode startNode = new NPuzzleNode(null, 0, start, heuristicFunction.apply(solution, start));
        this.openNodes.insert(startNode);
    }

    ArrayList<NPuzzleNode> unfoldNode(NPuzzleNode node) {
        ArrayList<NPuzzleNode> result = new ArrayList<NPuzzleNode>();
        for (NPuzzleState state : newStates(node.state)) {
            if (openNodes.contains(state)) {
                if (this.openNodes.get(state).depth > node.depth + 1) {
                    result.add(new NPuzzleNode(node, node.depth + 1, state, openNodes.get(state).heuristic));
                    openNodes.remove(state);
                }
            }
            else if (closedNodes.containsKey(state)) {
                if (this.closedNodes.get(state).depth > node.depth + 1) {
                    result.add(new NPuzzleNode(node, node.depth + 1, state, closedNodes.get(state).heuristic));
                    closedNodes.remove(state);
                }
            }
            else
                result.add(new NPuzzleNode(node, node.depth + 1, state, heuristicFunction.apply(solution, state)));
        }
        return result;
    }

    static List<NPuzzleState> newStates(NPuzzleState state) {
        List<NPuzzleState> states = new ArrayList<>();
        for (int y = 0; y < state.size; ++y)
            for (int x = 0; x < state.size; ++x) {
                if (state.pieces[x + y * state.size] != 0)
                    continue;
                if (x > 0) {
                    int[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x - 1 + y * state.size];
                    t[x - 1 + y * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                if (x < state.size - 1) {
                    int[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x + 1 + y * state.size];
                    t[x + 1 + y * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                if (y > 0) {
                    int[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x + (y - 1) * state.size];
                    t[x + (y - 1) * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                if (y < state.size - 1) {
                    int[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x + (y + 1) * state.size];
                    t[x + (y + 1) * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                break;
            }
        return states;
    }

    List<NPuzzleState> solve() {
        concurentMemoryUsage = openNodes.nodes.size();
        closedNodes.clear();
        finished = false;
        NPuzzleNode node;
        if (solution.isSolved(openNodes.peek().state))
            return List.of(openNodes.pop().state);
        while ((node = openNodes.pop()) != null) {
            closedNodes.put(node.state, node);
            ArrayList<NPuzzleNode> unfolded = unfoldNode(node);
            for (NPuzzleNode neighbor : unfolded) {
                if (solution.isSolved(neighbor.state)) {
                    ArrayList<NPuzzleState> out = new ArrayList<>();
                    out.add(solution.state);
                    out.addFirst(node.state);
                    NPuzzleNode parent;
                    while ((parent = node.prev) != null) {
                        out.addFirst(parent.state);
                        node = parent;
                    }
                    return out;
                }
                else
                    openNodes.insert(neighbor);
            }
            int total = closedNodes.size() + openNodes.nodes.size();
            if (total > concurentMemoryUsage)
                concurentMemoryUsage = total;
        }
        return null;
    }
}
