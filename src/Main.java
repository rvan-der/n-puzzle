import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static class ParserError extends Error {
        public ParserError(String message) {
            super(message);
        }
    }

    static NPuzzleState parsePuzzleFile(String path) throws ParserError {
        int[] out = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            ArrayList<ArrayList<Integer>> numberTokens = new ArrayList<>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                boolean first = true;
                for (String token : line.trim().split("\\s+")) {
                    if (token.trim().startsWith("#")) break;
                    try {
                        int number = Integer.parseInt(token);
                        if (first) {
                            first = false;
                            numberTokens.add(new ArrayList<>(Arrays.asList(number)));
                        } else {
                            numberTokens.getLast().add(number);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            reader.close();
            if (numberTokens.isEmpty())
                throw new ParserError("Invalid puzzle file (missing size at the top)");
            if (numberTokens.getFirst().size() > 1)
                throw new ParserError("Invalid puzzle file (too many numbers at the top)");
            int size = numberTokens.getFirst().getFirst();
            out = new int[size * size];
            if (numberTokens.size() - 1 < size)
                throw new ParserError("Invalid puzzle file (not enough lines for expected size of " + size + ")");
            for (int l = 0; l < size; l++) {
                ArrayList<Integer> line = numberTokens.get(l + 1);
                if (line.size() != size)
                    throw new ParserError("Invalid puzzle file (line " + l + " contains " + line.size() + " numbers when " + size + " where expected)");
                for (int n = 0; n < size; ++n)
                    out[l * size + n] = line.get(n);
            }
            return new NPuzzleState(out, size);
        } catch (IOException ignore) {
            System.err.println("file path: '" + path + "' could not be found or read");
            System.exit(-1);
            return null;
        }
    }



    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.err.println("Usage: java Main path");
//        } else {
            NPuzzleState start = parsePuzzleFile(/*args[0]*/"test.pzl");
            System.out.println("parsed pieces: " + Arrays.toString(start.pieces));
            NPuzzleSolver solver = new NPuzzleSolver(start, NPuzzleHeuristics::manhattanHeuristic);
            List<NPuzzleState> states = solver.solve();
            System.out.println("complexity time: " + solver.openNodes.totalInsertions);
            System.out.println("complexity memory: " + solver.concurentMemoryUsage);
            if (states != null) {
                System.out.println("number of steps: " + (states.size() - 1));
            }
            System.out.println("states: " + states);
//        }
    }
}
