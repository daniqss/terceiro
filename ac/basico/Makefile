CC = mpicc
RUNNER = mpirun
CFLAGS = -Wall -g -Wextra
SRCS = matrixVector.c
OBJS = $(SRCS:.c=.o)
PROCS = 4

TARGET = matrixVector

all: $(TARGET)

%.o : %.c
	$(CC) $(CFLAGS) -c $<

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJS)

run: $(TARGET)
	$(RUNNER) -np $(PROCS) --oversubscribe ./$(TARGET)

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean run
