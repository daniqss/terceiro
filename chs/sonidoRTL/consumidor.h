#ifndef CONSUMIDOR_H
#define CONSUMIDOR_H
#include"generadorFrecuencia.h"

#include "systemc.h"

// estructura de la cabecera de un archivo .WAV

struct wavHeader {
	unsigned char riff[4];                      // RIFF string
	unsigned int overall_size;               // overall size of file in bytes
	unsigned char wave[4];                      // WAVE string
	unsigned char fmt_chunk_marker[4];          // fmt string with trailing null char
	unsigned int length_of_fmt;                 // length of the format data
	unsigned short format_type;                   // format type. 1-PCM, 3- IEEE float, 6 - 8bit A law, 7 - 8bit mu law
	unsigned short channels;                      // no.of channels
	unsigned int sample_rate;                   // sampling rate (blocks per second)
	unsigned int byterate;                      // SampleRate * NumChannels * BitsPerSample/8
	unsigned short block_align;                   // NumChannels * BitsPerSample/8
	unsigned short bits_per_sample;               // bits per sample, 8- 8bits, 16- 16 bits etc
	unsigned char data_chunk_header[4];        // DATA string or FLLR string
	unsigned int data_size;                     // NumSamples * NumChannels * BitsPerSample/8 - size of the next chunk that will be read
};


SC_MODULE (consumidor) {
public:
	sc_in < bool > clk;
	sc_in < bool > rst;

	sc_in < sc_uint <12> > sonido;
	sc_in <bool> valIn;


  void recibir(){
	  
	  sc_uint<12> muestra;
	  short entCorto;

		if (rst.read()) {
			contNotas = contMuestras = 0;
			return;
		}

		if (valIn.read()) {
			muestra = sonido.read();
			entCorto = muestra * 8;
			entCorto -= 16384;
			fwrite(&entCorto, 1, 2, fichero);
			++contMuestras;
			if (contMuestras == SAMPLES_PER_SECOND) {
				++contNotas;
				contMuestras = 0;
				if (contNotas == nNotas) {
					fclose(fichero);
					fprintf(stderr, "Grabacion finalizada\n");
					sc_stop();
					exit(0);
				}
			}
		}

  }

  SC_CTOR(consumidor) {
    cout<<"consumidor: "<<name()<<endl;

	fichero = fopen("sonidoGenerado.wav", "wb");
	if (!fichero) {
		fprintf(stderr, "Error abriendo el fichero sonidoGenerado.wav\n");
		sc_stop();
	}

	// rellena y vuelca a fichero la cabecera del archivo .WAV

	cab.riff[0]='R';	cab.riff[1] = 'I';	cab.riff[2] = 'F';	cab.riff[3] = 'F';
	cab.overall_size = 44 + (2 * nNotas * SAMPLES_PER_SECOND);
	cab.wave[0]='W';	cab.wave[1] = 'A';	cab.wave[2] = 'V';	cab.wave[3] = 'E';
	cab.fmt_chunk_marker[0]='f'; cab.fmt_chunk_marker[1] = 'm'; cab.fmt_chunk_marker[2] = 't'; cab.fmt_chunk_marker[3] = ' ';
	cab.length_of_fmt = 16;
	cab.format_type = 1;
	cab.channels = 1;
	cab.sample_rate = SAMPLES_PER_SECOND;
	cab.byterate = SAMPLES_PER_SECOND * 2;
	cab.block_align = 2;
	cab.bits_per_sample = 16;
	cab.data_chunk_header[0] = 'd';	cab.data_chunk_header[1] = 'a';	cab.data_chunk_header[2] = 't';	cab.data_chunk_header[3] = 'a';
	cab.data_size = (2 * nNotas * SAMPLES_PER_SECOND);

	fwrite(&cab, 44, 1, fichero);

	contNotas = contMuestras = 0; 

	SC_METHOD(recibir);
	sensitive << clk.pos();
  } 

private:
	FILE* fichero;
	wavHeader cab; 
	int contNotas, contMuestras; 

}; 






#endif
