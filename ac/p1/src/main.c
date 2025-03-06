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
                    int32_t *n, float *alpha);
void fill_matrix(float *matrix, int32_t rows, int32_t cols);
void multiply_matrix(float *a_matrix, float *b_matrix, float *c_matrix,
                     int32_t m, int32_t k, int32_t n, float alpha);
void print_matrix(float *matrix, int32_t rows, int32_t cols);
void calculate_distribution(int32_t total_size, int32_t elements_per_row,
                            int32_t mpi_size, int32_t mpi_rank,
                            int32_t *section_size, int32_t *displacement);

int32_t main(int32_t argc, char *argv[]) {
    int32_t params[3];
    int32_t m, k, n;
    float alpha;
    int32_t mpi_size, mpi_rank;
    float *a_matrix, *b_matrix, *c_matrix;
    float *local_a, *local_c;
    int32_t *a_sendcounts, *a_displs, *c_sendcounts, *c_displs;
    int32_t a_section_size, c_section_size;
    int32_t a_displacement, c_displacement;
    double time;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &mpi_size);
    MPI_Comm_rank(MPI_COMM_WORLD, &mpi_rank);

    if (mpi_rank == MASTER) {
        if (manage_args(argc, argv, &params[0], &params[1], &params[2],
                        &alpha)) {
            fprintf(stderr, "usage: mpirun -np %d ./%s <m> <n> <k> <alpha>\n",
                    mpi_size, argv[0]);
            MPI_Finalize();
            return EXIT_FAILURE;
        };
    }

    MPI_Bcast(params, 3, MPI_INT, MASTER, MPI_COMM_WORLD);
    MPI_Bcast(&alpha, 1, MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    m = params[0], k = params[1], n = params[2];

    // alloc and initialize matrices
    if (mpi_rank == MASTER) {
        a_matrix = (float *)malloc(m * k * sizeof(float));
        c_matrix = (float *)malloc(m * n * sizeof(float));
        b_matrix = (float *)malloc(k * n * sizeof(float));

        fill_matrix(a_matrix, m, k);
        fill_matrix(b_matrix, k, n);

        // calculate displacements and sendcounts for a and c matrices
        // distribution how much data each process will receive
        a_sendcounts = (int32_t *)malloc(mpi_size * sizeof(int32_t));
        c_sendcounts = (int32_t *)malloc(mpi_size * sizeof(int32_t));

        // offset for each process from which it will receive data
        a_displs = (int32_t *)malloc(mpi_size * sizeof(int32_t));
        c_displs = (int32_t *)malloc(mpi_size * sizeof(int32_t));

        // calculate the remainder
        int32_t remainder_a = m % mpi_size;
        int32_t remainder_c = m % mpi_size;
        // calculate the base size(in rows) for each process
        int32_t a_base_size = m / mpi_size;
        int32_t c_base_size = m / mpi_size;

        int32_t disp_a = 0;
        int32_t disp_c = 0;

        // for each process calculate how much data it will receive
        for (int32_t i = 0; i < mpi_size; i++) {
            // if remainder is greater or equal to i then process i will receive
            // one more row (+ k elements)
            // the i first processes will receive one more row
            a_sendcounts[i] = a_base_size * k + (i < remainder_a ? k : 0);
            a_displs[i] = disp_a;
            // update the offset
            disp_a += a_sendcounts[i];

            // same for c matrix but with n colunms
            c_sendcounts[i] = c_base_size * n + (i < remainder_c ? n : 0);
            c_displs[i] = disp_c;
            disp_c += c_sendcounts[i];
        }
    } else {
        b_matrix = (float *)malloc(k * n * sizeof(float));
    }

    // each process calculates its own section size and displacement
    calculate_distribution(m, k, mpi_size, mpi_rank, &a_section_size,
                           &a_displacement);
    calculate_distribution(m, n, mpi_size, mpi_rank, &c_section_size,
                           &c_displacement);

    MPI_Barrier(MPI_COMM_WORLD);
    time = MPI_Wtime();

    local_a = (float *)malloc(a_section_size * sizeof(float));
    local_c = (float *)malloc(c_section_size * sizeof(float));

    // distribute matrices between processes using Scatterv
    MPI_Scatterv((mpi_rank == MASTER) ? a_matrix : NULL,
                 (mpi_rank == MASTER) ? a_sendcounts : NULL,
                 (mpi_rank == MASTER) ? a_displs : NULL, MPI_FLOAT, local_a,
                 a_section_size, MPI_FLOAT, MASTER, MPI_COMM_WORLD);

    MPI_Bcast(b_matrix, k * n, MPI_FLOAT, MASTER, MPI_COMM_WORLD);

    // matrix multiplication of a_matrix and b_matrix, stored in c_matrix
    multiply_matrix(local_a, b_matrix, local_c, a_section_size / k, k, n,
                    alpha);

    // send results to process 0
    MPI_Gatherv(local_c, c_section_size, MPI_FLOAT,
                (mpi_rank == MASTER) ? c_matrix : NULL,
                (mpi_rank == MASTER) ? c_sendcounts : NULL,
                (mpi_rank == MASTER) ? c_displs : NULL, MPI_FLOAT, MASTER,
                MPI_COMM_WORLD);

    time = MPI_Wtime() - time;

    if (DEBUG)
        printf("time used by proccess %d -> %f\n", mpi_rank, time);
    fflush(NULL);
    MPI_Barrier(MPI_COMM_WORLD);

    if (mpi_rank == MASTER) {
        if (DEBUG)
            printf("\n");
        print_matrix(c_matrix, m, n);

        free(a_matrix);
        free(c_matrix);
        free(b_matrix);
        free(a_sendcounts);
        free(a_displs);
        free(c_sendcounts);
        free(c_displs);
    } else {
        free(local_a);
        free(local_c);
        free(b_matrix);
    }

    MPI_Finalize();
    return EXIT_SUCCESS;
}

// calculate which section of the matrix each process will use
void calculate_distribution(int32_t total_size, int32_t elements_per_row,
                            int32_t mpi_size, int32_t mpi_rank,
                            int32_t *section_size, int32_t *displacement) {
    int32_t remainder = total_size % mpi_size;
    int32_t base_size = total_size / mpi_size;

    int32_t rows = base_size + (mpi_rank < remainder ? 1 : 0);

    *section_size = rows * elements_per_row;

    int32_t disp_rows = 0;

    for (int32_t i = 0; i < mpi_rank; i++) {
        disp_rows += base_size + (i < remainder ? 1 : 0);
    }

    *displacement = disp_rows * elements_per_row;
}

int32_t manage_args(int32_t argc, char *argv[], int32_t *m, int32_t *k,
                    int32_t *n, float *alpha) {
    if (argc != 5)
        return 1;

    // parse string to integer
    *m = atoi(argv[1]);
    *k = atoi(argv[2]);
    *n = atoi(argv[3]);
    // parse string to float
    *alpha = atof(argv[4]);

    return (*m <= 0) || (*k <= 0) || (*n <= 0);
}

void fill_matrix(float *matrix, int32_t rows, int32_t cols) {
    for (int32_t i = 0; i < rows; i++) {
        for (int32_t j = 0; j < cols; j++) {
            // matrix[i * cols + j] = (float)(i);
            matrix[i * cols + j] = (float)(i + 4.0);
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