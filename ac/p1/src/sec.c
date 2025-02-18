#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAX 100
#define MASTER 0
#ifndef DEBUG
#define DEBUG 1
#endif

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n, float *alpha);
void fill_matrix(float *matrix, int32_t rows, int32_t cols);
void multiply_matrix(float *a_matrix, float *b_matrix, float *c_matrix,
                     int32_t m, int32_t k, int32_t n, float alpha);
void print_matrix(float *matrix, int32_t rows, int32_t cols);

int32_t main(int32_t argc, char *argv[]) {
    // matrix dimensions
    int32_t m, k, n;
    float alpha;
    float *a_matrix, *b_matrix, *c_matrix;
    double time;

    if (manage_args(argc, argv, &m, &k, &n, &alpha)) {
        fprintf(stderr, "usage: %s <m> <n> <k> <alpha>\n", argv[0]);
        return EXIT_FAILURE;
    }

    // alloc and initialize matrices
    a_matrix = (float *)malloc(m * k * sizeof(float));
    b_matrix = (float *)malloc(k * n * sizeof(float));
    c_matrix = (float *)malloc(m * n * sizeof(float));

    fill_matrix(a_matrix, m, k);
    fill_matrix(b_matrix, k, n);

    // start timer
    clock_t start = clock();

    // matrix multiplication
    multiply_matrix(a_matrix, b_matrix, c_matrix, m, k, n, alpha);

    // end timer
    time = (double)(clock() - start) / CLOCKS_PER_SEC;
    if (DEBUG)
        printf("time used -> %f\n\n", time);

    // debug print
    print_matrix(c_matrix, m, n);

    free(a_matrix);
    free(b_matrix);
    free(c_matrix);

    return EXIT_SUCCESS;
}

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n, float *alpha) {
    if (argc != 5)
        return 1;

    // parse string to integer
    *m = atoi(argv[1]);
    *k = atoi(argv[2]);
    *n = atoi(argv[3]);
    *alpha = atof(argv[4]);

    // success if m is greater than 0 and divisible by mpi_size
    // return (*m <= 0) || (*m % mpi_size);
    return 0;
}

void fill_matrix(float *matrix, int32_t rows, int32_t cols) {
    for (int32_t i = 0; i < rows; i++) {
        for (int32_t j = 0; j < cols; j++) {
            matrix[i * cols + j] = (float)(i);
        }
    }
}

void multiply_matrix(float *a_matrix, float *b_matrix, float *c_matrix,
                     int32_t m, int32_t k, int32_t n, float alpha) {

    for (int32_t i = 0; i < m; i++) {
        for (int32_t j = 0; j < n; j++) {
            for (int32_t l = 0; l < k; l++) {
                c_matrix[i * n + j] +=
                    alpha * a_matrix[i * k + l] * b_matrix[l * n + j];
            }
        }
    }
}

void print_matrix(float *matrix, int32_t rows, int32_t cols) {

    for (int32_t i = 0; i < rows; i++) {
        for (int32_t j = 0; j < cols; j++) {
            printf("%1.1f ", matrix[i * cols + j]);
        }
        printf("\n");
    }
    printf("\n");
}