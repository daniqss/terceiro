#include "envolvente.h"
#include "generadorFrecuencia.h"
#include <cmath>

void envolvente::formar() {
  sc_uint<12> muestra;
  sc_uint<22> prod;
  int envelope_factor = 0;

  const int attack_samples = 6400;
  const int sustain_samples = 18480;
  const int release_samples = 6720;

  const int attack_peak = 800;
  const int sustain_level = 850;
  const int sustain_fluctuation = 50;

  int envelope_table[33] = {0,   180, 255, 312, 360, 403, 442, 478, 512,
                            544, 574, 603, 630, 656, 681, 705, 728, 750,
                            771, 792, 812, 831, 850, 868, 885, 902, 918,
                            933, 949, 963, 977, 991, 1024};

  while (true) {
    for (int i = 0; i < SAMPLES_PER_SECOND; ++i, envelope_factor = 0) {
      sampleIn->read(muestra);

      if (i < attack_samples) {
        int scaled_i = (i * 1024) / attack_samples;

        int idx = scaled_i >> 5; // scaled_i / 32
        int next = idx < 32 ? idx + 1 : 32;

        int frac = scaled_i & 31;
        envelope_factor = ((envelope_table[next] * frac) +
                           (envelope_table[idx] * (32 - frac))) >>
                          5;

        envelope_factor = (envelope_factor * attack_peak) >> 10;
      }

      // fase de sostenido
      else if (i < attack_samples + sustain_samples) {
        int sustain_pos = i - attack_samples;

        int vibrato_cycle = (sustain_pos * 8) % SAMPLES_PER_SECOND;
        int vibrato_angle = (vibrato_cycle * 2048) / SAMPLES_PER_SECOND;

        int vibrato_value = 0;
        if (vibrato_angle < 1024) {
          vibrato_value = (vibrato_angle * (1024 - vibrato_angle)) >> 9;
        } else {
          int x = vibrato_angle - 1024;
          vibrato_value = -((x * (1024 - x)) >> 9);
        }

        int vibrato_scaled = (vibrato_value * sustain_fluctuation) >> 8;
        envelope_factor = sustain_level + vibrato_scaled;
      }

      // fase de decaimiento
      else if (i < attack_samples + sustain_samples + release_samples) {
        int release_pos = i - attack_samples - sustain_samples;
        int normalized_pos = (release_pos * 1024) / release_samples;

        int inv_pos = 1024 - normalized_pos;
        envelope_factor = (sustain_level * inv_pos * inv_pos) >> 20;
      } else {
        envelope_factor = 0;
      }

      prod = (muestra * envelope_factor) >> 10;
      muestra = prod;

      sampleOut->write(muestra);
    }
  }
}
