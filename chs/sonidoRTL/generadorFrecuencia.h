#ifndef GENERADORFRECUENCIA_H
#define GENERADORFRECUENCIA_H

#include "systemc.h"

#define SAMPLES_PER_SECOND 33600
#define nNotas 14

SC_MODULE (generadorFrecuencia) {
public:


sc_in<bool> clk;
sc_in<bool> rst;

sc_in<sc_uint<5>>	nota;
sc_in<bool> start;

sc_out<sc_uint<9>>	dirA, dirB;
sc_in<sc_uint<16>>	datoA;
sc_in<sc_uint<16>>	datoB;

sc_out<sc_uint<12>>	dOut;

  void generar();   
  void registros();

  SC_CTOR(generadorFrecuencia) {
    cout<<"generadorFrecuencia: "<<name()<<endl;

	fireRegs.write(false); 

	SC_METHOD(generar);
	sensitive << fireRegs;
		
	SC_METHOD(registros);
	sensitive << clk.pos();
  } 

private:
	sc_signal<bool>		fireRegs;  
//	bool				valid;
	sc_uint<5>			regNota;
	sc_uint<16>			indice;
	sc_uint<23>			producto;

}; 

#endif

