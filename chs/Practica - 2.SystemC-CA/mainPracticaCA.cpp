#include "systemc.h"

#define trazas


#ifdef trazas
#include <sysc/tracing/sc_vcd_trace.h>
#endif


#include "productor.h"
#include "consumidor.h"
#include "sumar.h"
#include "restar.h"



class top : public sc_module
{
public:

	sc_in<bool> clk, reset; 

	sc_signal<sc_int<12>> Qdato1, Qdato2, Qsuma, Qresta;
	sc_signal<bool> Qvalid1, Qvalid2, QvalidS, QvalidR, QnextS, QnextR;


	productor * instProductor1;
	productor* instProductor2;
	consumidor * instConsumidor;
	sumar *instSumar;
	restar *instRestar;	

	SC_CTOR(top) // the module constructor
	{

		instProductor1 = new productor("instProductor1", 0);
		instProductor2 = new productor("instProductor2", 5);
		instSumar = new sumar("instSumar");
		instRestar = new restar("instRestar");

		instConsumidor = new consumidor("instConsumidor", "resultados.txt");

		instProductor1->clk(clk);
		instProductor1->reset(reset);
		instProductor1->next(QnextS);
		instProductor1->validOut(Qvalid1);
		instProductor1->dataOut(Qdato1);

		instProductor2->clk(clk);
		instProductor2->reset(reset);
		instProductor2->next(QnextR);
		instProductor2->validOut(Qvalid2);
		instProductor2->dataOut(Qdato2);

		instSumar->clk(clk);
		instSumar->reset(reset);
		instSumar->validIn(Qvalid1);
		instSumar->dataIn(Qdato1);
		instSumar->next(QnextS);
		instSumar->validOut(QvalidS);
		instSumar->dataOut(Qsuma);


		instRestar->clk(clk);
		instRestar->reset(reset);

		instRestar->validIn1(QvalidS);
		instRestar->validIn2(Qvalid2);

		instRestar->dataIn1(Qsuma);
		instRestar->dataIn2(Qdato2);

		instRestar->next(QnextR);

		instRestar->validOut(QvalidR);
		instRestar->dataOut(Qresta);


		instConsumidor->clk(clk);
		instConsumidor->reset(reset);
		instConsumidor->validIn(QvalidR);
		instConsumidor->dataIn(Qresta);

	}

};


int sc_main(int nargs, char* vargs[]){

	sc_core::sc_report_handler::set_actions(
		"/IEEE_Std_1666/deprecated",
		sc_core::SC_DO_NOTHING
	);

	sc_clock clk("clk", 1); 
	sc_signal <bool> reset;

#ifdef trazas
	sc_trace_file* Tf;
	Tf = sc_create_vcd_trace_file("traza");
	((vcd_trace_file*)Tf)->set_time_unit(1, SC_NS);
#endif


	top principal("top");

	principal.clk(clk);
	principal.reset(reset);

#ifdef trazas
	sc_trace(Tf, clk, "clk");
	sc_trace(Tf, reset, "reset");
	sc_trace(Tf, principal.Qdato1, "Qdato1");
	sc_trace(Tf, principal.Qdato2, "Qdato2");
	sc_trace(Tf, principal.Qvalid1, "Qvalid1");
	sc_trace(Tf, principal.Qvalid2, "Qvalid2");
	sc_trace(Tf, principal.QnextS, "QnextS");
	sc_trace(Tf, principal.Qsuma, "Qsuma");
	sc_trace(Tf, principal.QvalidS, "QvalidS");
	sc_trace(Tf, principal.QnextR, "QnextR");
	sc_trace(Tf, principal.Qresta, "Qresta");
	sc_trace(Tf, principal.QvalidR, "QvalidR");
#endif


	reset.write(true); sc_start(2, SC_NS);
	reset.write(false); sc_start(1, SC_SEC);

	return 0;

}

