#!/bin/bash

echo "=== Compiling BHRMS Application ==="
echo

SRC_DIR="src"
BIN_DIR="bin"
LIB_DIR="lib"
MARIA_JAR="$LIB_DIR/mariadb-java-client.jar"

# Clean
echo "1. Cleaning previous build..."
rm -rf $BIN_DIR
mkdir -p $BIN_DIR

# Find all Java files
echo "2. Finding Java files..."
find $SRC_DIR -name "*.java" > sources.txt
echo "   Found $(wc -l < sources.txt) Java files"

# Compile
echo "3. Compiling..."
javac -cp "$MARIA_JAR" -d $BIN_DIR @sources.txt 2>&1

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo
    echo "Compiled classes:"
    find $BIN_DIR -name "*.class" | wc -l
    rm sources.txt
else
    echo "❌ Compilation failed!"
    echo "Error output:"
    cat sources.txt
    rm sources.txt
    exit 1
fi
