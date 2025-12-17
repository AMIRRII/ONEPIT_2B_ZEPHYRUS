.PHONY: run clean compile db-start db-status

run: db-start compile
	@echo "Starting BHRMS..."
	@echo "Login: admin / admin123"
	@java -cp "bin:lib/mariadb-java-client.jar" bhrms.Main

compile:
	@echo "Compiling..."
	@rm -rf bin
	@mkdir -p bin
	@find src -name "*.java" > sources.txt
	@javac -cp "lib/mariadb-java-client.jar" -d bin @sources.txt
	@rm sources.txt
	@echo "✅ Compiled"

clean:
	@rm -rf bin
	@echo "✅ Cleaned"

db-start:
	@if ! sudo systemctl is-active --quiet mysql; then \
		echo "Starting MySQL..."; \
		sudo systemctl start mysql; \
		sleep 2; \
	fi

db-status:
	@sudo systemctl status mysql --no-pager | head -5

quick:
	@make db-start > /dev/null
	@make compile > /dev/null
	@echo "🚀 Starting BHRMS..."
	@java -cp "bin:lib/mariadb-java-client.jar" bhrms.Main
