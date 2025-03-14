#include"mezclador.h"

void mezclador::mezclar(){

	sc_uint<8> peso1, peso2;
	sc_uint<12> muestra1, muestra2; 
	sc_uint<20> prod1, prod2, suma;

	nivel1->read(peso1);
	nivel2->read(peso2);

	while(true){
		sample1->read(muestra1);
		sample2->read(muestra2);

		prod1 = muestra1 * peso1; 
		prod2 = muestra2 * peso2;
		suma = prod1 + prod2; 

		sampleOut->write( suma(19,8) );
	}
}
