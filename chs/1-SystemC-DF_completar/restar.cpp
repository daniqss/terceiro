#include "restar.h"


void restar::procRestar() {

	sc_int<12> dIn1, dIn2;

	while (true) {
		dataIn1->read(dIn1);
		dataIn2->read(dIn2);

		// cout << "estamos restando " << dIn1 << " con " << dIn2 << endl;

		dataOut->write(dIn1 - dIn2);
	}
}
