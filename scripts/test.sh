#!/usr/bin/env sh
set -eu

scripts/compile.sh

echo ""
echo "--- Self-test (built-in) ---"
java -cp out Main --self-test

echo ""
echo "--- Characterization Tests ---"
java -cp out CharacterizationTest

