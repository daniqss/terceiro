#include "sumar.h"


void sumar::procSumar() {

	if (nDatos == 2) {

		dataOut->write(dato1 + dato2);
		validOut.write(true);
		// cout << "Suma: " << dato1 << " + " << dato2 << " = " << dato1 + dato2 << endl;
	}
	else {
        dataOut.write(0); 
        validOut.write(false);
	}

	next.write(!reset.read());
	
}


void sumar::registers() {

	if (reset.read()) {
		nDatos = 0;
		dato1 = dato2 = 0;
	}
	else {
		if (validIn.read()) {
			if (nDatos != 1) {
				nDatos = 1;
				dato1 = dataIn.read();
			}
			else {
				nDatos = 2;
				dato2 = dataIn.read();
			}
		}
		else {
			if (nDatos == 2)
				nDatos = 0;
		}
	}

	FIRE.write(!FIRE.read());

}