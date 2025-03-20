import numpy as np
import matplotlib.pyplot as plt
"================================================="
" Senal coseno"
valini = 0 # Valor inicial
duracion = 512 # Num muestras
fs = 22100 # Hz
f = 100
Na = 0
p = 2
media = 0
sigma = 0.3
# con 100 tenemos un transitorio
Lh = 100

"================================================="
" Generar señales "
nx = np.arange(valini,duracion)/fs
xcos = np.cos(2 * np.pi * f * nx)
xn = np.random.normal(media, sigma, duracion)
x = xcos + xn

plt.subplot(311)
plt.stem(nx, xcos, '-.')
plt.subplot(312)
plt.stem(nx, x, '-.')

"============================================"
" Filtrado en tiempo "
h = np.ones(Lh)/Lh
y = np.convolve(x,h)
lh = len(h)
nh = np.arange(0,lh)/fs
ly = len(y)
ny = np.arange(0,ly)/fs
plt.subplot(313)
plt.stem(ny, y, '-.')
plt.show()