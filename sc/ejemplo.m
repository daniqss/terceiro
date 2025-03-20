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



% ////////// 

clear;
close all; 
N = 10;

bits = randn(1, N) > 0.5;

bR = reshape(bits, 2, [])
simbM = [-3 -1 1 3];
ind = bit2int(bR, 2) + 1;

% como xerar vector de ruido
vectorRuido = sqrt(No/2) * (randn(1, size(simbM)) + 1j*randn(1, size(simbM)));
% // EbN0 -> dB, 10dB -> 10
% // 10 * log10 (Eb/No)
% // N0 = Eb / 10^(EbN0/10)
% // N0 -> enerxia media de bit

% // sinal recibido
simRec = sindM + vectorRuido;

% // bit estimado
bitEst = []

// temos q mirar q recibimos e ver q é mais probable q sexa
for ii = 1 : length(simRec)
    // distancia euclidea
    // para simRec = 1.2 y simbM = [-3 -1 1 3]
    // obtendriamos 1 -> posicion 3
    dist = abs(simRec(ii) - simbM);
    // ~ para non gardar o valor
    [~, p] = min(dist);
    bitsRec(ii) = p - 1;
    // k bits int2bit(p, k)
    bitEst = [bitEst int2bit(p - 1, k)];
end

