MAIN = Main

SRCS = src/Main.java\
        src/NPuzzleHeuristics.java\
        src/NPuzzleNode.java\
        src/NPuzzleOpenNodes.java\
        src/NPuzzleSolution.java\
        src/NPuzzleSolver.java\
        src/NPuzzleState.java

CLS = $(SRCS:src/%.java=build/%.class)

.PHONY: all, clean, run

all: ${CLS}

${CLS}: ${SRCS}
	javac -d build $^

clean:
	rm -rf build

#python2 npuzzle-gen.py 3 -s -i 7 > test.pzl