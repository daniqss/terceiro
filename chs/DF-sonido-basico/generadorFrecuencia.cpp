#include"generadorFrecuencia.h"
#include<stdio.h>

#define SPY 2000

void generadorFrecuencia::generar(){

	sc_uint<5> freq;
	sc_uint<16> muestra, posicion;
	sc_uint<16> indice;
	sc_uint<32> producto;

	while(true){
		nota->read( freq );			// lee la nota

		dirA->write( freq );
		datoA->read( posicion );	// lee la "aceleración" de la nota con respecto al DO básico

		indice = 0;
		for (int i = 0; i < SAMPLES_PER_SECOND; ++i) {	// genera muestras equivalentes a un segundo de esta nota
			producto = posicion * indice;		// acelera el índice básico
			producto >>= 13;					// trunca el índice acelerado
			producto &= 0x7f;					// la lista de muestras es de sólo 128 elementos
			dirB->write(producto + 256);		
			datoB->read(muestra);				// lee la muestra de sonido 
			dOut->write((sc_uint<12>) muestra);	

			++indice;
		}
	}
}
