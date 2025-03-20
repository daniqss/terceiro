import numpy as np
import matplotlib.pyplot as plt
from skimage import io
from scipy import signal

         
"================== Parametros ========= "
N = 3 # Tamaño de la mascara. Normalmente 3 o 5

tipoFiltro = 2 #1:paso bajo  2:paso alto

p = 0.05
m = 0
d = 100

tipoMascara = 1
tipoGradiente = 1
th = 50

" Leer imagen"
img = io.imread("./Cameraman.tif")
im = io.imshow(img, cmap = 'gray')
io.show()

# filtro paso bajo
if tipoFiltro == 1:
    mascara = np.ones((N,N))/(N ** 2) 
    
    im = io.imshow(mascara, cmap ='gray')
    io.show()

    tamimg = img.shape
    R = np.sqrt(d)*np.random.randn(tamimg[0], tamimg[1]) + m
    Img1 = img + R

    im = io.imshow(Img1, cmap ='gray')
    io.show()

    # imfil = signal.convolve2d(img, mascara)
    # im = io.imshow(imfil, cmap ='gray')
    # io.show()

    imfil2 = signal.convolve2d(Img1, mascara)
    im = io.imshow(imfil2, cmap ='gray')
    io.show()


     

# filtro paso alto
elif tipoFiltro == 2:
    mascara = np.array([[0, -1, 0], [-1, 4, -1], [0, -1, 0]]) if tipoMascara == 1 else np.array([[-1, -1, -1], [-1, 8, -1], [-1, -1, -1]])
    grad_x = np.array([[-1, -1, -1], [0, 0, 0], [1, 1, 1]]) if tipoGradiente == 1 else np.array([[-1, 0, 1], [-1, 0, 1], [-1, 0, 1]])
    grad_y = grad_x.T

    plt.imshow(mascara, cmap='gray')
    plt.title("Máscara")
    plt.show()

    If = signal.convolve2d(img, grad_x, mode='same', boundary='symm')
    plt.imshow(If, cmap='gray')
    plt.title("If")
    plt.show()

    Ic = signal.convolve2d(img, grad_y, mode='same', boundary='symm')
    plt.imshow(Ic, cmap='gray')
    plt.title("Ic")
    plt.show()

    Ir = np.abs(If) + np.abs(Ic)
    plt.imshow(Ir, cmap='gray')
    plt.title("Ir")
    plt.show()

    U = 90
    Imgu = (Ir <= U) * 0 + (Ir > U) * 255
    plt.imshow(Imgu, cmap='gray')
    plt.title(f"Iu con U={U}")
    plt.show()
