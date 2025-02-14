CXX = g++
CXXFLAGS = -Wall -Wextra -std=c++11
LDFLAGS = -lsystemc

SRC = mainPracticaDF.cpp restar.cpp sumar.cpp
OBJ = $(SRC:.cpp=.o)

TARGET = main

all: $(TARGET)

$(TARGET): $(OBJ)
	$(CXX) $(CXXFLAGS) -o $@ $^

%.o: %.cpp %.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJ) $(TARGET)

.PHONY: all clean