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

int32_t main(int32_t argc, char *argv[]) {
    // matrix dimensions
    int32_t m, k, n;
    float alpha;
    int32_t mpi_size, mpi_rank;
    float *a_matrix, *b_matrix, *c_matrix;
    float *local_a, *local_c;
    int32_t *a_sendcounts, *a_displs, *c_sendcounts, *c_displs;
    int32_t a_section_size, c_section_size;
    double time;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &mpi_size);
    MPI_Comm_rank(MPI_COMM_WORLD, &mpi_rank);

    if (mpi_rank == MASTER) {
        if (manage_args(argc, argv, &m, &k, &n, &alpha)) {
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
    } else {
        b_matrix = (float *)malloc(k * n * sizeof(float));
    }

    // calculate displacements and sendcounts for a and c matrices distribution
    // how much data each process will receive
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

    if (mpi_rank == MASTER) {
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
    }

    MPI_Barrier(MPI_COMM_WORLD);
    time = MPI_Wtime();

    // send section sizes to all processes
    MPI_Scatter(a_sendcounts, 1, MPI_INT, &a_section_size, 1, MPI_INT, MASTER,
                MPI_COMM_WORLD);
    MPI_Scatter(c_sendcounts, 1, MPI_INT, &c_section_size, 1, MPI_INT, MASTER,
                MPI_COMM_WORLD);

    local_a = (float *)malloc(a_section_size * sizeof(float));
    local_c = (float *)malloc(c_section_size * sizeof(float));

    // distribute matrices between processes using Scatterv
    // with sendcount and displs depending on the process mpi_rank
    MPI_Scatterv(a_matrix, a_sendcounts, a_displs, MPI_FLOAT, local_a,
                 a_section_size, MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    MPI_Scatterv(c_matrix, c_sendcounts, c_displs, MPI_FLOAT, local_c,
                 c_section_size, MPI_FLOAT, MASTER, MPI_COMM_WORLD);
    MPI_Bcast(b_matrix, k * n, MPI_FLOAT, MASTER, MPI_COMM_WORLD);

    // matrix multiplication of a_matrix and b_matrix, stored in c_matrix
    multiply_matrix(local_a, b_matrix, local_c, a_section_size / k, k, n,
                    alpha);

    // send results to process 0
    MPI_Gatherv(local_c, c_section_size, MPI_FLOAT, c_matrix, c_sendcounts,
                c_displs, MPI_FLOAT, MASTER, MPI_COMM_WORLD);
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
