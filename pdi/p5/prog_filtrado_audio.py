import numpy as np
import matplotlib.pyplot as plt
import scipy.signal as signal
import scipy.io.wavfile as wav
import scipy.fft as sp
import sounddevice as sd

# Leer archivo de audio
fs, x1 = wav.read('./Cancion_de_Saria.wav')  # Asegúrate de tener un archivo de audio.wav
x = x1[:,0] / np.max(np.abs(x1[:,0]))  # Normalizar la señal

# Parámetros del filtro
fcorte = 4000  # Frecuencia de corte (Hz)
norden = 4  # Orden del filtro
tamano_ventana = 1024  # Tamaño de la ventana
solapamiento = tamano_ventana // 2  # 50% de solapamiento

# Diseño del filtro Butterworth en el dominio de la frecuencia
L = tamano_ventana
d = 1 / fs
freqs = np.fft.fftfreq(L, d)
freqs_shifted = np.fft.fftshift(freqs)
H = 1 / (1 + (freqs_shifted / fcorte)**(2 * norden))

# Aplicar filtrado por ventanas solapadas
y = np.zeros(len(x))
ventana = np.hamming(tamano_ventana)
n_segmentos = (len(x) - tamano_ventana) // solapamiento + 1

for i in range(n_segmentos):
    inicio = i * solapamiento
    fin = inicio + tamano_ventana
    if fin > len(x):
        break
    
    segmento = x[inicio:fin] * ventana
    X = sp.fft(segmento, L)
    X_shifted = sp.fftshift(X)
    Yf = X_shifted * H
    y_segmento = np.real(sp.ifft(np.fft.ifftshift(Yf)))
    
    y[inicio:fin] += y_segmento #* ventana  # Superposición y suma

# Normalizar la señal filtrada
y = y / np.max(np.abs(y))
n = np.arange(0, len(y)) / fs

# FFT de toda la slen(y)eñal
X = sp.fft(x, len(x))
X_shifted = sp.fftshift(X)
freqs_X_final = np.fft.fftshift(np.fft.fftfreq(len(X), d))
    
Y = sp.fft(y, len(y))
Y_shifted = sp.fftshift(Y)
freqs_Y_final = np.fft.fftshift(np.fft.fftfreq(len(Y), d))

# Graficar resultados
plt.figure(figsize=(12, 6))

plt.subplot(2, 2, 1)
plt.plot(n, x[:len(n)])
plt.xlabel('Tiempo (s)')
plt.ylabel('Amplitud')
plt.title('Señal Original')
plt.grid()

plt.subplot(2, 2, 2)
plt.semilogy(freqs_X_final, np.abs(X_shifted), 'r')
plt.xlabel('Frecuencia (Hz)')
plt.ylabel('|X(f)|  Escala Logarítmica')
plt.title('Espectro de Magnitud (Original)')
plt.xlim(-5000, 5000)
plt.grid()

plt.subplot(2, 2, 3)
plt.plot(n, y[:len(n)])
plt.xlabel('Tiempo (s)')
plt.ylabel('Amplitud')
plt.title('Señal Filtrada con Ventanas Solapadas')
plt.grid()

plt.subplot(2, 2, 4)
plt.semilogy(freqs_Y_final, np.abs(Y_shifted), 'r')
plt.xlabel('Frecuencia (Hz)')
plt.ylabel('|Y(f)|  Escala Logarítmica')
plt.title('Espectro después del Filtrado')
plt.xlim(-5000, 5000)
plt.grid()

plt.tight_layout()
plt.show()

sd.play(y, fs)
sd.wait()  # Espera a que termine la reproducción

