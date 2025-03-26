import machine
import time

led = machine.Pin(2, machine.Pin.OUT)

while True:
    led.value(1)  # Enciende el LED
    time.sleep(1)  # Espera 1 segundo
    led.value(0)  # Apaga el LED
    time.sleep(1)  # Espera 1 segundo