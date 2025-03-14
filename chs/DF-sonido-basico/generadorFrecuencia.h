#ifndef GENERADORFRECUENCIA_H
#define GENERADORFRECUENCIA_H


#include "systemc.h"
#include "fifo.h"

// estas constantes se definen aquí y se utilizan en otros módulos

#define SAMPLES_PER_SECOND 33600
#define nNotas 14

SC_MODULE (generadorFrecuencia) {
public:
sc_port<read_if_T<sc_uint<5>>>  nota;
sc_port<write_if_T<sc_uint<9>>>  dirA, dirB;
sc_port<read_if_T<sc_uint<16>>>  datoA, datoB;
sc_port<write_if_T<sc_uint<12>>>  dOut;

  void generar();

  SC_CTOR(generadorFrecuencia) {
    cout<<"generadorFrecuencia: "<<name()<<endl;

    SC_THREAD(generar);
  } 
}; 

#endif