import cv2

# Selecciona el filtro (0: Canny, 1: Sobel, 2: Laplacian, 3: Prewitt, 4: Scharr)
tipofil = 1
# Modo (True: solo bordes, False: con fondo)
modo = True

# peso del frame original
alpha = 0.8  
# peso de los bordes
beta = 0.4   
# valor constante sumado a la combinación
gamma = 0.3  

filtros = ["Canny", "Sobel", "Laplacian", "Scharr"]

capture = cv2.VideoCapture(0)

while capture.isOpened():
    ret, frame = capture.read()
    if not ret:
        break

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    if tipofil == 0:
        edges = cv2.Canny(gray, 100, 200)
    elif tipofil == 1:
        edges = cv2.Sobel(gray, cv2.CV_64F, 1, 0, ksize=5)
    elif tipofil == 2:
        edges = cv2.Laplacian(gray, cv2.CV_64F)
        edges = cv2.convertScaleAbs(edges)
    elif tipofil == 3:
        edges = cv2.Scharr(gray, cv2.CV_64F, 1, 0) + cv2.Scharr(gray, cv2.CV_64F, 0, 1)
        edges = cv2.convertScaleAbs(edges)

    if modo:
        imsal = edges
    else:
        edges_color = cv2.cvtColor(edges, cv2.COLOR_GRAY2BGR)
        imsal = cv2.addWeighted(frame, alpha, edges_color, beta, gamma)

    cv2.putText(imsal, f"Filtro: {filtros[tipofil]}", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)

    cv2.imshow('Webcam con Filtro', imsal)

    key = cv2.waitKey(1) & 0xFF
    if key == ord('q'):
        break
    elif key == ord('b'):
        modo = not modo if tipofil != 1 else modo
    elif key == ord('f'):
        tipofil = (tipofil + 1) % filtros.__len__()
        if tipofil == 1:
            tipofil = (tipofil + 1) % filtros.__len__()

capture.release()
cv2.destroyAllWindows()
