import matplotlib.pyplot as plt
import numpy as np
import wave
import sys
from scipy.interpolate import lagrange

# Cargar el archivo de audio
spf = wave.open("download.wav", "r")

# Extraer el audio en bruto del archivo WAV
signal = spf.readframes(-1)
signal = np.frombuffer(signal, np.int16).copy()

# Si es estéreo, salir
if spf.getnchannels() == 2:
    print("Just mono files")
    sys.exit(0)

# Eliminar valores negativos
signal[signal < 0] = 0

# La señal completa es demasiado grande para interpolación de Lagrange
# Seleccionemos un número pequeño de puntos representativos
# Por ejemplo, tomemos 10 puntos equidistantes

num_points = 1000
indices = np.linspace(0, len(signal) - 1, num_points, dtype=int)
x_sample = indices
y_sample = signal[indices]

# Crear el polinomio de Lagrange
poly = lagrange(x_sample, y_sample)

# Crear puntos para graficar el polinomio
x_plot = np.linspace(min(x_sample), max(x_sample), 1000)
y_plot = poly(x_plot)

# Graficar la señal original y el polinomio interpolado
plt.figure(figsize=(12, 8))

plt.subplot(2, 1, 1)
plt.title("Señal de Audio Original (Sin Valores Negativos)")
plt.plot(signal)
plt.plot(x_sample, y_sample, 'ro', markersize=8, label='Puntos seleccionados')
plt.legend()

plt.subplot(2, 1, 2)
plt.title("Polinomio de Lagrange Interpolado")
plt.plot(x_plot, y_plot)
plt.plot(x_sample, y_sample, 'ro', markersize=8)

plt.tight_layout()
plt.show()

# Imprimir el polinomio (en forma de coeficientes)
poly_coefs = poly.coef
print("\nCoeficientes del polinomio de Lagrange (de mayor a menor grado):")
for i, coef in enumerate(poly_coefs):
    print(f"Coeficiente x^{len(poly_coefs)-i-1}: {coef}")

# Imprimir la función polinómica en formato legible
print("\nFunción polinómica de Lagrange:")
poly_str = "f(x) = "
for i, coef in enumerate(poly_coefs):
    power = len(poly_coefs) - i - 1
    if i > 0:
        if coef >= 0:
            poly_str += " + "
        else:
            poly_str += " - "
            coef = abs(coef)
    
    if power > 1:
        poly_str += f"{coef:.2e}x^{power}"
    elif power == 1:
        poly_str += f"{coef:.2e}x"
    else:
        poly_str += f"{coef:.2e}"

print(poly_str)
    
# Crear un ejemplo con datos sintéticos
print("\nCreando ejemplo con datos sintéticos:")

# Generar algunos puntos de ejemplo
x = np.array([0, 1, 2, 3, 4, 5])
y = np.array([0, 8, 12, 10, 15, 25])

# Aplicar interpolación de Lagrange
poly = lagrange(x, y)

# Crear puntos para graficar
x_plot = np.linspace(min(x), max(x), 100)
y_plot = poly(x_plot)

# Graficar
plt.figure(figsize=(10, 6))
plt.title("Ejemplo de Interpolación de Lagrange")
plt.plot(x_plot, y_plot, 'b-', label='Polinomio de Lagrange')
plt.plot(x, y, 'ro', markersize=8, label='Puntos de datos')
plt.grid(True)
plt.legend()
plt.show()

# Imprimir la función
poly_coefs = poly.coef
print("\nCoeficientes del polinomio de Lagrange (de mayor a menor grado):")
for i, coef in enumerate(poly_coefs):
    print(f"Coeficiente x^{len(poly_coefs)-i-1}: {coef}")

# Imprimir la función polinómica en formato legible
print("\nFunción polinómica de Lagrange:")
poly_str = "f(x) = "
for i, coef in enumerate(poly_coefs):
    power = len(poly_coefs) - i - 1
    if i > 0:
        if coef >= 0:
            poly_str += " + "
        else:
            poly_str += " - "
            coef = abs(coef)
    
    if power > 1:
        poly_str += f"{coef:.4f}x^{power}"
    elif power == 1:
        poly_str += f"{coef:.4f}x"
    else:
        poly_str += f"{coef:.4f}"

print(poly_str)