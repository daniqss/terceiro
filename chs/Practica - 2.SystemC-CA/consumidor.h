#ifndef CONSUMIDOR_H
#define CONSUMIDOR_H


#include "systemc.h"
#include <stdio.h>


SC_MODULE (consumidor) {
public:

	sc_in<bool> clk, reset;
	sc_in <bool> validIn;
	sc_in<sc_int<12>> dataIn; 

SC_HAS_PROCESS(consumidor);



void consumir() {

	sc_int<12> dIn;
	int ref; 

	if (reset.read()) {
		contador = 0;
	}
	else {
		if (validIn.read()) {
			dIn = dataIn.read();
			fscanf(fichero, "%d ", &ref);

			if (ref != dIn.to_int()) {
				printf("Error. Se esperaba %d en la posici�on %d, se ha recibido %d\n", ref, contador, dIn.to_int());
			}
			contador++;
			if (contador == 20) {
				printf("\n\n\n\nFINAL, comprueba las lineas anteriores por si ha habido errores.\n\n\n");
				fclose(fichero);
				sc_stop();
			}
		}
	}

}


consumidor(sc_module_name name_, char *fileName) : sc_module(name_){

	cout<<"consumidor: " << name() << "  " << fileName << endl;


	fichero = fopen(fileName, "rt");

	
	if (!fichero) {
		fprintf(stderr, "No es posible abrir el fichero %s\n", fileName);
		exit(-1);
	}
	
	contador = 0; 

	SC_METHOD(consumir);
	sensitive << clk.pos();


}

private:

	FILE *fichero;
	int contador;

}; 


#endif // CONSUMIDOR_H