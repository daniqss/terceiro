#include <mpi.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#define MAX 100
#define MASTER 0
#define DEBUG 1

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n, float *alpha, int32_t mpi_size);
void fill_matrix(float *matrix, int32_t rows, int32_t cols);
void print_matrix(float *matrix, int32_t rows, int32_t cols);

int32_t main(int32_t argc, char *argv[]) {
    // matrix dimensions
    int32_t m, k, n;
    float alpha;
    int mpi_size, mpi_rank;
    float *a_matrix, *b_matrix, *c_matrix;
    float *local_a, *local_c;
    int rows_per_block;
    double time;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &mpi_size);
    MPI_Comm_rank(MPI_COMM_WORLD, &mpi_rank);

    if (mpi_rank == MASTER) {
        if (manage_args(argc, argv, &m, &k, &n, &alpha, mpi_size)) {
            fprintf(stderr, "usage: mpirun -np 4 ./%s <m> <n> <k> <alpha>\n",
                    argv[0]);
            MPI_Finalize();
            return EXIT_FAILURE;
        };
    }

    // alloc and initialize matrices
    if (mpi_rank == MASTER) {
        a_matrix = (float *)malloc(m * k * sizeof(float));
        c_matrix = (float *)malloc(m * n * sizeof(float));
        b_matrix = (float *)malloc(k * n * sizeof(float));

        fill_matrix(a_matrix, m, k);
        fill_matrix(b_matrix, k, n);
    } else {
        b_matrix = (float *)malloc(k * n * sizeof(float));
        rows_per_block = m / mpi_size;
        local_a = (float *)malloc(rows_per_block * k * sizeof(float));
        local_c = (float *)malloc(rows_per_block * n * sizeof(float));
    }

    // start timer, after allocations and before communications
    MPI_Barrier(MPI_COMM_WORLD);
    time = MPI_Wtime();

    // distribute matrices between processes
    MPI_Scatter(a_matrix, rows_per_block * k, MPI_FLOAT, local_a,
                rows_per_block * k, MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    MPI_Scatter(c_matrix, rows_per_block * n, MPI_FLOAT, local_c,
                rows_per_block * n, MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    MPI_Bcast(b_matrix, k * n, MPI_FLOAT, MASTER, MPI_COMM_WORLD);

    // matrix multiplication of a_matrix and b_matrix, stored in c_matrix
    multiply_matrix(a_matrix, b_matrix, c_matrix, m, k, n, alpha);

    if (mpi_rank == MASTER && DEBUG) {
        print_matrix(a_matrix, m, k);
        print_matrix(b_matrix, k, n);
        print_matrix(c_matrix, m, n);
    }

    free(a_matrix);
    free(b_matrix);
    free(c_matrix);

    MPI_Finalize();
    return EXIT_SUCCESS;
}

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n, float *alpha, int32_t mpi_size) {
    if (argc != 4)
        return 1;

    // parse string to integer
    *m = atoi(argv[1]);
    *k = atoi(argv[2]);
    *n = atoi(argv[3]);

    // success if m is greater than 0 and divisible by mpi_size
    return (*m <= 0) || (*m % mpi_size);
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
