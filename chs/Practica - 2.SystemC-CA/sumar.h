#ifndef SUMAR_H
#define SUMAR_H


#include "systemc.h"

SC_MODULE(sumar) {
public:
    sc_in<bool> clk, reset;
    sc_in <bool> validIn;
    sc_in<sc_int<12>> dataIn;
    
    sc_out <bool> next;

    sc_out <bool> validOut;
    sc_out<sc_int<12>> dataOut;

    void procSumar();
    void registers();

    SC_CTOR(sumar) {
        cout << "sumar: " << name() << endl;

        SC_METHOD(procSumar);
        sensitive << FIRE;
        
        SC_METHOD(registers);
        sensitive << clk.pos();

        FIRE.write(0); 
        nDatos = 0; 

    }

private:
    sc_signal<bool> FIRE; 
    sc_int<12> dato1, dato2; 
    sc_uint<2> nDatos; 
     
};

#endif