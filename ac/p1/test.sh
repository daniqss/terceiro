#!/usr/bin/env bash

runner=$1 || mpirun
procs=$2 || 4
dim=$3 || 16

main_output=$($runner -np $procs --oversubscribe bin/main_test $dim $dim $dim 1)
sec_output=$(./bin/sec_test $dim $dim $dim 1)

if [[ "$main_output" != "$sec_output" ]]; then
    echo "$main_output"
    echo "===================="
    echo "$sec_output"
fi
