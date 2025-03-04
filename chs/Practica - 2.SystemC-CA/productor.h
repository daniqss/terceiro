#ifndef PRODUCTOR_H
#define PRODUCTOR_H

#include "systemc.h"


SC_MODULE (productor) {
public:
	sc_in<bool> clk, reset, next;
	sc_out <bool> validOut;
	sc_out<sc_int<12>> dataOut;	

SC_HAS_PROCESS(productor);


void producir() {

	if (reset.read()) {
		contador = inicial;
		validOut.write(false);
		dataOut.write(0);
	}
	else {
		if (next.read())
			++contador; 
		dataOut.write(contador);
		validOut.write(true);
	}

	FIRE.write(!FIRE.read());
}


productor(sc_module_name name_, int _inicial) : sc_module(name_){

	cout<<"productor: " << name() << "  " << endl;
	
	inicial = _inicial; 
	contador = _inicial;

    SC_METHOD(producir);
	sensitive << clk.pos();

	FIRE.write(0);
}

private:

	int inicial;
	int contador; 
	sc_signal<bool> FIRE; 

}; 


#endif