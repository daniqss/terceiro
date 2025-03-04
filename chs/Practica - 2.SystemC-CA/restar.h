#ifndef RESTAR_H
#define RESTAR_H


#include "systemc.h"

SC_MODULE(restar) {
public:
    sc_in<bool> clk, reset;
    sc_in <bool> validIn1, validIn2;
    sc_in<sc_int<12>> dataIn1, dataIn2;

    sc_out <bool> next;

    sc_out <bool> validOut;
    sc_out<sc_int<12>> dataOut;


    void procRestar();
    void registers();

    SC_CTOR(restar) {
        cout << "restar: " << name() << endl;

        SC_METHOD(procRestar);
        sensitive << FIRE;

        SC_METHOD(registers);
        sensitive << clk.pos();

        FIRE.write(0);
        
    }

private:
    sc_signal<bool> FIRE;
    sc_int<12> minuendo, sustraendo;
    bool valid1, valid2; 

};

#endif // !RESTAR_H