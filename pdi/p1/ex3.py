import matplotlib.pyplot as plt
import numpy as np
import sounddevice as sd

"================================================="
" Senal coseno"
valini = 0 # Valor inicial
fs = 44100  # Hz
duracion = 44100  # en muestras
n =  np.arange(valini,duracion)/fs


"================================================="
" Creación de coseno "
f = 200 
Na = 35
p = 3
f2 = 480

xcos = np.cos(2 * np.pi * f * n)
xcos2 = np.cos(2 * np.pi * f2 * n)
# rango de 2 hasta Na + 1 porque el rango es excluyente en el limite superior en python
for k in range(2, Na + 1):
    xcos += np.cos(2 * np.pi * k**0.5 * f * n)
    xcos2 += np.cos(2 * np.pi * k**0.5 * f * n)
    
xcos_suma = xcos + xcos2
    
plt.figure(figsize=(10, 5))

plt.plot(n[:100], xcos[:100], label=f"xcos (f = {f} Hz)")
plt.plot(n[:100], xcos2[:100], label=f"xcos2 (f2 = {f2} Hz)", linestyle="dashed")
plt.plot(n[:100], xcos_suma[:100], label="Suma de ambas señales", linestyle="dotted")

plt.xlabel("Tiempo (s)")
plt.ylabel("Amplitud")
plt.title("Señales Coseno con Armónicos")
plt.legend()
plt.show()

sd.play(xcos_suma, fs)   #escucha la señal si tienes cascos
