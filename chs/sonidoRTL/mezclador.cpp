#include"mezclador.h"


void mezclador::registros() {

	if (rst.read()) {
		regSample1 = regSample2 = 0;
		regNivel1 = regNivel2 = 0;
	}
	else {
		regSample1 = sample1.read();
		regSample2 = sample2.read();
		regNivel1 = nivel1.read();
		regNivel2 = nivel2.read();
	}
	fireRegs.write(!fireRegs.read());
}

void mezclador::mezclar(){

	sc_uint<20> prod1, prod2, suma;

	prod1 = regSample1 * regNivel1;
	prod2 = regSample2 * regNivel2;
	suma = prod1 + prod2; 

	sampleOut.write( suma(19,8) );

}

