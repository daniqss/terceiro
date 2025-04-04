import numpy as np
import matplotlib.pyplot as plt
from skimage import io
tipofil = 1 #1: paso bajo ideal 2: Butterworth paso bajo, 3: paso alto ideal 
D0 = 0.1
n = 2
imagen12 = io.imread("Baboon.tif")/255.0
show_rgb = io.imshow(imagen12)
io.show()
# DFT de imagen
ft12 = np.fft.fft2(imagen12)
ft12 = np.fft.fftshift(ft12)

if (tipofil == 1):
    lx = len(ft12[:,1])
    ly = len(ft12[:,2])
    F1 = np.arange(-lx/2 + 1, lx/2 + 1)
    F2 = np.arange(-ly/2 + 1, ly/2 + 1)
    [X,Y] = np.meshgrid(F1, F2) # arreglo matricial de las combinaciones
    D = np.sqrt(X**2 + Y**2) # distancia del centro
    D=D/np.max(D)
    Haux = np.zeros([lx, ly])
    for i in range(lx):
        for j in range(ly):
            if (D[i,j] < D0):
                Haux[i,j]=1

elif (tipofil == 2):
    lx = len(ft12[:,1])
    ly = len(ft12[:,2])
    F1 = np.arange(-lx/2 + 1, lx/2 + 1)
    F2 = np.arange(-ly/2 + 1, ly/2 + 1)
    [X,Y] = np.meshgrid(F1, F2) # arreglo matricial de las combinaciones
    D = np.sqrt(X**2 + Y**2) # distancia del centro
    D=D/np.max(D)
    Haux = np.zeros([lx, ly])
    for i in range(lx):
        for j in range(ly):
            Haux[i,j] = 1 / (1 + (D[i,j]/D0) ** (2*n))
elif (tipofil == 3):
    lx = len(ft12[:,1])
    ly = len(ft12[:,2])
    F1 = np.arange(-lx/2 + 1, lx/2 + 1)
    F2 = np.arange(-ly/2 + 1, ly/2 + 1)
    [X,Y] = np.meshgrid(F1, F2)
    D = np.sqrt(X**2 + Y**2)
    D=D/np.max(D)
    Haux = np.zeros([lx, ly])
    for i in range(lx):
        for j in range(ly):
            if (D[i,j] > D0):
                Haux[i,j]=1

#Filtrado
ftHf = Haux * ft12
#Inversa de la dft
rec12 = np.fft.ifft2(ftHf)
rec12 = np.abs(rec12)

plt.subplot(321)
plt.set_cmap("gray")
plt.imshow(imagen12)
plt.subplot(322)
plt.imshow(20 * np.log10(1+abs(ft12)))
plt.subplot(324)
plt.imshow(Haux)
plt.subplot(325)
plt.imshow(rec12)
plt.subplot(326)
plt.imshow(20 * np.log10(1+abs(ftHf)))
plt.show()