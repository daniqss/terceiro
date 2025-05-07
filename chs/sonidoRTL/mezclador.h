#include "systemc.h"


// solo admite 2 muestras (samples) habría que extenderlo para un sonido más sofisticado

SC_MODULE (mezclador) {
public:

	sc_in < bool > clk;
	sc_in < bool > rst;

	sc_in < sc_uint <12> > sample1, sample2;
	sc_in < sc_uint <8> > nivel1, nivel2;
	sc_out < sc_uint <12> > sampleOut;

	void registros();  
	void mezclar();

  SC_CTOR(mezclador) {
    cout<<"mezclador: "<<name()<<endl;

	fireRegs.write(false);

	SC_METHOD(mezclar);
	sensitive << fireRegs;

	SC_METHOD(registros);
	sensitive << clk.pos();

  }

private:
	sc_signal<bool>		fireRegs;
	sc_uint<12>			regSample1, regSample2;
	sc_uint<8>			regNivel1, regNivel2;
}; 
