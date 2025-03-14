#ifndef ROMFRECUENCIAS_H
#define ROMFRECUENCIAS_H


#include "systemc.h"
#include "fifo.h"

SC_MODULE (romFrecuencias) {
public:
sc_port<read_if_T<sc_uint<9>>>  dirA, dirB;
sc_port<write_if_T<sc_uint<16>>>  datoA, datoB;

  void leerA();
  void leerB();

  SC_CTOR(romFrecuencias) {
    cout<<"romFrecuencias: "<<name()<<endl;

    SC_THREAD(leerA);
	SC_THREAD(leerB);
  } 
}; 


#endif