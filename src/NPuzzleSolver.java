import java.util.*;
import java.util.function.BiFunction;

public class NPuzzleSolver {
    final NPuzzleSolution solution;
    final NPuzzleSolution startTarget;
    final NPuzzleState start;
    final NPuzzleOpenNodes openNodesStart = new NPuzzleOpenNodes();
    final NPuzzleOpenNodes openNodesFinish = new NPuzzleOpenNodes();
    final HashMap<NPuzzleState, NPuzzleNode> closedNodesStart = new HashMap<>();
    final HashMap<NPuzzleState, NPuzzleNode> closedNodesFinish = new HashMap<>();
    BiFunction<NPuzzleSolution, NPuzzleState, Double> heuristicFunction;
    int concurentMemoryUsage = 0;
    final double gm, hm;
    final boolean bidirectional;

    NPuzzleSolver(NPuzzleState start, BiFunction<NPuzzleSolution, NPuzzleState, Double> heuristicFunction, double gm, double hm, boolean bi) {
        this.bidirectional = bi;
        this.heuristicFunction = heuristicFunction;
        this.start = start;
        this.solution = new NPuzzleSolution(start.size);
        this.openNodesStart.insert(new NPuzzleNode(null, 0, start, heuristicFunction.apply(solution, start) * hm));
        this.startTarget = new NPuzzleSolution(start);
        if (bi)
            this.openNodesFinish.insert(new NPuzzleNode(null, 0, solution.state, heuristicFunction.apply(startTarget, solution.state) * hm));
        this.hm = hm;
        this.gm = gm;
    }

    boolean isSolvable() {
        return solution.isSolvable(start);
    }

    ArrayList<NPuzzleNode> unfoldNode(NPuzzleNode node, NPuzzleOpenNodes openNodes, HashMap<NPuzzleState, NPuzzleNode> closedNodes, NPuzzleSolution target) {
        ArrayList<NPuzzleNode> result = new ArrayList<>();
        for (NPuzzleState childState : newStates(node.state)) {
            NPuzzleNode duplicate;
            if ((duplicate = openNodes.get(childState)) != null) {
                if (duplicate.depth > node.depth + gm) {
                    result.add(new NPuzzleNode(node, node.depth + gm, childState, duplicate.heuristic));
                    openNodes.remove(childState);
                }
            }
            else if ((duplicate = closedNodes.get(childState)) != null) {
                if (duplicate.depth > node.depth + gm) {
                    result.add(new NPuzzleNode(node, node.depth + gm, childState, duplicate.heuristic));
                    closedNodes.remove(childState);
                }
            }
            else
                result.add(new NPuzzleNode(node, node.depth + gm, childState, heuristicFunction.apply(target, childState) * hm));
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
                    short[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x - 1 + y * state.size];
                    t[x - 1 + y * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                if (x < state.size - 1) {
                    short[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x + 1 + y * state.size];
                    t[x + 1 + y * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                if (y > 0) {
                    short[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x + (y - 1) * state.size];
                    t[x + (y - 1) * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                if (y < state.size - 1) {
                    short[] t = state.pieces.clone();
                    t[x + y * state.size] = t[x + (y + 1) * state.size];
                    t[x + (y + 1) * state.size] = 0;
                    states.add(new NPuzzleState(t, state.size));
                }
                break;
            }
        return states;
    }

    List<NPuzzleState> solve() {
        concurentMemoryUsage = openNodesStart.nodes.size() + openNodesFinish.nodes.size();
        closedNodesStart.clear();
        NPuzzleNode node;
        if (solution.isSolved(openNodesStart.peek().state))
            return List.of(openNodesStart.pop().state);
        while (openNodesStart.peek() != null || (bidirectional && openNodesFinish.peek() != null)) {
            ArrayList<NPuzzleNode> unfolded;
            NPuzzleSolution target;
            NPuzzleOpenNodes openNodes;
            HashMap<NPuzzleState, NPuzzleNode> closedNodes;
            boolean toFinish = !bidirectional || (openNodesStart.peek().getScore() <= openNodesFinish.peek().getScore());
            if (toFinish) {
                openNodes = openNodesStart;
                closedNodes = closedNodesStart;
                target = solution;

            } else {
                openNodes = openNodesFinish;
                closedNodes = closedNodesFinish;
                target = startTarget;
            }
            node = openNodes.pop();
            closedNodes.put(node.state, node);
            unfolded = unfoldNode(node, openNodes, closedNodes, target);
            for (NPuzzleNode neighbor : unfolded) {
                if (target.isSolved(neighbor.state)) {
                    ArrayList<NPuzzleState> out = new ArrayList<>();
                    out.add(target.state);
                    out.add(0, node.state);
                    NPuzzleNode parent;
                    while ((parent = node.prev) != null) {
                        out.add(0, parent.state);
                        node = parent;
                    }
                    return out;
                }
                else if (toFinish && (openNodesFinish.contains(neighbor.state) || closedNodesFinish.containsKey(neighbor.state))) {
                    ArrayList<NPuzzleState> out = new ArrayList<>();
                    NPuzzleNode finishNode = openNodesFinish.get(neighbor.state);
                    if (finishNode == null)
                        finishNode = closedNodesFinish.get(neighbor.state);
                    NPuzzleNode parent;
                    out.add(finishNode.state);
                    while ((parent = finishNode.prev) != null) {
                        out.add(parent.state);
                        finishNode = parent;
                    }
                    out.add(0, node.state);
                    while ((parent = node.prev) != null) {
                        out.add(0, parent.state);
                        node = parent;
                    }
                    return out;
                }
                else if (!toFinish && (openNodesStart.contains(neighbor.state) || closedNodesStart.containsKey(neighbor.state))) {
                    ArrayList<NPuzzleState> out = new ArrayList<>();
                    NPuzzleNode startingNode = openNodesStart.get(neighbor.state);
                    if (startingNode == null)
                        startingNode = closedNodesStart.get(neighbor.state);
                    NPuzzleNode parent;
                    out.add(node.state);
                    while ((parent = node.prev) != null) {
                        out.add(parent.state);
                        node = parent;
                    }
                    out.add(0, startingNode.state);
                    while ((parent = startingNode.prev) != null) {
                        out.add(0, parent.state);
                        startingNode = parent;
                    }
                    return out;
                }
                else
                    openNodes.insert(neighbor);
            }
            int total = closedNodesStart.size() + openNodesStart.nodes.size() + closedNodesFinish.size() + openNodesFinish.nodes.size();
            if (total > concurentMemoryUsage)
                concurentMemoryUsage = total;
        }
        return null;
    }
}
