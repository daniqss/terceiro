#include "systemc.h"

#include "mezclador.h"
#include "envolvente.h"
#include "consumidor.h"
#include "generadorFrecuencia.h"
#include "romFrecuencias.h"
#include "productor.h"


class top : public sc_module
{
public:

sc_in < bool > clk;
sc_in < bool > rst;

sc_signal<sc_uint<5>> Qnota1, Qnota2;
sc_signal<sc_uint<8>> Qnivel1, Qnivel2;
sc_signal<sc_uint<9>> QdirA1, QdirB1, QdirA2, QdirB2;
sc_signal<sc_uint<16>> QdatoA1, QdatoB1, QdatoA2, QdatoB2;
sc_signal<sc_uint<12>> Qsample1, Qsample2, QsampleMix, QsampleOut;
sc_signal<bool>	Qstart, Qvalid; 

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

	datosEntrada->clk(clk);
	datosEntrada->rst(rst);
	datosEntrada->start(Qstart);
	datosEntrada->nota1(Qnota1);
	datosEntrada->nivel1(Qnivel1);
	datosEntrada->nota2(Qnota2);
	datosEntrada->nivel2(Qnivel2);

	instGeneradorFrecuencia1->clk(clk);
	instGeneradorFrecuencia1->rst(rst);
	instGeneradorFrecuencia1->start(Qstart);
	instGeneradorFrecuencia1->nota(Qnota1);
	instGeneradorFrecuencia1->dirA(QdirA1);
	instGeneradorFrecuencia1->dirB(QdirB1);
	instGeneradorFrecuencia1->datoA(QdatoA1);
	instGeneradorFrecuencia1->datoB(QdatoB1);
	instGeneradorFrecuencia1->dOut(Qsample1);

	instGeneradorFrecuencia2->clk(clk);
	instGeneradorFrecuencia2->rst(rst);
	instGeneradorFrecuencia2->start(Qstart);
	instGeneradorFrecuencia2->nota(Qnota2);
	instGeneradorFrecuencia2->dirA(QdirA2);
	instGeneradorFrecuencia2->dirB(QdirB2);
	instGeneradorFrecuencia2->datoA(QdatoA2);
	instGeneradorFrecuencia2->datoB(QdatoB2);
	instGeneradorFrecuencia2->dOut(Qsample2);

	instRomFrecuencias1->clk(clk);
	instRomFrecuencias1->dirA(QdirA1);
	instRomFrecuencias1->dirB(QdirB1);
	instRomFrecuencias1->datoA(QdatoA1);
	instRomFrecuencias1->datoB(QdatoB1);

	instRomFrecuencias2->clk(clk);
	instRomFrecuencias2->dirA(QdirA2);
	instRomFrecuencias2->dirB(QdirB2);
	instRomFrecuencias2->datoA(QdatoA2);
	instRomFrecuencias2->datoB(QdatoB2);

	instMezclador->clk(clk);
	instMezclador->rst(rst);
	instMezclador->sample1(Qsample1);
	instMezclador->sample2(Qsample2);
	instMezclador->nivel1(Qnivel1);
	instMezclador->nivel2(Qnivel2);
	instMezclador->sampleOut(QsampleMix);

	instEnvolvente->clk(clk);
	instEnvolvente->rst(rst);
	instEnvolvente->start(Qstart);
	instEnvolvente->sampleIn(QsampleMix);
	instEnvolvente->sampleOut(QsampleOut);
	instEnvolvente->valOut(Qvalid);

	salidaResultados->clk(clk);
	salidaResultados->rst(rst);
	salidaResultados->sonido(QsampleOut);
	salidaResultados->valIn(Qvalid);

}
};


int sc_main(int nargs, char* vargs[]){

	sc_clock clk("clk", 1, SC_NS);
	sc_signal <bool> rst;

	top principalSonido("top");
	principalSonido.clk(clk);
	principalSonido.rst(rst);

	rst.write(true);
	sc_start(5, SC_NS);
	rst.write(false);
	sc_start();

	return 0;

}

