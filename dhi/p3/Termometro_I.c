#include "EFM51BB3U.h"
#include <math.h>

float read_voltage(uint8_t pin_to_read);
void write_serial_temp(float temp, bit write_celsius);

// temperature constants to make calculations
#define KA 1.122775062e-3
#define KB 2.358874358e-4
#define KC 7.504998806e-8

#define MAX_CELSIUS ((int32_t)125)
#define MIN_CELSIUS ((int32_t)-40)

const uint8_t BUTTON = A1;
const uint8_t PIN_TO_READ = A0;

const float R0 = 10000.0;
const float SAMPLES = 100.0;

float time;
bit write_celsius = 1;
float temp;
float received_voltage;
float resistor;

void setup() {
    serialBegin(115200);
    pinMode(BUTTON, INPUT);

    analogReadResolution(12);
}

void loop() {
    received_voltage = read_voltage(PIN_TO_READ);
    time = millis();

    resistor = R0 * ((4096.0 / received_voltage) - 1);

    temp = 1 / (KA + KB * log(resistor) + KC * pow(log(resistor), 3));
    temp = temp - ((3.3-3.3*received_voltage/4096)*(3.3-3.3*received_voltage/4096)/(2.5*resistor));
    temp = temp - 273.15;

    write_celsius = (!digitalRead(BUTTON)) ? false : true;
    temp = (temp > MAX_CELSIUS) ? MAX_CELSIUS : (temp < MIN_CELSIUS) ? MIN_CELSIUS : temp;

    write_serial_temp(temp, write_celsius);
}

float read_voltage(uint8_t pin_to_read) {
  int32_t total_voltage = 0;
  uint8_t i = 0;

  while(i < SAMPLES) {
    total_voltage += analogRead(pin_to_read);
    delay(2);
    i += 1;
  }

  return ((float)total_voltage/SAMPLES);
}

void write_serial_temp(float temp, bit write_celsius) {
  serialPrint(Strn, "Time(ms): ");
  serialPrint(Flt0, time);

  serialPrint(Strn, " Rm(ohm.): ");
  serialPrint(Flt1, resistor);

  serialPrint(Strn, write_celsius ? " T(ºC): " : " T(ºF): ");
  serialPrintln(Flt1, write_celsius ? temp : temp * 1.8 + 32);
}
