#include "mcd.h"
#include <iostream>

void mcd::FSM() {

  sc_int<16> tmp;
  int miraA, miraB;

  miraA = A.read(); // utilizad estas variables para depurar
  miraB = B.read();

  listo.write(false);
  if (reset.read()) {
    estado = ini;
    A.write(0);
    B.write(0);
  } else {
    switch (estado) {
    case ini:
      estado = valIn.read() ? restar : ini;

      A.write(static_cast<sc_int<16>>(entA->read()));
      B.write(static_cast<sc_int<16>>(entB->read()));

      break;
    case restar:
      estado = cero ? fin : signo ? aMenor : aMayor;

      break;
    case aMayor:
      A.write(resta);

      estado = restar;

      break;
    case aMenor:
      tmp = A.read();
      A.write(B.read());
      B.write(tmp);

      estado = restar;
      break;
    case fin:
      listo.write(true);

      estado = valIn->read() ? fin : ini;
      break;
    default:
      cerr << "Error, estado inexistente: " << estado << endl;
      break;
    };
  }
  resultado.write(A.read().to_uint());

#ifdef trazas
  estadoSig.write(estado);
#endif
}

void mcd::calcular() { // no tocar

  resta = A.read() - B.read();
  signo = resta.bit(15);
  cero = (resta == 0);

#ifdef trazas
  restaSig.write(resta);
  signoSig.write(signo);
  ceroSig.write(cero);
#endif
}
