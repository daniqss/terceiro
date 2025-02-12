CC = mpicc
RUNNER = mpirun
CFLAGS = -Wall -g -Wextra
SRCS = src/main.c
OBJS = bin/main.o
PROCS = 4

TARGET = bin/main

all: $(TARGET)

bin:
	mkdir -p bin

bin/%.o: src/%.c | bin
	$(CC) $(CFLAGS) -c $< -o $@

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJS)

run: $(TARGET)
	$(RUNNER) -np $(PROCS) --oversubscribe $(TARGET) 4 4 4 1

clean:
	rm -f $(OBJS) $(TARGET)
	rmdir bin 2>/dev/null || true

.PHONY: all clean run