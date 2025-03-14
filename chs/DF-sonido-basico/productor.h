#ifndef PRODUCTOR_H
#define PRODUCTOR_H

// https://pianosage.blogspot.com/2011/11/simple-5-finger-piano-songs-for.html?msclkid=0656577fa60911ec868c79cbc1944372

#include "systemc.h"
#include "fifo.h"

SC_MODULE (productor) {
public:
sc_port<write_if_T<sc_uint<5>>>  nota1, nota2;
sc_port<write_if_T<sc_uint<8>>>  nivel1, nivel2;

  void generar(){
	  
	nivel1->write(192);	// volumen de la nota dominante
	nivel2->write(64);	// volumen del único armónico que añado. Convendría poner más

	// En "generadorFrecuencia.h" se puede cambiar el número de notas

	// 14 notas en escala
	nota1->write(0);	nota2->write(12);	// 12 posiciones despues de cada nota está su homónima en la siguiente octava
	nota1->write(2);	nota2->write(14);
	nota1->write(4);	nota2->write(16);
	nota1->write(5);	nota2->write(17);
	nota1->write(7);	nota2->write(19);
	nota1->write(9);	nota2->write(21);
	nota1->write(11);	nota2->write(23);
	nota1->write(12);	nota2->write(24);
	nota1->write(11);	nota2->write(23);
	nota1->write(9);	nota2->write(21);
	nota1->write(7);	nota2->write(19);
	nota1->write(5);	nota2->write(17);
	nota1->write(4);	nota2->write(16);
	nota1->write(2);	nota2->write(14);
	
	// 14 notas (Frere Jacques)
	/*
	nota1->write(0);	nota2->write(12);
	nota1->write(2);	nota2->write(14);
	nota1->write(4);	nota2->write(16);

	nota1->write(0);	nota2->write(12);

	nota1->write(0);	nota2->write(12);
	nota1->write(2);	nota2->write(14);
	nota1->write(4);	nota2->write(16);

	nota1->write(0);	nota2->write(12);

	nota1->write(4);	nota2->write(16);
	nota1->write(5);	nota2->write(17);
	nota1->write(7);	nota2->write(19);

	nota1->write(4);	nota2->write(16);
	nota1->write(5);	nota2->write(17);
	nota1->write(7);	nota2->write(19);
	*/

  }

  SC_CTOR(productor) {
    cout<<"productor: "<<name()<<endl;

    SC_THREAD(generar);
  } 
}; 

#endif