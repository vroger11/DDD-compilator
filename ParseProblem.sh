#!/bin/sh

if [ $# -ne 2 -o ! -d "$1" ]; then
	echo "bad use: $0 folder_with_smt_problems folder_for_result" >&2
	exit 1
fi

pathResult=$(readlink -f "$2");

for fileOrFolder in "$1"/* ; do
    if [ -d "$fileOrFolder" ]; then
        "$0" "$fileOrFolder" "$pathResult"
    fi
    
	if [ -f "$fileOrFolder" ];	then
	    if [ ${fileOrFolder: -3} == "smt" -o ${fileOrFolder: -4} == "smt2" ]; then
            echo "$fileOrFolder"
            
            cd bin
            for i in 0 1 2 3; do
                echo "launch heuristic $i"
            java tests.Test_Parser "$fileOrFolder" "$i" "$pathResult" > /dev/null 2> /dev/null
                echo "heuristic $i pass"
            done
            cd ..
        fi
	fi
done

exit 0
