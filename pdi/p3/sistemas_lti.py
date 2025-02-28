import numpy as np
import matplotlib.pyplot as plt
from scipy.io.wavfile import read, write
from scipy import signal
import sounddevice as sd
import time

fs, x  = read('hola_22050.wav')
amplitude = np.iinfo(np.int16).max
x =  x/amplitude

"================================================="
" Convolución de señales "
tipoh = 4

Lx = len(x)
nx = np.arange(0,Lx)/fs
if tipoh == 1:
    "código generación h tipo 1"
    Lh = Lx
    h = (nx==0.2) + (nx==0.4)

elif tipoh == 2:
    # camino directo -> n = 0
    # h(n) = delta(n) + 0.5 delta(n - 0.1) + 0.25 delta(n - 0.2)
    Lh = Lx
    h = (nx==0) + 0.5 * (nx == 0.1) + 0.25 * (nx == 0.2)

elif tipoh == 3:
    "código generación h tipo 3"
    a = 0.99
    Lh = 100
    nh = np.arange(0,Lh)
    h = a ** nh
    rebotes = sum(abs(h)>0.001)

elif tipoh == 4:
    "código generación h tipo 4"
    fh, h  = read('golpe.wav')
    h =  h/amplitude
    Lh = len(h)
elif tipoh == 5:
    "código generación h tipo 5"
    fh, h  = read('golpe_eco.wav')
    h =  h/amplitude
    Lh = len(h)



nh = np.arange(0,Lh)/fs

y = signal.convolve(x,h)
Ly = len(y)
ny = np.arange(0,Ly)/fs

sd.play(x, fs)
sd.wait()

sd.play(y, fs)
sd.wait()
sd.play(h, fh)
sd.wait()

plt.subplot(311)
plt.stem(nx, x, '-.')
plt.subplot(312)
plt.stem(nh, h, '-.')
plt.subplot(313)
plt.stem(ny, y, '-.')
plt.show()
plt.plot(nh, h)
plt.show()
