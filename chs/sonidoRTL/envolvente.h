#include "systemc.h"

SC_MODULE(envolvente) {
public:
  sc_in<bool> clk;
  sc_in<bool> rst;

  sc_in<sc_uint<12>> sampleIn;
  sc_in<bool> start;
  sc_out<sc_uint<12>> sampleOut;
  sc_out<bool> valOut;

  void formar();
  void registros();

  SC_CTOR(envolvente) {
    cout << "envolvente: " << name() << endl;

    SC_METHOD(formar);
    sensitive << clk.pos();

    fireRegs.write(false);

    SC_METHOD(registros);
    sensitive << clk.pos();
  }

private:
  sc_signal<bool> fireRegs;
  bool valid;
  sc_uint<12> sample;
  sc_uint<12> nivel, newNivel;
  sc_uint<16> indice;

  sc_uint<16> envelope_factor;
  sc_uint<2> phase;
};