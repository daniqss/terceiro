#include "sumar.h"


void sumar::procSumar() {

	sc_int<12> dIn1, dIn2;


	while (true) {
		sumar::dataIn->read(dIn1);
		sumar::dataIn->read(dIn2);

		sumar::dataOut->write(dIn1 + dIn2);
	}
}


