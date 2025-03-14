#include"envolvente.h"
#include"generadorFrecuencia.h"

void envolvente::formar(){

	sc_uint<12> muestra; 
	int indice;
	sc_uint<22> prod;

	while(true){

		indice = 0; 

		// una envolvente muy sencilla en forma de subida y bajada relativamente suave y una meseta prolongada
		// aquí se pueden crear envolventes más sofisticadas
		// siempre se debe usar aritmética entera para conseguirlo, y nunca usar funciones trascenentales 

		for (int i = 0; i < SAMPLES_PER_SECOND; ++i) {

			sampleIn->read(muestra);

			if (i < 1024) {		// rampa de subida
				prod = (muestra * indice) >> 10;
				muestra = prod;
				++indice;
			}

			if (i > (SAMPLES_PER_SECOND - 1024)) {		// rampa de bajada
				prod = (muestra * indice) >> 10;
				muestra = prod;
				--indice;
			}

			sampleOut->write( muestra );

		}
	}

}
