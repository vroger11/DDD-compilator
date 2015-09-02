# DDD-compilator #
Compilator based on Difference Decision Diagrams (DDD)


## This library can:
- parse a smt or smt2 problems, but variables needs to be Real.
- generate locally reduced DDD.
- generate path reduced and tight DDD (implemented with exponential algorithms)
- use 4 different heuristics were also implemented to parse an smt problem.

## Compiling
First, we download the sources:
> git clone https://github.com/vroger11/DDD-compilator

Next, we will compile our sources (note: this project require at least **Java 8**, and **ant** to compile it)
> cd DDD-compilator

> ant build

## Usage of the parser

### Parse a single file with a specific heuristic

To parse a single smt file we can use the parser like this:
> cd bin

> java tests.Test_Parser \[file to parse\] \[method number\] \[folder where the result will be\]

Where,
- \[file to parse\], is an smt problem
- \[method number\], is the number associated with an heuristic (0: LCFD, 1: MCFD, 2: DFmin, 3: DFmax)

### Parse multiple file with all heuristics using the script provided with the parser
To use this script, you simply have to write:

> ./ParseProblem.sh \[folder_with_smt_problems\] \[folder_for_result\]
