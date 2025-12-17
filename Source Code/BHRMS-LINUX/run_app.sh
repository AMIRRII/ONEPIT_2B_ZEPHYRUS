#!/bin/bash

echo "=== Starting BHRMS ==="
echo

BIN_DIR="bin"
LIB_DIR="lib"
MARIA_JAR="$LIB_DIR/mariadb-java-client.jar"

# Check if compiled
if [ ! -d "$BIN_DIR" ] || [ -z "$(ls -A $BIN_DIR)" ]; then
    echo "No compiled classes found. Running compile..."
    ./compile_all.sh
    if [ $? -ne 0 ]; then
        exit 1
    fi
fi

echo "Launching application..."
echo "Login credentials:"
echo "  Username: admin"
echo "  Password: admin123"
echo

java -cp "$BIN_DIR:$MARIA_JAR" bhrms.Main
