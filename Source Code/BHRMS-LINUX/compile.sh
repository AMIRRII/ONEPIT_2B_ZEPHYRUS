#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== BHRMS Compilation ===${NC}"
echo

# Set paths
SRC_DIR="src"
BIN_DIR="bin"
LIB_DIR="lib"
MARIA_JAR="$LIB_DIR/mariadb-java-client.jar"

echo -e "${YELLOW}1. Cleaning previous build...${NC}"
rm -rf $BIN_DIR
mkdir -p $BIN_DIR

echo -e "${YELLOW}2. Finding Java files...${NC}"
find $SRC_DIR -name "*.java" > sources.txt
echo "Found $(wc -l < sources.txt) Java files"~
echo -e "${YELLOW}3. Compiling...${NC}"
javac -cp "$MARIA_JAR" -d $BIN_DIR @sources.txt 2>&1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Compilation successful!${NC}"
    rm sources.txt
    echo -e "${GREEN}Compiled classes in: $BIN_DIR/${NC}"
    echo
    echo -e "${YELLOW}Compiled classes:${NC}"
    find $BIN_DIR -name "*.class" | wc -l
else
    echo -e "${RED}❌ Compilation failed!${NC}"
    echo -e "${RED}Error output:${NC}"
    cat sources.txt
    rm sources.txt
    exit 1
fi
