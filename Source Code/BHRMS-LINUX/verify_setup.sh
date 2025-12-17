#!/bin/bash

echo "=== BHRMS Setup Verification ==="
echo

echo "1. Checking files..."
echo "   Main.java: $(ls ~/bhrms-project/src/bhrms/Main.java 2>/dev/null && echo '✅' || echo '❌')"
echo "   DatabaseConnection.java: $(ls ~/bhrms-project/src/bhrms/DatabaseConnection.java 2>/dev/null && echo '✅' || echo '❌')"
echo "   JAR file: $(ls ~/bhrms-project/lib/mariadb-java-client.jar 2>/dev/null && echo '✅' || echo '❌')"
echo

echo "2. Checking database..."
sudo mysql -u root -e "USE bhrms_db; SHOW TABLES;" 2>/dev/null && echo "✅ Database and tables exist" || echo "❌ Database issue"
echo

echo "3. Testing Java compilation..."
cd ~/bhrms-project
rm -rf bin_test
mkdir -p bin_test
javac -cp "lib/mariadb-java-client.jar" -d bin_test src/bhrms/DatabaseConnection.java 2>/dev/null && echo "✅ Java compiles" || echo "❌ Java compilation failed"
echo

echo "4. Testing database connection from Java..."
java -cp "bin_test:lib/mariadb-java-client.jar" bhrms.DatabaseConnection 2>&1 | grep -E "✅|❌|SUCCESS|FAILED"
echo

echo "=== Verification Complete ==="
