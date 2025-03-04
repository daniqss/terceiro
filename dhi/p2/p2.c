#include "EFM51BB3U.h"

// pin where this components are connected
const uint8_t BUTTON = 3;
const uint8_t RED_LED = 10;
const uint8_t GREEN_LED = 5;

volatile boolean led_state = true;
boolean is_starting = true;
boolean state_changed = false;

uint16_t hours = 0;
uint16_t minutes = 0;
uint16_t seconds = 0;

uint16_t aux_hours = 0;
uint16_t aux_mins = 0;
uint16_t aux_secs = 0;

// executed once, in the program boot
void setup() {
  // configure pin modes
  pinMode(BUTTON, INPUT);
  pinMode(RED_LED, OUTPUT);
  pinMode(GREEN_LED, OUTPUT);

  // configure serial port
  serialBegin(115200);

  // setup power save mode
  setPowerSave(POWER_MODE_SNOOZE);

  // every 1000ms calls timeWakeISR
  timeWakeSet(1000, WAKE_UNITS_MS);
  // enable pin to handle interruption when its level(LOW here)
  attachIntMatch(BUTTON, LOW);
  // enable user periodic wake ups
  timeWakeStart();
}

void loop() {
  // check if there is serial data available
  while (serialAvailable()) {
    if (led_state) {
      // get user inputs from serial
      // time is set writing hh:mm:ss in serial port
      // it go to suspend mode after writing hh > 23 or hh or ss > 59
      aux_hours = serialParseInt();
      aux_mins = serialParseInt();
      aux_secs = serialParseInt();

      // enter suspend mode if above conditions are met
      if (aux_secs > 59 || aux_mins > 59 || aux_hours > 23) {
        serialPrintln(Strn, "Go to Suspend mode");
        state_changed = true;
        led_state = false;
      } else {
        hours = aux_hours;
        minutes = aux_mins;
        seconds = aux_secs;
      }
    }
  }

  // process according to current state
  // to quit suspend push button
  if (led_state) {
    disablePowerSave();
    if (state_changed) {
      serialPrintln(Strn, "Go to Normal mode");
      state_changed = false;
    }
  } else {
    // suspend mode -> turn off LEDs and enable power save
    digitalWrite(RED_LED, LOW);
    digitalWrite(GREEN_LED, LOW);
    enablePowerSave();
  }
  serialFlush();
}

void timeWakeISR() {
  // advance to next second
  seconds++;

  if (seconds > 59) {
    seconds = 0;
    minutes++;
  }
  if (minutes > 59) {
    minutes = 0;
    hours++;
  }
  if (hours > 23)
    hours = 0;


  if (led_state) {
    // toggle LEDs
    digitalWrite(RED_LED, !digitalRead(RED_LED));
    digitalWrite(GREEN_LED, !digitalRead(RED_LED));

    // display time
    if (is_starting) {
      is_starting ^= 1;
      serialPrintln(Strn, "Start 24 hour clock");
    }
    if (hours < 10)
      serialPrint(Strn, "0");
    serialPrint(Sint, hours);
    serialPrint(Strn, ":");
    if (minutes < 10)
      serialPrint(Strn, "0");
    serialPrint(Sint, minutes);
    serialPrint(Strn, ":");
    if (seconds < 10)
      serialPrint(Strn, "0");
    serialPrintln(Sint, seconds);
  }
}

// update state from button
void matchISR(void) {
  led_state = matchPin() == BUTTON;
}
