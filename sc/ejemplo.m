a = [1 2 3]

a = 0:5:30

// sobre os indices do vector
for ii = 1 : length(a)
    a(ii)
end

// pasarlle directamente o vector
// o ii = 0:5:30
for ii = a 
    ii
end

// NUMEROS COMPLEJOS
// todo juntito a parte complexa
2 + 1j.*43

// 2 columnas, todas as filas q sexan necesarias
bits = [ 1 0 0 1 0 1 1]
bitsR = reshape(bits, 2, [])

bitsR = [1 0 0 1 0 1 1]
bitsR = reshape(bitsR, [], 2)

// + 1 porque en matlab se empieza a contar por 1
ind = bit2int(bitsR, 2) + 1

sM = [-3 -1 1 3]
sM(ind)



////////// 

clear;
close all; 
N = 10;

bits = randn(1, N) > 0.5;

bR = reshape(bits, 2, [])
simbM = [-3 -1 1 3];
ind = bit2int(bR, 2) + 1;

