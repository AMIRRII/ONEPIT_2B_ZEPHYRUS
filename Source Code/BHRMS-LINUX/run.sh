#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Starting BHRMS ===${NC}"
echo

# Set paths
BIN_DIR="bin"
LIB_DIR="lib"
MARIA_JAR="$LIB_DIR/mariadb-java-client.jar"

# Check if compiled classes exist
if [ ! -d "$BIN_DIR" ] || [ -z "$(find $BIN_DIR -name '*.class' -print -quit)" ]; then
    echo -e "${YELLOW}No compiled classes found. Running compile.sh first...${NC}"
    ./compile.sh
    if [ $? -ne 0 ]; then
        exit 1
    fi
fi

echo -e "${GREEN}Launching application...${NC}"
java -cp "$BIN_DIR:$MARIA_JAR" bhrms.Main
