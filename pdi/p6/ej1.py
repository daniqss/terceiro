import numpy as np
import matplotlib.pyplot as plt
from skimage import io

for image in ['./Baboon.tif', './Barbara.tif']:
    imagen_original = io.imread(image)/255.0


    ft = np.fft.fft2(imagen_original)
    ft_shift = np.fft.fftshift(ft)

    
    ft_ishift = np.fft.ifftshift(ft_shift)
    imagen_recuperada = np.abs(np.fft.ifft2(ft_ishift))


    plt.figure(figsize=(15, 5))

    plt.subplot(131)
    plt.set_cmap("gray")
    plt.imshow(imagen_original)
    plt.title('Imagen Original')
    plt.axis('off')

    plt.subplot(132)
    plt.set_cmap("gray")
    # |D(u, v)| = log(1 + |F(u, v)|)
    plt.imshow(20 * np.log10(1 + np.abs(ft_shift)))
    plt.title('Módulo de la DFT centrada')
    plt.axis('off')

    # 3. Imagen recuperada
    plt.subplot(133)
    plt.set_cmap("gray")
    plt.imshow(imagen_recuperada)
    plt.title('Imagen Recuperada')
    plt.axis('off')

    plt.tight_layout()
    plt.show()