#!/bin/bash

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🚀 Quick Starting BHRMS...${NC}"
echo

cd ~/bhrms-project

# Check if MySQL is running
echo -e "${YELLOW}1. Checking MySQL...${NC}"
if sudo systemctl is-active --quiet mysql; then
    echo -e "${GREEN}✅ MySQL is running${NC}"
else
    echo -e "${RED}❌ MySQL not running. Starting...${NC}"
    sudo systemctl start mysql
    sleep 2
fi

# Quick compile
echo -e "${YELLOW}2. Compiling...${NC}"
rm -rf bin
mkdir -p bin
find src -name "*.java" > sources.txt
javac -cp "lib/mariadb-java-client.jar" -d bin @sources.txt 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Compilation successful${NC}"
    rm sources.txt
else
    echo -e "${RED}❌ Compilation failed${NC}"
    rm sources.txt
    exit 1
fi

# Run
echo -e "${YELLOW}3. Starting application...${NC}"
echo -e "${GREEN}Login credentials:${NC}"
echo -e "  👤 Username: ${YELLOW}admin${NC}"
echo -e "  🔑 Password: ${YELLOW}admin123${NC}"
echo -e "${YELLOW}────────────────────────────────${NC}"
echo

java -cp "bin:lib/mariadb-java-client.jar" bhrms.Main
