#ifndef PRODUCTOR_H
#define PRODUCTOR_H

#include "systemc.h"
#include "fifo.h"


SC_MODULE (productor) {
public:
sc_port<write_if_T<sc_int<12>>>  dataOut;	

SC_HAS_PROCESS(productor);

void producir(){

	int i = inicial;
	
	while(true){
		dataOut->write(i++);
	}

	// termina aqu�
	
	sc_stop();
}


productor(sc_module_name name_, int _inicial) : sc_module(name_){

	cout<<"productor: " << name() << "  " << endl;
	
	inicial = _inicial; 

    SC_THREAD(producir);
}

private:

	int inicial;

}; 


#endif