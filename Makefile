CC = javac
CCFLAGS =

MAIN = Main

SRCS = $(shell find src -name "*.java")
CLS = $(SRCS:src/%.java=build/%.class)
.PHONY: all, clean, run

run: all
	java -cp build Main test.pzl

all: ${CLS}

${CLS}: sources.txt
	javac -d build @sources.txt

sources.txt: ${SRCS}
	echo "${SRCS}" > sources.txt

clean:
	rm -rf build
	rm sources.txt

#python2 npuzzle-gen.py 3 -s -i 7 > test.pzl