CXX = g++
CXXFLAGS = -Wall
LDFLAGS = -lsystemc

SRC = mainDF2022.cpp envolvente.cpp generadorFrecuencia.cpp mezclador.cpp romFrecuencias.cpp
OBJ = $(SRC:.cpp=.o)

TARGET = main

all: $(TARGET)

$(TARGET): $(OBJ)
	$(CXX) $(CXXFLAGS) $(LDFLAGS) -o $@ $^

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

run: $(TARGET)
	./$(TARGET)

clean:
	rm -f $(OBJ) $(TARGET) traza.vcd

.PHONY: all clean run
