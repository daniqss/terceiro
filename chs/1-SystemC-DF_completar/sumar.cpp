#include "sumar.h"


void sumar::procSumar() {

	sc_int<12> dIn1, dIn2;


	while (true) {
		sumar::dataIn->read(dIn1);
		sumar::dataIn->read(dIn2);
		
		// cout << "estamos sumando " << dIn1 << " con " << dIn2 << endl;

		sumar::dataOut->write(dIn1 + dIn2);
	}
}


