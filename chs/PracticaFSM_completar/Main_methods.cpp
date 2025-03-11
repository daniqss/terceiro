#include "systemc.h"

#include "mcd.h"
#include "productorConsumidor.h"

// #ifdef trazas
// #include <sysc/tracing/sc_vcd_trace.h>
// #endif

class top : public sc_module {
public:
  sc_in<bool> clk, rst;

  productorConsumidor *prodCon;
  mcd *instMcd;

  sc_signal<sc_uint<16>> datoA, datoB, resultado;
  sc_signal<bool> valido, listo;

  SC_CTOR(top) {

    int i;

    prodCon = new productorConsumidor("prodCon");
    instMcd = new mcd("instMcd");

    prodCon->datoA(datoA);
    prodCon->datoB(datoB);
    prodCon->resultado(resultado);
    prodCon->valOut(valido);
    prodCon->listo(listo);
    prodCon->reset(rst);
    prodCon->clock(clk);

    instMcd->entA(datoA);
    instMcd->entB(datoB);
    instMcd->resultado(resultado);
    instMcd->valIn(valido);
    instMcd->listo(listo);
    instMcd->reset(rst);
    instMcd->clock(clk);
  }
};

int sc_main(int nargs, char *vargs[]) {

  sc_clock clk("clk", 1, SC_NS);
  sc_signal<bool> rst;

  // #ifdef trazas
  //   sc_core::sc_report_handler::set_actions("/IEEE_Std_1666/deprecated",
  //                                           sc_core::SC_DO_NOTHING);

  //   sc_trace_file *Tf;
  //   Tf = sc_create_vcd_trace_file("traza");
  //   ((vcd_trace_file *)Tf)->set_time_unit(1, SC_NS);
  // #endif

  top principal("top");
  principal.clk(clk);
  principal.rst(rst);

  // #ifdef trazas
  //   sc_trace(Tf, clk, "clk");
  //   sc_trace(Tf, rst, "rst");
  //   sc_trace(Tf, principal.datoA, "entA");
  //   sc_trace(Tf, principal.datoB, "entB");
  //   sc_trace(Tf, principal.valido, "validInit");
  //   sc_trace(Tf, principal.resultado, "resultado");
  //   sc_trace(Tf, principal.listo, "listo");
  //   sc_trace(Tf, principal.instMcd->A, "A");
  //   sc_trace(Tf, principal.instMcd->B, "B");
  //   sc_trace(Tf, principal.instMcd->estadoSig, "estado");
  //   sc_trace(Tf, principal.instMcd->restaSig, "resta");
  //   sc_trace(Tf, principal.instMcd->signoSig, "signo");
  //   sc_trace(Tf, principal.instMcd->ceroSig, "cero");
  // #endif

  rst.write(true);
  sc_start(2, SC_NS);

  rst.write(false);
  sc_start();

  return 0;
}
