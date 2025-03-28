import numpy as np
import matplotlib.pyplot as plt
import scipy.fft as sp


valini = 0
fs = 22100
duracion = 22100
n = np.arange(valini, duracion) / fs

media = 0
sigma = 1
lx = len(n)

f = 1000
Na = 20
L = lx
d = 1 / fs

xcos = np.cos(2 * np.pi * f * n)
for k in range(1, Na + 1):
    xcos += np.cos(2 * np.pi * (k + 1) * f * n)

X = sp.fft(xcos, L)
freqs = np.fft.fftfreq(L, d)
X_shifted = sp.fftshift(X)
freqs_shifted = np.fft.fftshift(freqs)

plt.stem(n, xcos)
plt.xlabel("Frecuencia (Hz)")
plt.ylabel("Magnitud")
plt.title("DFT de la señal sin ruido")
plt.show()

xn = np.random.normal(media, sigma, lx)
x = xcos + xn

X = sp.fft(xcos, L)
freqs = np.fft.fftfreq(L, d)
X_shifted = sp.fftshift(X)
freqs_shifted = np.fft.fftshift(freqs)

plt.stem(freqs_shifted, np.abs(X_shifted))
plt.xlabel("Frecuencia (Hz)")
plt.ylabel("Magnitud")
plt.title("DFT de la señal con ruido")
plt.show()
