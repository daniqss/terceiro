#include <mpi.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#define MAX 100
#define MASTER 0
#ifndef DEBUG
#define DEBUG 1
#endif

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n, float *alpha, int32_t mpi_size);
void fill_matrix(float *matrix, int32_t rows, int32_t cols);
void multiply_matrix(float *a_matrix, float *b_matrix, float *c_matrix,
                     int32_t m, int32_t k, int32_t n, float alpha);
void print_matrix(float *matrix, int32_t rows, int32_t cols);

int32_t main(int32_t argc, char *argv[]) {
    // matrix dimensions
    int32_t m, k, n;
    float alpha;
    int mpi_size, mpi_rank;
    float *a_matrix, *b_matrix, *c_matrix;
    float *local_a, *local_c;
    int a_section_size, c_section_size, common_section_size;
    double time;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &mpi_size);
    MPI_Comm_rank(MPI_COMM_WORLD, &mpi_rank);

    if (mpi_rank == MASTER) {
        if (manage_args(argc, argv, &m, &k, &n, &alpha, mpi_size)) {
            fprintf(stderr, "usage: mpirun -np %d ./%s <m> <n> <k> <alpha>\n",
                    mpi_size, argv[0]);
            MPI_Finalize();
            return EXIT_FAILURE;
        };
    }

    MPI_Bcast(&m, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&n, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&k, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&alpha, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    // alloc and initialize matrices
    if (mpi_rank == MASTER) {
        a_matrix = (float *)malloc(m * k * sizeof(float));
        c_matrix = (float *)malloc(m * n * sizeof(float));
        b_matrix = (float *)malloc(k * n * sizeof(float));

        fill_matrix(a_matrix, m, k);
        fill_matrix(b_matrix, k, n);
        // } else {
    } else {
        b_matrix = (float *)malloc(k * n * sizeof(float));
    }

    common_section_size = m / mpi_size;
    a_section_size = k * common_section_size;
    c_section_size = n * common_section_size;
    local_a = (float *)malloc(a_section_size * sizeof(float));
    local_c = (float *)malloc(c_section_size * sizeof(float));

    // start timer, after allocations and before communications
    MPI_Barrier(MPI_COMM_WORLD);
    time = MPI_Wtime();

    // distribute matrices between processes
    MPI_Scatter(a_matrix, a_section_size, MPI_FLOAT, local_a, a_section_size,
                MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    MPI_Scatter(c_matrix, c_section_size, MPI_FLOAT, local_c, c_section_size,
                MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    MPI_Bcast(b_matrix, k * n, MPI_FLOAT, MASTER, MPI_COMM_WORLD);

    // matrix multiplication of a_matrix and b_matrix, stored in c_matrix
    multiply_matrix(local_a, b_matrix, local_c, common_section_size, k, n,
                    alpha);

    // gather results
    MPI_Gather(local_c, c_section_size, MPI_FLOAT, c_matrix, c_section_size,
               MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    time = MPI_Wtime() - time;

    // debug print
    if (DEBUG)
        printf("time used by proccess %d -> %f\n", mpi_rank, time);
    fflush(NULL);
    MPI_Barrier(MPI_COMM_WORLD);

    if (mpi_rank == MASTER) {
        puts("");
        print_matrix(c_matrix, m, n);
        free(a_matrix);
        free(c_matrix);
        free(b_matrix);
    } else {
        free(local_a);
        free(local_c);
        free(b_matrix);
    }

    MPI_Finalize();
    return EXIT_SUCCESS;
}

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n, float *alpha, int32_t mpi_size) {
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
            c_matrix[i * n + j] = 0;
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
