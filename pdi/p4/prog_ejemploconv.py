import matplotlib.pyplot as plt
import numpy as np
from scipy import signal

" Convolución de señales "
x = [1, 1]
Lx = len(x)
nx = np.arange(0,Lx)
# amplitud de 0.5
h = np.multiply(x, 0.5)
# desplazar señal
h = np.concatenate([[0, 0], h])
Lh = len(h)
nh = np.arange(0,Lh)

y = signal.convolve(x,h)
Ly = len(y)
ny = np.arange(0,Ly)


plt.subplot(311)
plt.stem(nx, x, '-.')
plt.subplot(312)
plt.stem(nh, h, '-.')
plt.subplot(313)
plt.stem(ny, y, '-.')
plt.show()