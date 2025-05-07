#include "envolvente.h"
#include "generadorFrecuencia.h"

void envolvente::registros() {

  if (rst.read()) {
    valid = sample = nivel = 0;
    indice = 0;
  } else {
    sample = sampleIn.read();
    if (start.read()) {
      indice = 1;
      nivel = 0;
    } else {
      if (indice) {
        if (indice == SAMPLES_PER_SECOND)
          indice = 0;
        else
          ++indice;
      }
      nivel = newNivel;
    }
  }
  fireRegs.write(!fireRegs.read());
}

void envolvente::formar() {

  sc_uint<24> prod;

  prod = sample * nivel;

  newNivel = nivel;
  if (indice < 4096)
    newNivel = indice;
  if (indice > SAMPLES_PER_SECOND)
    newNivel = 0;
  else if (indice > (SAMPLES_PER_SECOND - 4096))
    newNivel = nivel - 1;

  sampleOut.write(prod(23, 12));
  valOut.write(indice != 0);
}
