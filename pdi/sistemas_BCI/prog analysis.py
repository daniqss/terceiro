#==================================================================
# Analysis of signals corresponding to open or closed eyes
# Created by: Adriana Dapena (CITIC, University of A Coruña)
#==================================================================#

#============= BLOCK 0 ==================
# Import Python libraries
import numpy as np
import matplotlib.pyplot as plt

# Definition of constant. 
fs = 200 #Sampling frequency
snum = 12180 #Position of closed eyes

#Parameters
windowNum = 0 #Window number (0, 1, 2, 3 or 4)
N = 1024  #Number of samples=frequency bins
nameFile = 'Subject_2.csv' #File name
    

#============= BLOCK 1 ==================
dataload = np.loadtxt(open(nameFile), dtype=float, delimiter= ",")

pos1 = 2 * windowNum * snum   # Open eyes
dataCh1Open = dataload[pos1:pos1 + N, 1]
dataCh1Open = dataCh1Open - np.mean(dataCh1Open)
dataCh2Open = dataload[pos1:pos1 + N, 2]
dataCh2Open = dataCh2Open - np.mean(dataCh2Open)

pos2 = (2 * windowNum + 1) * snum   # Closed eyes
dataCh1Closed = dataload[pos2:pos2 + N, 1]
dataCh1Closed = dataCh1Closed - np.mean(dataCh1Closed)
dataCh2Closed = dataload[pos2:pos2 + N, 2]
dataCh2Closed = dataCh2Closed - np.mean(dataCh2Closed)

#============= BLOCK 2 ==================
FFTCh1Open = np.fft.fft(dataCh1Open, N)
FFTCh1Open = np.abs(FFTCh1Open[0:int(N/2)])
FFTCh2Open = np.fft.fft(dataCh2Open, N)
FFTCh2Open = np.abs(FFTCh2Open[0:int(N/2)])
FFTCh1Closed = np.fft.fft(dataCh1Closed, N)
FFTCh1Closed = np.abs(FFTCh1Closed[0:int(N/2)])
FFTCh2Closed = np.fft.fft(dataCh2Closed, N)
FFTCh2Closed = np.abs(FFTCh2Closed[0:int(N/2)])

#============= BLOCK 3 ==================
t = 1/fs * np.arange(0, N)
plt.subplot(421)
plt.plot(t, dataCh1Open)
plt.axis((0, np.max(t), -100, 100))
plt.subplot(423)
plt.plot(t, dataCh2Open)
plt.axis((0, np.max(t), -100, 100))
plt.subplot(425)
plt.plot(t, dataCh1Closed)
plt.axis((0, np.max(t), -100, 100))
plt.subplot(427)
plt.plot(t, dataCh2Closed)
plt.axis((0, np.max(t), -100, 100))
plt.xlabel("Time (s)")

#============= BLOCK 4 ==================
f = fs * np.arange(0, N/2) / N
plt.subplot(422)
plt.plot(f, FFTCh1Open)
plt.axis((0, 50, 0, 5000))
plt.subplot(424)
plt.plot(f, FFTCh2Open)
plt.axis((0, 50, 0, 5000))
plt.subplot(426)
plt.plot(f, FFTCh1Closed)
plt.axis((0, 50, 0, 5000))
plt.subplot(428)
plt.plot(f, FFTCh2Closed)
plt.axis((0, 50, 0, 5000))
plt.xlabel("Frequency (Hz)")
plt.show()

#============= BLOCK 5 ==================
f1 = 8
f2 = 13
posf = np.where((f1 < f) & (f < f2))[0]

PowFFTCh1Open = np.mean(np.power(FFTCh1Open[posf], 2))
PowFFTCh2Open = np.mean(np.power(FFTCh2Open[posf], 2))
PowFFTCh1Closed = np.mean(np.power(FFTCh1Closed[posf], 2))
PowFFTCh2Closed = np.mean(np.power(FFTCh2Closed[posf], 2))

print("===================================")
print("Power of signals in frequency domain")
print("Power of FFT Ch1 Open", PowFFTCh1Open)
print("Power of FFT Ch1 Closed", PowFFTCh1Closed)
print("Power of FFT Ch2 Open", PowFFTCh2Open)
print("Power of FFT Ch2 Closed", PowFFTCh2Closed)