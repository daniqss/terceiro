import numpy as np
import matplotlib.pyplot as plt
from scipy.io.wavfile import read
from scipy import signal
import sounddevice as sd

fs, x = read('hola_22050.wav')
amplitude = np.iinfo(np.int16).max
x = x/amplitude

Lx = len(x)
nx = np.arange(0, Lx)/fs

Lh1 = Lx
nh1 = np.arange(0, Lh1)/fs
h1 = (nh1 == 0) + 0.5 * (nh1 == 0.1) + 0.25 * (nh1 == 0.2)

a = 0.8
Lh2 = 50
nh2 = np.arange(0, Lh2)/fs
h2 = a ** (nh2 * fs)

h = signal.convolve(h1, h2)
Lh = len(h)
nh = np.arange(0, Lh)/fs

y = signal.convolve(x, h)
Ly = len(y)
ny = np.arange(0, Ly)/fs

plt.figure(figsize=(10, 8))
plt.subplot(311)
plt.stem(nh1, h1, '-.')
plt.title('h1(n)')

plt.subplot(312)
plt.stem(nh2, h2, '-.')
plt.title('h2(n)')

plt.subplot(313)
plt.stem(nh, h, '-.')
plt.title('h(n)')
plt.show()

plt.figure(figsize=(10, 6))
plt.subplot(211)
plt.plot(nx, x)
plt.title('x(n)')

plt.subplot(212)
plt.plot(ny, y)
plt.title('y(n)')
plt.show()

sd.play(x, fs)
sd.wait()

y_normalized = y / np.max(np.abs(y))
sd.play(y_normalized, fs)
sd.wait()
