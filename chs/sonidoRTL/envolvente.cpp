#include "envolvente.h"
#include "generadorFrecuencia.h"

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

const int envelope_table[33] = {0,   180, 255, 312, 360, 403, 442, 478, 512,
                                544, 574, 603, 630, 656, 681, 705, 728, 750,
                                771, 792, 812, 831, 850, 868, 885, 902, 918,
                                933, 949, 963, 977, 991, 1024};

void envolvente::registros() {
  if (rst.read()) {
    valid = sample = nivel = 0;
    indice = 0;
    envelope_factor = 0;
    phase = 0;
  }

  // if not reset read de sample
  else {
    sample = sampleIn.read();

    if (start.read()) {
      indice = 1;
      phase = 1;
      nivel = 0;
      envelope_factor = 0;
    } else {
      if (indice) {
        if (indice == SAMPLES_PER_SECOND)
          indice = 0;
        else
          ++indice;

        // update envelope phase depending on the current index
        if (indice < attack_samples)
          phase = 1;
        else if (indice < attack_samples + sustain_samples)
          phase = 2;
        else if (indice < attack_samples + sustain_samples + release_samples)
          phase = 3;
        else
          phase = 0;
      }
      nivel = newNivel;
    }
  }
  fireRegs.write(!fireRegs.read());
}

void envolvente::formar() {
  sc_uint<24> prod;
  int scaled_i, idx, next, frac;
  int sustain_pos, vibrato_cycle, vibrato_angle, vibrato_value, vibrato_scaled;
  int release_pos, normalized_pos, inv_pos;

  newNivel = nivel;
  envelope_factor = 0;

  if (indice > 0) {
    switch (phase) {
    // fase de ataque
    case 1: {
      scaled_i = (indice * ATTACK_SCALE) >> (ATTACK_SCALE_SHIFT - 10);
      idx = scaled_i >> 5;
      next = idx < 32 ? idx + 1 : 32;
      frac = scaled_i & 31;

      envelope_factor = ((envelope_table[next] * frac) +
                         (envelope_table[idx] * (32 - frac))) >>
                        5;

      envelope_factor = (envelope_factor * attack_peak) >> 10;
    } break;

    // fase de sostenido
    case 2:
      sustain_pos = indice - attack_samples;
      vibrato_cycle =
          (sustain_pos * SUSTAIN_VIBRATO_CYCLE_SCALE) % SAMPLES_PER_SECOND;
      vibrato_angle =
          (vibrato_cycle * SUSTAIN_VIBRATO_ANGLE_SCALE) / SAMPLES_PER_SECOND;

      vibrato_value = 0;
      if (vibrato_angle < 1024) {
        vibrato_value = (vibrato_angle * (1024 - vibrato_angle)) >> 9;
      } else {
        int x = vibrato_angle - 1024;
        vibrato_value = -((x * (1024 - x)) >> 9);
      }

      vibrato_scaled = (vibrato_value * sustain_fluctuation) >> 8;
      envelope_factor = sustain_level + vibrato_scaled;
      break;

    case 3: {

      release_pos = indice - attack_samples - sustain_samples;
      normalized_pos =
          (release_pos * RELEASE_SCALE) >> (RELEASE_SCALE_SHIFT - 10);
      inv_pos = 1024 - normalized_pos;
      envelope_factor = (sustain_level * inv_pos * inv_pos) >> 20;
    } break;

    // idle if not phase
    default: {
      envelope_factor = 0;
    } break;
    }

    newNivel = envelope_factor;
  }

  // apply the envelope to the sample
  prod = sample * nivel;
  sampleOut.write(prod(23, 12));
  valOut.write(indice != 0);
}