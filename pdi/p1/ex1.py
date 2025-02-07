import matplotlib.pyplot as plt
import numpy as np
import scipy.io.wavfile
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
Na = 20

xcos = np.cos(2 * np.pi * f * n)
# rango de 2 hasta Na + 1 porque el rango es excluyente en el limite superior en python
for k in range(2, Na + 1):
    xcos += np.cos(2 * np.pi * k * f * n)
    
plt.xlabel('tiempo')
plt.ylabel('amplitud')
plt.title('Señal coseno')
plt.stem(n[1:100], xcos[1:100])
plt.show()

duracion_seg = duracion / fs
print(f"Duración: {duracion_seg:.2f} segundos")

sd.play(xcos, fs)   #escucha la señal si tienes cascos
