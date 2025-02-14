#ifndef RESTAR_H
#define RESTAR_H


#include "systemc.h"
#include "fifo.h"

SC_MODULE(restar) {
public:
    sc_port<read_if_T<sc_int<12>>>  dataIn1, dataIn2;
    sc_port<write_if_T<sc_int<12>>>  dataOut;

    void procRestar();

    SC_CTOR(restar) {
        cout << "restar: " << name() << endl;

        SC_THREAD(procRestar);
    }

};

#endif