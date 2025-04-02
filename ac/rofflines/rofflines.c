#include <papi.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#define N 1000000
typedef long long int int128_t;

void handle_error(int32_t retval) {
  fprintf(stderr, "PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(EXIT_FAILURE);
}

int32_t main(void) {
  float r = 0;
  float *a, *b;
  int32_t retval, eventset = PAPI_NULL;
  int32_t events[] = {PAPI_TOT_CYC, PAPI_FP_OPS};
  int128_t values[sizeof(events) / sizeof(int32_t)];

  if ((retval = PAPI_library_init(PAPI_VER_CURRENT)) != PAPI_VER_CURRENT)
    handle_error(retval);
  printf("quien es?\n");
  if ((retval = PAPI_create_eventset(&eventset)) != PAPI_OK)
    handle_error(retval);
  printf("soy yo\n");
  for (int i = 0; i < sizeof(events) / sizeof(int32_t); ++i) {
    if ((retval = PAPI_add_event(eventset, events[i])) != PAPI_OK)
      handle_error(retval);
  }
  printf("que vienes a buscar\n");

  a = malloc(sizeof(float) * N);
  b = malloc(sizeof(float) * N);
  if (a == NULL || b == NULL) {
    fprintf(stderr, "Memory allocation failed\n");
    return EXIT_FAILURE;
  }
  for (uint32_t i = 0; i < N; ++i) {
    a[i] = (float)i;
    b[i] = (float)i * 2.0f;
  }
  printf("a ti\n");

  if ((retval = PAPI_reset(eventset) != PAPI_OK))
    handle_error(retval);
  if ((retval = PAPI_start(eventset) != PAPI_OK))
    handle_error(retval);

  for (uint32_t i = 0; i < N; ++i) {
    r += a[i] * b[i];
  }

  if ((retval = PAPI_stop(eventset, values)) != PAPI_OK)
    handle_error(retval);
  for (int i = 0; i < sizeof(events) / sizeof(int); ++i) {
    printf("Event %d: %lld\n", events[i], values[i]);
  }

  PAPI_cleanup_eventset(eventset);
  PAPI_destroy_eventset(&eventset);
  return EXIT_SUCCESS;
}