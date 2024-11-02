import java.util.HashMap;
import java.util.TreeSet;

public class NPuzzleOpenNodes {
    final TreeSet<NPuzzleNode> sorted = new TreeSet<>();
    final HashMap<NPuzzleState, NPuzzleNode> nodes = new HashMap<>();
    int totalInsertions = 0;

    boolean contains(NPuzzleState state) {
        return nodes.containsKey(state);
    }

    void insert(NPuzzleNode node) {
        if (!contains(node.state)) {
            sorted.add(node);
            nodes.put(node.state, node);
            ++totalInsertions;
        }
    }

    NPuzzleNode get(NPuzzleState state) {
        return nodes.get(state);
    }

    NPuzzleNode remove(NPuzzleNode node) {
        NPuzzleNode out = nodes.remove(node.state);
        if (out != null)
            sorted.remove(out);
        return out;
    }

    NPuzzleNode remove(NPuzzleState state) {
        NPuzzleNode out = nodes.remove(state);
        if (out != null)
            sorted.remove(out);
        return out;
    }

    NPuzzleNode pop() {
        if (sorted.isEmpty()) return null;
        NPuzzleNode out = sorted.removeFirst();
        if (out != null)
            nodes.remove(out.state);
        return out;
    }

    NPuzzleNode peek() {
        if (sorted.isEmpty()) return null;
        return sorted.first();
    }
}
