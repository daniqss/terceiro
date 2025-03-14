#include "systemc.h"

#include "fifo.h"
#include "mezclador.h"
#include "envolvente.h"
#include "consumidor.h"
#include "generadorFrecuencia.h"
#include "romFrecuencias.h"
#include "productor.h"


class top : public sc_module
{
public:

fifo_T<sc_uint<5>> *Qnota1, *Qnota2;
fifo_T<sc_uint<8>> *Qnivel1, *Qnivel2;
fifo_T<sc_uint<9>> *QdirA1, *QdirB1, *QdirA2, *QdirB2;
fifo_T<sc_uint<16>> *QdatoA1, *QdatoB1, *QdatoA2, *QdatoB2;
fifo_T<sc_uint<12>> *Qsample1, *Qsample2, *QsampleMix, * QsampleOut;

productor *datosEntrada;
consumidor *salidaResultados;
mezclador *instMezclador;
envolvente *instEnvolvente;
generadorFrecuencia *instGeneradorFrecuencia1, * instGeneradorFrecuencia2;
romFrecuencias* instRomFrecuencias1, * instRomFrecuencias2;

SC_CTOR(top) // the module constructor
{

	datosEntrada = new productor("datosEntrada");
	salidaResultados = new consumidor("salidaResultados");

	instGeneradorFrecuencia1 = new generadorFrecuencia("instGeneradorFrecuencia1");
	instGeneradorFrecuencia2 = new generadorFrecuencia("instGeneradorFrecuencia2");
	instRomFrecuencias1 = new romFrecuencias("instRomFrecuencias1");
	instRomFrecuencias2 = new romFrecuencias("instRomFrecuencias2");

	instMezclador = new mezclador("instMezclador");
	instEnvolvente = new envolvente("instEnvolvente");


	fifo_T<sc_uint<5>>* Qnota1, * Qnota2;
	fifo_T<sc_uint<8>>* Qnivel1, * Qnivel2;
	fifo_T<sc_uint<9>>* QdirA1, * QdirB1, * QdirA2, * QdirB2;
	fifo_T<sc_uint<16>>* QdatoA1, * QdatoB1, * QdatoA2, * QdatoB2;
	fifo_T<sc_uint<12>>* Qsample1, * Qsample2, * QsampleMix, * QsampleOut;


	Qnota1 = new fifo_T<sc_uint<5>>("Qnota1", 1);
	Qnota2 = new fifo_T<sc_uint<5>>("Qnota2", 1);
	Qnivel1 = new fifo_T<sc_uint<8>>("Qnivel1", 1);
	Qnivel2 = new fifo_T<sc_uint<8>>("Qnivel2", 1);
	QdirA1 = new fifo_T<sc_uint<9>>("QdirA1", 1);
	QdirB1 = new fifo_T<sc_uint<9>>("QdirB1", 1);
	QdirA2 = new fifo_T<sc_uint<9>>("QdirA2", 1);
	QdirB2 = new fifo_T<sc_uint<9>>("QdirB2", 1);
	QdatoA1 = new fifo_T<sc_uint<16>>("QdatoA1", 1);
	QdatoB1 = new fifo_T<sc_uint<16>>("QdatoB1", 1);
	QdatoA2 = new fifo_T<sc_uint<16>>("QdatoA2", 1);
	QdatoB2 = new fifo_T<sc_uint<16>>("QdatoB2", 1);
	Qsample1 = new fifo_T<sc_uint<12>>("Qsample1", 1);
	Qsample2 = new fifo_T<sc_uint<12>>("Qsample2", 1);
	QsampleMix = new fifo_T<sc_uint<12>>("QsampleMix", 1);
	QsampleOut = new fifo_T<sc_uint<12>>("QsampleOut", 1);


	datosEntrada->nota1(*Qnota1);
	datosEntrada->nivel1(*Qnivel1);
	datosEntrada->nota2(*Qnota2);
	datosEntrada->nivel2(*Qnivel2);

	instGeneradorFrecuencia1->nota(*Qnota1);
	instGeneradorFrecuencia1->dirA(*QdirA1);
	instGeneradorFrecuencia1->dirB(*QdirB1);
	instGeneradorFrecuencia1->datoA(*QdatoA1);
	instGeneradorFrecuencia1->datoB(*QdatoB1);
	instGeneradorFrecuencia1->dOut(*Qsample1);

	instGeneradorFrecuencia2->nota(*Qnota2);
	instGeneradorFrecuencia2->dirA(*QdirA2);
	instGeneradorFrecuencia2->dirB(*QdirB2);
	instGeneradorFrecuencia2->datoA(*QdatoA2);
	instGeneradorFrecuencia2->datoB(*QdatoB2);
	instGeneradorFrecuencia2->dOut(*Qsample2);

	instRomFrecuencias1->dirA(*QdirA1);
	instRomFrecuencias1->dirB(*QdirB1);
	instRomFrecuencias1->datoA(*QdatoA1);
	instRomFrecuencias1->datoB(*QdatoB1);

	instRomFrecuencias2->dirA(*QdirA2);
	instRomFrecuencias2->dirB(*QdirB2);
	instRomFrecuencias2->datoA(*QdatoA2);
	instRomFrecuencias2->datoB(*QdatoB2);

	instMezclador->sample1(*Qsample1);
	instMezclador->sample2(*Qsample2);
	instMezclador->nivel1(*Qnivel1);
	instMezclador->nivel2(*Qnivel2);
	instMezclador->sampleOut(*QsampleMix);

	instEnvolvente->sampleIn(*QsampleMix);
	instEnvolvente->sampleOut(*QsampleOut);

	salidaResultados->sonido(*QsampleOut);

}
};


int sc_main(int nargs, char* vargs[]){

	top principalIntra("top");
	sc_start();

	return 0;

}
