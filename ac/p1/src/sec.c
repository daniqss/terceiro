#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#define MAX 100

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n);
void fill_matrix(float **matrix, int32_t rows, int32_t cols);
void print_matrix(float **matrix, int32_t rows, int32_t cols);

int32_t main(int32_t argc, char *argv[]) {
    int32_t m, k, n;
    float **a_matrix, **b_matrix, **c_matrix;
    float alpha = 3.0;

    if (manage_args(argc, argv, &m, &k, &n)) {
        fprintf(stderr, "usage: mpirun -np 4 ./%s <m> <n> <k>\n", argv[0]);
        return EXIT_FAILURE;
    };

    // alloc and initialize matrices
    a_matrix = (float **)malloc(m * sizeof(float *));
    b_matrix = (float **)malloc(k * sizeof(float *));
    c_matrix = (float **)malloc(m * sizeof(float *));
    for (int32_t i = 0; i < m; i++) {
        a_matrix[i] = (float *)malloc(k * sizeof(float));
        b_matrix[i] = (float *)malloc(n * sizeof(float));
        c_matrix[i] = (float *)malloc(n * sizeof(float));
    }
    fill_matrix(a_matrix, m, k);
    fill_matrix(b_matrix, k, n);

    // matrix multiplication of a_matrix and b_matrix, stored in c_matrix
    for (int32_t i = 0; i < m; i++) {
        for (int32_t j = 0; j < n; j++) {
            c_matrix[i][j] = 0;
            for (int32_t l = 0; l < k; l++) {
                c_matrix[i][j] += alpha * a_matrix[i][l] * b_matrix[l][j];
            }
        }
    }

    print_matrix(a_matrix, m, k);
    print_matrix(b_matrix, k, n);
    print_matrix(c_matrix, m, n);

    // freed memory
    for (int32_t i = 0; i < m; i++) {
        free(a_matrix[i]);
        free(b_matrix[i]);
        free(c_matrix[i]);
    }
    free(a_matrix);
    free(b_matrix);
    free(c_matrix);

    return EXIT_SUCCESS;
}

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n) {
    if (argc != 4)
        return 1;

    // parse string to integer
    *m = atoi(argv[1]);
    *k = atoi(argv[2]);
    *n = atoi(argv[3]);
    return 0;
}

void fill_matrix(float **matrix, int32_t rows, int32_t cols) {
    for (int32_t i = 0; i < rows; i++) {
        for (int32_t j = 0; j < cols; j++) {
            // matrix[i][j] = (float)(rand() % MAX);
            matrix[i][j] = (float)i;
        }
    }
}

void print_matrix(float **matrix, int32_t rows, int32_t cols) {
    for (int32_t i = 0; i < rows; i++) {
        for (int32_t j = 0; j < cols; j++) {
            printf("%1.1f ", matrix[i][j]);
        }
        printf("\n");
    }
    printf("\n");
}