#include "systemc.h"
#include "fifo.h"

SC_MODULE (envolvente) {
public:
sc_port<read_if_T<sc_uint<12>>>  sampleIn;
sc_port<write_if_T<sc_uint<12>>>  sampleOut;

  void formar();

  SC_CTOR(envolvente) {
    cout<<"envolvente: "<<name()<<endl;

    SC_THREAD(formar);
  } 
}; 
