#include "envolvente.h"
#include "generadorFrecuencia.h"
#include <cmath>
#include <cstdint>

void envolvente::formar() {

  sc_uint<12> muestra;
  int idx;
  sc_uint<22> prod;
  double x, y;
  idx = 0;
  int32_t mt, t, t1, t2, t3, t4, t5, t6;

  while (true) {
    // una envolvente muy sencilla en forma de subida y bajada relativamente
    // suave y una meseta prolongada aqu� se pueden crear envolventes m�s
    // sofisticadas siempre se debe usar aritm�tica entera para conseguirlo, y
    // nunca usar funciones trascenentales

    for (int i = 0; i < SAMPLES_PER_SECOND; ++i) {
      sampleIn->read(muestra);

      if (i < 2755) {
        y = 1.12 * muestra;
      } else if (i < 6786) {
        y = 0.06 * muestra + 2919;
      } else if (i < 9425) {
        y = 1.5 * muestra - 6808;
      } else if (i < 19833) {
        y = -0.15 * muestra + 8733;
      } else if (i < 31566) {
        y = 0.078 * muestra + 4119;
      } else {
        y = -5 * muestra + 166820;
      }
      // if (i < 4096) { // rampa de subida
      // prod = (muestra * idx) >> 10;
      // muestra = prod;
      // ++idx;
      // } else if (i > (SAMPLES_PER_SECOND - 4096)) { // rampa de bajada
      //   prod = (muestra * idx) >> 10;
      //   muestra = prod;
      //   --idx;
      // }

      muestra = (int)y;
      sampleOut->write(muestra);
    }
  }
}
