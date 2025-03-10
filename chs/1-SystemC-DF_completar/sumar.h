#ifndef SUMAR_H
#define SUMAR_H


#include "systemc.h"
#include "fifo.h"

SC_MODULE(sumar) {
public:
    sc_port<read_if_T<sc_int<12>>>  dataIn;
    sc_port<write_if_T<sc_int<12>>>  dataOut;

    void procSumar();

    SC_CTOR(sumar) {
        cout << "sumar: " << name() << endl;

        SC_THREAD(procSumar);
    }

};

#endif