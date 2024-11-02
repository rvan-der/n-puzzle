import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Main {
    static class ParserError extends Error {
        public ParserError(String message) {
            super(message);
        }
    }

    static NPuzzleState parsePuzzleFile(String path) throws ParserError {
        short[] out;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            ArrayList<ArrayList<Short>> numberTokens = new ArrayList<>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                boolean first = true;
                for (String token : line.trim().split("\\s+")) {
                    if (token.trim().startsWith("#")) break;
                    try {
                        short number = Short.parseShort(token);
                        if (first) {
                            first = false;
                            numberTokens.add(new ArrayList<>(List.of(number)));
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
            short size = numberTokens.getFirst().getFirst();
            if (size > 255)
                throw new ParserError("Invalid puzzle size, can't solve more than 255*255 grids: " + size);
            out = new short[size * size];
            if (numberTokens.size() - 1 < size)
                throw new ParserError("Invalid puzzle file (not enough lines for expected size of " + size + ")");
            for (int l = 0; l < size; l++) {
                ArrayList<Short> line = numberTokens.get(l + 1);
                if (line.size() != size)
                    throw new ParserError("Invalid puzzle file (line " + l + " contains " + line.size() + " numbers when " + size + " where expected)");
                for (int n = 0; n < size; ++n)
                    out[l * size + n] = line.get(n);
            }
            return new NPuzzleState(out, (byte)(short)size);
        } catch (IOException ignore) {
            System.err.println("file path: '" + path + "' could not be found or read");
            System.exit(-1);
            return null;
        }
    }

    public static void main(String[] args) {
        double hm = 10.;
        double gm = 1.;
        boolean bi = false;
        BiFunction<NPuzzleSolution, NPuzzleState, Double> heuristicFunction = NPuzzleHeuristics::manhattanHeuristic;
        String path = null;
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-h" -> {
                    try {
                        hm = Double.parseDouble(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("expected number after -h option");
                    }
                }
                case "-g" -> {
                    try {
                        gm = Double.parseDouble(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("expected number after -g option");
                    }
                }
                case "-b" -> { bi = true; }
                case "-f" -> {
                    switch (args[++i]) {
                        case "m" -> {
                        }
                        case "e" -> {
                            heuristicFunction = NPuzzleHeuristics::euclidianHeuristic;
                        }
                        case "d" -> {
                            heuristicFunction = NPuzzleHeuristics::diffHeuristic;
                        }
                        default -> {
                            System.err.println("unknown method: " + args[i]);
                            System.exit(-1);
                        }
                    }
                }
                default -> path = args[i];
            }
        }
        if (path == null) {
            System.out.println("Usage: java Main [options ...] <file>");
            System.out.println("<file>: any readable file with a valid (but potentially unsolvable) n puzzle map");
            System.out.println("-b: bidirectional strategy");
            System.out.println("-h <mul>: multiply the heuristic by this value (default to 10)");
            System.out.println("-g <mul>: multiply the depth by this value (default to 1)");
            System.out.println("-f <char>: chose a heuristic from the set:");
            System.out.println("   m -> manhattan distances (default)");
            System.out.println("   e -> euclidian distances");
            System.out.println("   d -> differential");
            System.exit(0);
        }
        NPuzzleState start = parsePuzzleFile(path);
        System.out.println("grid:\n" + start);
        NPuzzleSolver solver = new NPuzzleSolver(start, heuristicFunction, gm, hm, bi);
        if (!solver.isSolvable()) {
            System.err.println("This grid is not solvable.");
            return;
        }
        else
            System.out.println("This grid is solvable !");
        try {
            List<NPuzzleState> states = solver.solve();
            System.out.println("complexity time: " + (solver.openNodesStart.totalInsertions + solver.openNodesFinish.totalInsertions));
            System.out.println("complexity memory: " + solver.concurentMemoryUsage);
            if (states != null) {
                System.out.println("number of steps: " + (states.size() - 1));
                System.out.println("states: ");
                for (NPuzzleState state : states) {
                    System.out.println(state + "\n");
                }
            } else
                System.err.println("Failed to solve the grid.");
        } catch (Exception e) {
            System.err.println("Failed to solve the grid because " + e.getMessage());
        }
    }
}
