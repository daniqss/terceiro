#include <papiStdEventDefs.h>
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
  int retval, eventset = PAPI_NULL;
  int events[] = {
      PAPI_TOT_CYC, // bien
      // PAPI_REF_CYC, // no existe
      // PAPI_TOT_INS, // si existe pero no se puede contar por hardware??
      // PAPI_L1_TCM,  // no existe
      // PAPI_L2_TCM,  // no existe
      // PAPI_L3_TCM,  // no existe
      // PAPI_LD_INS, // no existe
      // PAPI_SR_INS, // no existe
      PAPI_FP_OPS, // yupi si existe
  };
  long long values[sizeof(events) / sizeof(int)];

  // iniciar papi
  if ((retval = PAPI_library_init(PAPI_VER_CURRENT)) != PAPI_VER_CURRENT)
    handle_error(retval);

  // crear un eventset al q añadiremos eventos q queramos controlar
  if ((retval = PAPI_create_eventset(&eventset)) != PAPI_OK)
    handle_error(retval);

  for (int i = 0; i < sizeof(events) / sizeof(int); ++i) {
    if ((retval = PAPI_add_event(eventset, events[i])) != PAPI_OK)
      handle_error(retval);
  }

  posix_memalign((void **)&matrix, 64, sizeof(double) * N * N);
  posix_memalign((void **)&result, 64, sizeof(double) * N * N);

  for (int i = 0; i < N; ++i) {
    for (int j = 0; j < N; ++j) {
      matrix[i * N + j] = ((float)rand()) / RAND_MAX;
      result[i * N + j] = 0;
    }
  }

  printf("Matrix size: %d x %d\n", N, N);

  // empezamos a medir
  if ((retval = PAPI_reset(eventset) != PAPI_OK))
    handle_error(retval);
  if ((retval = PAPI_start(eventset) != PAPI_OK))
    handle_error(retval);

  // kernel a medir
  for (int i = 1; i < N; ++i) {
    for (int j = 0; j < i; ++j) {
      for (int k = 0; k < N; ++k) {
        result[i * N + j] += min(matrix[i * N + k], matrix[j * N + k]);
      }
    }
  }

  // fin del kernel
  // detenemos por tanto la medicion
  if ((retval = PAPI_stop(eventset, values)) != PAPI_OK)
    handle_error(retval);
  for (int i = 0; i < sizeof(events) / sizeof(int); ++i) {
    printf("Event %d: %lld\n", events[i], values[i]);
  }

  if (argc > 42 && !strcmp(argv[0], "")) {
    print_array(N, result);
  }

  PAPI_cleanup_eventset(eventset);
  PAPI_destroy_eventset(&eventset);
  return EXIT_SUCCESS;
}
