import numpy as np
import matplotlib.pyplot as plt
from scipy.io.wavfile import read, write
from scipy import signal
import scipy.io.wavfile

tipocanal = 1
if tipocanal == 1:
    Lh = 1000
    fs = 1000
    nh = np.arange(0,Lh)/fs
    h = (nh==0.2) + (nh==0.4)
elif tipocanal == 2:
    fh, h = read('golpe.wav')
    Lh = len(h)
    nh = np.arange(0,Lh)/fh
elif tipocanal == 3:
    fh, h = read('golpe_eco.wav')
    Lh = len(h)
    nh = np.arange(0,Lh)/fh
    
plt.stem(nh, h, '-.')
plt.show()

" Conexion en serie"
Lh = 10
fh = 10
nh = np.arange(0,Lh)/fh
h1 = (nh==0.2) + (nh==0.4)
h2 = (nh==0.2) + 0.5*(nh==0.4) + 0.25*(nh==0.6)
heq = signal.convolve(h1,h2)
lheq = len(heq)
neq = np.arange(0,lheq)/fh
plt.figure
plt.subplot(311)
plt.stem(nh, h1, '-.')
plt.subplot(312)
plt.stem(nh, h2, '-.')
plt.subplot(313)
plt.stem(neq, heq, '-.')
plt.title('Equivalente en serie')
plt.show()

" Conexion en paralelo"
heq = h1 + h2
lheq = len(heq)
neq = np.arange(0,lheq)/fh
neq = nh
plt.figure
plt.subplot(311)
plt.stem(nh, h1, '-.')
plt.subplot(312)
plt.stem(nh, h2, '-.')
plt.subplot(313)
plt.stem(neq, heq, '-.')
plt.title('Equivalente en paralelo')
