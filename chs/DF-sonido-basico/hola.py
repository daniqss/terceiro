xpuntos = [0, 19.45, 47.9,  66.53, 140, 222.82, 230.95, 235.95]
ypuntos = [0, 21.79, 23.52, 51.36, 40,  46.46,  5.19,   0]
nxpuntos = [] 
nypuntos = []
for i in range(len(xpuntos)):

    nxpuntos.append(34000*xpuntos[i]/240)
    nypuntos.append(34000*ypuntos[i]/240)
    print(f"x = {nxpuntos[i]:.4f} y = {nypuntos[i]:.4f} i = {i}\n")

for i in range(len(nxpuntos) - 1):
    m = (nypuntos[i+1] - nypuntos[i]) / (nxpuntos[i+1] - nxpuntos[i])
    b = nypuntos[i] - m * nxpuntos[i]
    print(f"x = {nxpuntos[i]:.4f} y = {nypuntos[i]:.4f} m = {m:.4f} b = {b:.4f}")
    print(f"y = {m:.4f}x + {b:.4f}\n")
    

