#include "systemc.h"
#include "fifo.h"


// solo admite 2 muestras (samples) habría que extenderlo para un sonido más sofisticado

SC_MODULE (mezclador) {
public:
sc_port<read_if_T<sc_uint<12>>>  sample1, sample2;
sc_port<read_if_T<sc_uint<8>>>  nivel1, nivel2;
sc_port<write_if_T<sc_uint<12>>>  sampleOut;

  void mezclar();

  SC_CTOR(mezclador) {
    cout<<"mezclador: "<<name()<<endl;

    SC_THREAD(mezclar);
  } 
}; 
