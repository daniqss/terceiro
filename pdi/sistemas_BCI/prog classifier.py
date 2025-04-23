#==================================================================
# Determine open or closed eyes
# Created by: Queijo Seoane, Daniel
#==================================================================#

#============= BLOCK 0 ==================
import numpy as np

N = 1024   
fs = 200
f1 = 8
f2 = 13
Th = 12000000
#============= BLOCK 1 ==================
dataload = np.loadtxt('Subject_test.csv', dtype=float, delimiter= ",")
posini = 0
nchannel = 1 #1 or 2
data = dataload[posini:posini+N, nchannel]
data = data - np.mean(data)
#============= BLOCK 2 ==================
FFTCh = np.fft.fft(data, N)
FFTCh = np.abs(FFTCh[0:int(N/2)])
#============= BLOCK 3 ==================
f = fs * np.arange(0, N/2) / N
posf = np.where((f1 < f) & (f < f2))[0]
val = np.mean(np.power(FFTCh[posf], 2))
print(val)


#============= BLOCK 4 ==================
if val < Th:
    mytext = "Ojos abiertos"
else:
    mytext = "ojos cerrados"
        
print(mytext)


