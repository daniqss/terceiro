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
		datoA->read( posicion );	// lee la "aceleraci�n" de la nota con respecto al DO b�sico

		indice = 0;
		for (int i = 0; i < SAMPLES_PER_SECOND; ++i) {	// genera muestras equivalentes a un segundo de esta nota
			producto = posicion * indice;		// acelera el �ndice b�sico
			producto >>= 13;					// trunca el �ndice acelerado
			producto &= 0x7f;					// la lista de muestras es de s�lo 128 elementos
			dirB->write(producto + 256);		
			datoB->read(muestra);				// lee la muestra de sonido 
			dOut->write((sc_uint<12>) muestra);	

			++indice;
		}
	}
}
