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
plt.title("DFT de la señal sin ruido")
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
