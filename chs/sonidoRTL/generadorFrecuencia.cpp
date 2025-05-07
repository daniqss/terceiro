#include"generadorFrecuencia.h"


void generadorFrecuencia::generar(){

	sc_uint<16> posicion, muestra; 

	dirA->write( regNota );
	posicion = datoA.read();
	producto = posicion * indice;		// acelera el índice básico
	producto >>= 13;					// trunca el índice acelerado
	producto &= 0x7f;					// la lista de muestras es de sólo 128 elementos
	dirB->write(producto + 256);
	muestra = datoB->read();			// lee la muestra de sonido 
	dOut->write((sc_uint<12>) muestra);
}

void generadorFrecuencia::registros(){

	if(rst){	
		regNota = 0;
		indice = 0;
	}else{	
		if (start.read()) {
			regNota = nota->read();
			indice = 1;
		}else{
			if (indice) {
				if (indice == SAMPLES_PER_SECOND)
					indice = 0;
				else
					++indice;
			}
		}
	}
	fireRegs.write(!fireRegs.read());
}
