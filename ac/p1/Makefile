CC = mpicc
RUNNER = mpirun
CFLAGS = -Wall -g -Wextra
test_CFLAGS = $(CFLAGS) -DDEBUG=0
SRCS = src/main.c src/sec.c
OBJS = bin/main.o bin/sec.o
PROCS = 16

TARGET = bin/main
SEQUENTIAL_TARGET = bin/sec

# build rules
all: $(TARGET) $(SEQUENTIAL_TARGET)

bin:
	mkdir -p bin

bin/%.o: src/%.c | bin
	$(CC) $(CFLAGS) -c $< -o $@

$(TARGET): bin/main.o
	$(CC) $(CFLAGS) -o $(TARGET) bin/main.o

$(SEQUENTIAL_TARGET): bin/sec.o
	$(CC) $(CFLAGS) -o $(SEQUENTIAL_TARGET) bin/sec.o

bin/main_test.o: src/main.c | bin
	$(CC) $(test_CFLAGS) -c $< -o $@

bin/sec_test.o: src/sec.c | bin
	$(CC) $(test_CFLAGS) -c $< -o $@

test_main: bin/main_test.o
	$(CC) $(test_CFLAGS) -o bin/main_test bin/main_test.o

test_sec: bin/sec_test.o
	$(CC) $(test_CFLAGS) -o bin/sec_test bin/sec_test.o

# run rules
run: $(TARGET)
	$(RUNNER) -np $(PROCS) --oversubscribe $(TARGET) 27 20 36 3

sec: $(SEQUENTIAL_TARGET)
	./$(SEQUENTIAL_TARGET) 27 20 36 3

test: test_main test_sec
	./test.sh $(RUNNER) 4 4
	./test.sh $(RUNNER) 4 8
	./test.sh $(RUNNER) 4 16
	./test.sh $(RUNNER) 8 8
	./test.sh $(RUNNER) 8 16
	./test.sh $(RUNNER) 8 32
	./test.sh $(RUNNER) 16 16
	./test.sh $(RUNNER) 32 32
	./test.sh $(RUNNER) 16 27

clean:
	rm -f $(OBJS) $(TARGET) $(SEQUENTIAL_TARGET) bin/*_test* bin/*_test.o
	rmdir bin 2>/dev/null || true

.PHONY: all clean run sec