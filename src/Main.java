import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
                            numberTokens.get(numberTokens.size() - 1).add(number);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            reader.close();
            if (numberTokens.isEmpty())
                throw new ParserError("Invalid puzzle file (missing size at the top)");
            if (numberTokens.get(0).size() > 1)
                throw new ParserError("Invalid puzzle file (too many numbers at the top)");
            short size = numberTokens.get(0).get(0);
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
            return new NPuzzleState(out, (byte)size);
        } catch (IOException ignore) {
            System.err.println("file path: '" + path + "' could not be found or read");
            System.exit(-1);
            return null;
        }
    }

    static NPuzzleState shuffle(int size) {
        NPuzzleSolution solution = new NPuzzleSolution(size);
        int x = solution.indices[0] % solution.state.size;
        int y = solution.indices[0] / solution.state.size;
        short[] state = solution.state.pieces.clone();
        Random r = new Random();
        for (int i = 0; i < solution.state.size * solution.state.size * 2; ++i)
            for (int t = 0; t < 4; ++t) {
                int d = r.nextInt(4);
                if (d == 0 && y > 0) {
                    state[x + y * size] = state[x + (y - 1) * size];
                    state[x + --y * size] = 0;
                    break;
                }
                if (d == 1 && y < size - 1) {
                    state[x + y * size] = state[x + (y + 1) * size];
                    state[x + ++y * size] = 0;
                    break;
                }
                if (d == 2 && x > 0) {
                    state[x + y * size] = state[(x - 1) + y * size];
                    state[--x + y * size] = 0;
                    break;
                }
                if (d == 3 && x < size - 1) {
                    state[x + y * size] = state[(x + 1) + y * size];
                    state[++x + y * size] = 0;
                    break;
                }
            }
        return new NPuzzleState(state, (byte)size);
    }

    public static void main(String[] args) {
        double hm = 10.;
        double gm = 1.;
        boolean bi = false;
        int random = Integer.MIN_VALUE;
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
                case "-r" -> {
                    try {
                        random = Integer.parseInt(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("expected number after -g option");
                    }
                }
                case "-b" -> bi = true;
                case "-f" -> {
                    switch (args[++i]) {
                        case "m" -> {
                        }
                        case "e" -> heuristicFunction = NPuzzleHeuristics::euclidianHeuristic;
                        case "d" -> heuristicFunction = NPuzzleHeuristics::diffHeuristic;
                        default -> {
                            System.err.println("unknown method: " + args[i]);
                            System.exit(-1);
                        }
                    }
                }
                default -> path = args[i];
            }
        }
        NPuzzleState start;
        if (path != null)
            start = parsePuzzleFile(path);
        else if (random > 0)
            start = shuffle(random);
        else {
            System.out.println("Usage: java Main [options ...] <file>/-r <size>");
            System.out.println("<file>: any readable file with a valid (but potentially unsolvable) n puzzle map");
            System.out.println("-r <size>: randomly generate a solvable puzzle instead of reading a file");
            System.out.println("-b: bidirectional strategy");
            System.out.println("-h <mul>: multiply the heuristic by this value (default to 10)");
            System.out.println("-g <mul>: multiply the depth by this value (default to 1)");
            System.out.println("-f <char>: chose a heuristic from the set:");
            System.out.println("   m -> manhattan distances (default)");
            System.out.println("   e -> euclidian distances");
            System.out.println("   d -> differential");
            return;
        }
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
