from skimage import io
import numpy as np
import matplotlib.pyplot as plt

imagen = io.imread("peppers.png")
N = 2
y1, x1, _ = imagen.shape


# diezmado sobre peppers.png
imagen_diezmada = np.zeros((y1 // N, x1 // N, 3), dtype=np.uint8)
y2, x2, _ = imagen_diezmada.shape
for i in range(y1):
    for j in range(x1):
        if i % 2 == 0 and j % 2 == 0:
            imagen_diezmada[i // N, j // N] = imagen[i, j]


# recuperacion haciendo la media de los vecinos
imagen_recuperada = imagen.copy()
for i in range(y1):
    for j in range(x1):
        if i % 2 == 0 and j % 2 == 0:
            continue
            
        vecinos = []

        if i > 0:
            vecinos.append(imagen_recuperada[i-1, j])
        if i < y1 - 1 :
            vecinos.append(imagen_recuperada[i+1, j])
        if j > 0:
            vecinos.append(imagen_recuperada[i, j-1])
        if j < x1-1:
            vecinos.append(imagen_recuperada[i, j+1])
            
        if i > 0 and j > 0:
            vecinos.append(imagen_recuperada[i-1, j-1])
        if i > 0 and j < x1-1:
            vecinos.append(imagen_recuperada[i-1, j+1])
        if i < y1-1 and j > 0:
            vecinos.append(imagen_recuperada[i+1, j-1])
        if i < y1-1 and j < x1-1:
            vecinos.append(imagen_recuperada[i+1, j+1])
            
        if vecinos:
            vecinos = np.array(vecinos)
            imagen_recuperada[i, j] = np.mean(vecinos, axis=0).astype(np.uint8)

for image in [imagen, imagen_diezmada, imagen_recuperada]:
    plt.figure()
    plt.imshow(image)
    plt.title("peppers")
    plt.show()