#ifndef CONSUMIDOR_H
#define CONSUMIDOR_H


#include "systemc.h"
#include "fifo.h"
#include <stdio.h>


SC_MODULE (consumidor) {
public:
sc_port<read_if_T<sc_int<12>>> dataIn; 

SC_HAS_PROCESS(consumidor);



void consumir() {

	sc_int<12> dIn;
	int ref; 

	int i;

	for (i = 0; i < 20; ++i) {
		dataIn->read(dIn);
		fscanf(fichero, "%d ", &ref);

		// cout << "hemos consumido " << dIn << endl;

		if (ref != dIn.to_int()) {
			printf("Error. Se esperaba %d, se ha recibido %d\n", ref, dIn.to_int());
		}
	}

	printf("FINAL, comprueba las lineas anteriores por si ha habido errores.\n");
	fclose(fichero);
	sc_stop();

}


consumidor(sc_module_name name_, char *fileName) : sc_module(name_){

	cout<<"consumidor: " << name() << "  " << fileName << endl;


	fichero = fopen(fileName, "rt");

	
	if (!fichero) {
		fprintf(stderr, "No es posible abrir el fichero %s\n", fileName);
		exit(-1);
	}
	
	SC_THREAD(consumir);


}

private:
FILE *fichero;
}; 


#endif