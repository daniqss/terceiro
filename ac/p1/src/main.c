#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#define MAX 100

int32_t manageArgs(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                   int32_t *n);
void fillMatrix(float **matrix, int32_t rows, int32_t cols);
void printMatrix(float **matrix, int32_t rows, int32_t cols);

int32_t main(int32_t argc, char *argv[]) {
    int32_t m, k, n;
    float **A, **B, **C;
    // float alpha = 3.0;

    if (manageArgs(argc, argv, &m, &k, &n)) {
        fprintf(stderr, "usage: mpirun -np 4 ./%s <m> <n> <k>\n", argv[0]);
        return EXIT_FAILURE;
    };

    // alloc and initialize matrices
    A = (float **)malloc(m * sizeof(float *));
    B = (float **)malloc(k * sizeof(float *));
    C = (float **)malloc(m * sizeof(float *));
    for (int32_t i = 0; i < m; i++) {
        A[i] = (float *)malloc(k * sizeof(float));
        B[i] = (float *)malloc(n * sizeof(float));
        C[i] = (float *)malloc(n * sizeof(float));
    }
    fillMatrix(A, m, k);
    fillMatrix(B, k, n);

    // print matrices
    printMatrix(A, m, k);
    printMatrix(B, k, n);

    return EXIT_SUCCESS;
}

int32_t manageArgs(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                   int32_t *n) {
    if (argc != 4)
        return 1;

    // parse string to integer
    *m = atoi(argv[1]);
    *k = atoi(argv[2]);
    *n = atoi(argv[3]);
    return 0;
}

void fillMatrix(float **matrix, int32_t rows, int32_t cols) {
    for (int32_t i = 0; i < rows; i++) {
        for (int32_t j = 0; j < cols; j++) {
            // matrix[i][j] = (float)(rand() % MAX);
            matrix[i][j] = (float)i;
        }
    }
}

void printMatrix(float **matrix, int32_t rows, int32_t cols) {
    for (int32_t i = 0; i < rows; i++) {
        for (int32_t j = 0; j < cols; j++) {
            printf("%1.1f", matrix[i][j]);
        }
        printf("\n");
    }
    printf("\n");
}