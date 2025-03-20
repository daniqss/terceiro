import numpy as np
import matplotlib.pyplot as plt
from scipy import signal

" Crear máscara "
mascara = np.zeros((10,10))
mascara[:,:] = 1
mascara[:,0:5] = -1.
plt.imshow(mascara)
plt.colorbar()
plt.title("Máscara")
plt.show()

" Crear imágen sencilla"
img = np.zeros((100,100))
img[0:50,0:40] = 1.
img[50:100,0:60] = 1.
plt.imshow(img)
plt.colorbar()
plt.title("Imágen")
plt.show()

" Convolucionar"
imres = signal.convolve2d(img, mascara)
plt.imshow(imres)
plt.colorbar()
plt.show()