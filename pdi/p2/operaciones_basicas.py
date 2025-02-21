import matplotlib.pyplot as plt
import numpy as np
from scipy.io.wavfile import read
import sounddevice as sd
import time

"================================================="
" Diezmado "
valini = 0 # Valor inicial
duracion = 200  # Num muestras
fs = 44100  # Hz
f = 100
N = 2

n = np.arange(valini,duracion)/fs
fs, xcos = read("hola_22050.wav")
valini = 0 # Valor inicial
duracion = len(xcos)
n = np.arange(valini,duracion)/fs


ln = len(n)
xcosd = xcos[0:ln:N]
nd = np.arange(valini,duracion/N)/fs

plt.subplot(211)
plt.stem(n, xcos, '-.')
plt.xlabel('tiempo')
plt.ylabel('amplitud')
plt.title('señal')

plt.subplot(212)
plt.stem(nd, xcosd, '-.')
plt.xlabel('tiempo')
plt.ylabel('amplitud')
plt.title('señal con diezmado')
plt.show()
sd.play(xcos, fs)
time.sleep(2)
sd.play(xcosd, fs)
"================================================="
" Interpolación             "

ln = len(n)
xcosi = np.zeros(ln)
xcosi[0:ln:N] = xcosd

plt.subplot(211)
plt.stem(n, xcosi, '-.')
plt.xlabel('tiempo')
plt.ylabel('amplitud')
plt.title('señal interpolada')
plt.show()
sd.play(xcosi, fs)

xcosr = xcosi.copy()
for i in range(0, len(xcosr)):
    if xcosr[i] == 0 :
        xcosr[i] = xcosr[i - 1]
        
plt.subplot(211)
plt.stem(n, xcosr, '-.')
plt.xlabel('tiempo')
plt.ylabel('amplitud')
plt.title('señal reconstruida')
plt.show()
sd.play(xcosr, fs)

# Reconstruccion de xcos
xcosr2 = xcosi.copy()
for i in range(0, len(xcosr2)):
    if xcosr2[i] == 0 :
        xcosr2[i] = (xcosr2[i - 1] + xcosr2[i + 1]) / 2 if i == len(xcosr2) != 0 else xcosr2[i - 1]
        
plt.subplot(211)
plt.stem(n, xcosr2, '-.')
plt.xlabel('tiempo')
plt.ylabel('amplitud')
plt.title('señal reconstruida 2')
plt.show()
sd.play(xcosr2, fs)

