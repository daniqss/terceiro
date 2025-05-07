#ifndef ROMFRECUENCIAS_H
#define ROMFRECUENCIAS_H


#include "systemc.h"

SC_MODULE (romFrecuencias) {
public:

sc_in<bool> clk;

sc_in<sc_uint<9>>	dirA, dirB;
sc_out<sc_uint<16>>	datoA;
sc_out<sc_uint<16>>	datoB;

  void leerA();
  void leerB();

  SC_CTOR(romFrecuencias) {
    cout<<"romFrecuencias: "<<name()<<endl;

	SC_METHOD(leerA);
	sensitive << clk.pos();

	SC_METHOD(leerB);
	sensitive << clk.pos();

  } 
}; 


#endif