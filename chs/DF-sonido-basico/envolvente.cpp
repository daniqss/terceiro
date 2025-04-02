#include "envolvente.h"
#include "generadorFrecuencia.h"
#include <cstdint>

void envolvente::formar() {

  sc_uint<12> muestra;
  int indice;
  int32_t t1, t2;
  sc_uint<22> prod;
  indice = 0;
  double x, y;

  while (true) {

    // una envolvente muy sencilla en forma de subida y bajada relativamente
    // suave y una meseta prolongada aqu� se pueden crear envolventes m�s
    // sofisticadas siempre se debe usar aritm�tica entera para conseguirlo, y
    // nunca usar funciones trascenentales

    for (int i = 0; i < SAMPLES_PER_SECOND; ++i) {

      sampleIn->read(muestra);

      //   if (i < 1024) { // rampa de subida
      //     prod = (muestra * indice) >> 10;
      //     muestra = prod;
      //     ++indice;
      //   }

      //   if (i > (SAMPLES_PER_SECOND - 1024)) { // rampa de bajada
      //     prod = (muestra * indice) >> 10;
      //     muestra = prod;
      //     --indice;
      //   }

      x = i;
      y = (-3E-09 * x * x) + (0.0001 * x) + 0.3;
      y = y * muestra;
      muestra = (int)y;

      // t2 = (-21 * i * i) >> 21;
      // t1 = (219 * i) >> 9;

      // y = t1 + t2 + 226;

      // muestra *= y;
      // muestra >>= 12;

      sampleOut->write(muestra);
    }
  }
}
