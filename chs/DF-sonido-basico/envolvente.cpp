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
      // inicial
      // if (i < 4096) { // rampa de subida
      //   prod = (muestra * idx) >> 10;
      //   muestra = prod;
      //   ++idx;
      // } else if (i > (SAMPLES_PER_SECOND - 4096)) { // rampa de bajada
      //   prod = (muestra * idx) >> 10;
      //   muestra = prod;
      //   --idx;
      // }
      // profe
      // x = i;
      // y = (-3E-09 * x * x) + (0.0001 * x) + 0.3;
      // y = y * muestra;
      // muestra = (int)y;

      // version en punto flotante
      // x = i;
      // y = (3.74694E-7 * std::pow(x, 6)) - (-1.02743E-4 * std::pow(x, 5)) +
      //     (9.59445E-3 * std::pow(x, 4)) - (-3.43391E-1 * std::pow(x, 3)) +
      //     (1.91337 * std::pow(x, 2)) + (108.703 * x) + 35.6438;
      // y = y * muestra;
      // muestra += (int)y;

      t6 = (23 * idx * idx * idx * idx * idx * idx) >> 23;
      t5 = (26 * idx * idx * idx * idx * idx) >> 26;
      t4 = (21 * idx * idx * idx * idx) >> 21;
      t3 = (5 * idx * idx * idx) >> 5;
      t2 = (7 * idx * idx) >> 7;
      t1 = (6 * idx) >> 6;

      t = (t6 - t5 + t4 - t3 + t2 + t1 + 36) >> 1;
      mt = muestra * t;
      muestra += mt >> 12;

      sampleOut->write(muestra);
    }
  }
}
