#include "systemc.h"

#include "fifo.h"

#include "productor.h"
#include "consumidor.h"
#include "sumar.h"
#include "restar.h"
// OJO! no se ha incluido todos los m�dulos, hazlo aqu�



class top : public sc_module
{
	public:

	// 12 bit integer
	fifo_T<sc_int<12>> *Qval1, *Qval2, *Qsuma, *Qresta;

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

		Qval1 = new fifo_T<sc_int<12>>("Qval1", 1); 
		Qval2 = new fifo_T<sc_int<12>>("Qval2", 1);
		Qsuma = new fifo_T<sc_int<12>>("Qsuma", 1);
		Qresta = new fifo_T<sc_int<12>>("Qresta", 1);
		// OJO! instancia la cola que falta


		instProductor1->dataOut( *Qval1 );
		instProductor2->dataOut( *Qval2 );

		instSumar->dataIn( *Qval1 );
		instSumar->dataOut( *Qsuma );

		// Restar recibe de Qsuma y Qval2
		instRestar->dataIn1(*Qsuma);
		instRestar->dataIn2(*Qval2);
		instRestar->dataOut(*Qresta);

		instConsumidor->dataIn( *Qresta );

	}

};


int sc_main(int nargs, char* vargs[]){

	top principal("top");
	sc_start();

	return 0;

}

