SCRIPTS_PATH = scripts/

setup:
	@echo "Setting up the environment..."
	@bash $(SCRIPTS_PATH)setup.sh

compile:
	@echo "Compiling language files..."
	@bash $(SCRIPTS_PATH)compile.sh

run:
	@if [ -z "$(lang)" ]; then \
		echo "No language specified. Running application with default language."; \
		python3 -m src.main; \
	else \
		echo "Starting application in: $(lang)"; \
		LC_ALL=$(lang) LANG=$(lang) LANGUAGE=$(lang) python3 -m src.main; \
	fi
