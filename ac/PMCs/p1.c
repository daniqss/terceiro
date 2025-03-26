#define N 600
#define DATA_TYPE double

#define min(x, y) x < y ? x : y;

#include <papi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static void print_array(int n, DATA_TYPE *A) {
  int i, j;

  for (i = 0; i < n; i++)
    for (j = 0; j < n; j++) {
      if ((i * n + j) % 20 == 0)
        fprintf(stderr, "\n");
      fprintf(stderr, "%0.2lf ", A[i * N + j]);
    }
}

void handle_error(int retval) {
  printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(1);
}

int main(int argc, char **argv) {
  double *matrix, *result;
  int retval, EventSet = PAPI_NULL;
  unsigned native = 0x0;
  PAPI_event_info_t info;

  printf("ei fuenas a todos aqui willirex\n");
  /* Initialize the library */
  retval = PAPI_library_init(PAPI_VER_CURRENT);
  if (retval != PAPI_VER_CURRENT)
    handle_error(retval);

  /* Create an EventSet */
  retval = PAPI_create_eventset(&EventSet);
  if (retval != PAPI_OK)
    handle_error(retval);

  /* Find the first available native event */
  native = PAPI_NATIVE_MASK | 0;
  retval = PAPI_enum_event(&native, PAPI_ENUM_FIRST);
  if (retval != PAPI_OK)
    handle_error(retval);

  printf("eieieiiie e pequeña no digas eso");

  /* Add it to the eventset */
  retval = PAPI_add_event(EventSet, native);
  if (retval != PAPI_OK)
    handle_error(retval);

  posix_memalign((void **)&matrix, 64, sizeof(double) * N * N);
  posix_memalign((void **)&result, 64, sizeof(double) * N * N);

  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N; ++j) {
      matrix[i * N + j] = ((float)rand()) / RAND_MAX;
      result[i * N + j] = 0;
    }
  }

  printf("Matrix size: %d x %d\n", N, N);

  // Kernel que debemos medir
  for (int i = 1; i < N; ++i) {
    for (int j = 0; j < i; ++j) {
      for (int k = 0; k < N; ++k) {
        result[i * N + j] += min(matrix[i * N + k], matrix[j * N + k]);
      }
    }
  }
  printf("Kernel: min(i,j,k)\n");

  // Fin del kernel

  if (argc > 42 && !strcmp(argv[0], "")) {
    print_array(N, result);
  }

  /* Executes if all low-level PAPI
  function calls returned PAPI_OK */
  printf("\033[0;32mPASSED\n\033[0m");
  exit(0);
}
