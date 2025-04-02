import numpy as np
import matplotlib.pyplot as plt
import scipy.fft as sp


fs = 22100
duracion = 22100
n = np.arange(duracion) / fs

f = 1000
Na = 10
L = len(n)
d = 1 / fs
media = 0
sigma = 1

xcos = np.cos(2 * np.pi * f * n)
for k in range(1, Na):
    xcos += np.cos(2 * np.pi * (k + 1) * f * n)

X = sp.fft(xcos, L)
freqs = np.fft.fftfreq(L, d)
X_shifted = sp.fftshift(X)
freqs_shifted = np.fft.fftshift(freqs)

plt.figure(figsize=(10, 4))
plt.stem(freqs_shifted, np.abs(X_shifted))
plt.xlabel("Frecuencia (Hz)")
plt.ylabel("Magnitud")
plt.title("DFT de la señal original")
plt.grid()
plt.show()

xn = np.random.normal(media, sigma, L)
x = xcos + xn

X_noisy = sp.fft(x, L)
X_noisy_shifted = sp.fftshift(X_noisy)

plt.figure(figsize=(10, 4))
plt.stem(freqs_shifted, np.abs(X_noisy_shifted))
plt.xlabel("Frecuencia (Hz)")
plt.ylabel("Magnitud")
plt.title("DFT de la señal con ruido")
plt.grid()
plt.show()

fcorte = 1500
HPB = (freqs_shifted > -fcorte) & (freqs_shifted < fcorte)
X_filtered = X_shifted * HPB
x_filtered = sp.ifft(sp.ifftshift(X_filtered)).real

plt.figure(figsize=(10, 4))
plt.stem(freqs_shifted, np.abs(X_filtered))
plt.xlabel("Frecuencia (Hz)")
plt.ylabel("Magnitud")
plt.title("DFT - Filtro Paso Bajo (Fundamental)")
plt.grid()
plt.show()

HPB2 = ((freqs_shifted > -2500) & (freqs_shifted < -500)) | ((freqs_shifted > 500) & (freqs_shifted < 2500))
X_filtered2 = X_shifted * HPB2
x_filtered2 = sp.ifft(sp.ifftshift(X_filtered2)).real

plt.figure(figsize=(10, 4))
plt.stem(freqs_shifted, np.abs(X_filtered2))
plt.xlabel("Frecuencia (Hz)")
plt.ylabel("Magnitud")
plt.title("DFT - Filtro Paso Banda (Fundamental + 1er Armónico)")
plt.grid()
plt.show()

f_last = f * Na
HPA = (freqs_shifted > f_last - 500) & (freqs_shifted < f_last + 500)
X_filtered3 = X_shifted * HPA
x_filtered3 = sp.ifft(sp.ifftshift(X_filtered3)).real

plt.figure(figsize=(10, 4))
plt.stem(freqs_shifted, np.abs(X_filtered3))
plt.xlabel("Frecuencia (Hz)")
plt.ylabel("Magnitud")
plt.title("DFT - Filtro Último Armónico")
plt.grid()
plt.show()