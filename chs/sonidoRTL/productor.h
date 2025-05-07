#ifndef PRODUCTOR_H
#define PRODUCTOR_H

// https://pianosage.blogspot.com/2011/11/simple-5-finger-piano-songs-for.html?msclkid=0656577fa60911ec868c79cbc1944372

#include "systemc.h"

SC_MODULE (productor) {
public:
	sc_in<bool> clk;
	sc_in<bool> rst;

	sc_out<sc_uint<5>>	nota1, nota2;
	sc_out<bool> start;
	sc_out<sc_uint<8>> nivel1, nivel2;


	void generar() {

		static short listaNotas[] = { 0, 2, 4, 5, 7, 9, 11, 12, 11, 9, 7, 5, 4, 2 };
		//static short listaNotas[] = {0, 2, 4, 0, 0, 2, 4, 0, 4, 5, 7, 4, 5, 7 };
		// En "generadorFrecuencia.h" se puede cambiar el número de notas

		if (rst.read()) {
			contNotas = contMuestras = 0;
			return;
		}

		nivel1->write(192);	// volumen de la nota dominante
		nivel2->write(64);	// volumen del único armónico que añado. Convendría poner más

		nota1.write(listaNotas[contNotas]);
		nota2.write(listaNotas[contNotas] + 12); // la misma nota en la siguiente octava

		if (contMuestras == 0) {
			start.write(true);
			++contMuestras;
		}
		else {
			start.write(false);
			++contMuestras;
			if (contMuestras == SAMPLES_PER_SECOND) {
				contMuestras = 0;
				++contNotas;
				if (contNotas == nNotas) {
					contMuestras = SAMPLES_PER_SECOND - 1;
					contNotas = nNotas - 1;	// nos quedamos en la última nota
				}
			}
		}

	}

  SC_CTOR(productor) {
    cout<<"productor: "<<name()<<endl;

    SC_METHOD(generar);
	sensitive << clk.pos();

	contNotas = contMuestras = 0;
  } 

private:
	int contNotas, contMuestras;
}; 

#endif