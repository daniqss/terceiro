#include "envolvente.h"
#include "generadorFrecuencia.h"
#include <cmath>

const int attack_samples = 6400;
const int sustain_samples = 18480;
const int release_samples = 6720;

const int attack_peak = 800;
const int sustain_level = 850;
const int sustain_fluctuation = 50;

const int ATTACK_SCALE = 655;
const int ATTACK_SCALE_SHIFT = 22;

const int RELEASE_SCALE = 624;
const int RELEASE_SCALE_SHIFT = 22;

const int SUSTAIN_VIBRATO_CYCLE_SCALE = 8;
const int SUSTAIN_VIBRATO_ANGLE_SCALE = 2048;

inline int scale_attack(int attack_pos) {
  return (attack_pos * ATTACK_SCALE) >> (ATTACK_SCALE_SHIFT - 10);
}

inline int scale_release(int release_pos) {
  return (release_pos * RELEASE_SCALE) >> (RELEASE_SCALE_SHIFT - 10);
}

inline int compute_vibrato_cycle(int sustain_pos) {
  return (sustain_pos * SUSTAIN_VIBRATO_CYCLE_SCALE) % SAMPLES_PER_SECOND;
}

inline int compute_vibrato_angle(int vibrato_cycle) {
  return (vibrato_cycle * SUSTAIN_VIBRATO_ANGLE_SCALE) / SAMPLES_PER_SECOND;
}

void envolvente::formar() {
  sc_uint<12> muestra;
  sc_uint<22> prod;
  int envelope_factor = 0;

  int envelope_table[33] = {0,   180, 255, 312, 360, 403, 442, 478, 512,
                            544, 574, 603, 630, 656, 681, 705, 728, 750,
                            771, 792, 812, 831, 850, 868, 885, 902, 918,
                            933, 949, 963, 977, 991, 1024};

  while (true) {
    for (int i = 0; i < SAMPLES_PER_SECOND; ++i, envelope_factor = 0) {
      sampleIn->read(muestra);

      // fase de ataque
      if (i < attack_samples) {
        int scaled_i = scale_attack(i);

        int idx = scaled_i >> 5;
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

        int vibrato_cycle = compute_vibrato_cycle(sustain_pos);
        int vibrato_angle = compute_vibrato_angle(vibrato_cycle);

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
        int normalized_pos = scale_release(release_pos);

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
