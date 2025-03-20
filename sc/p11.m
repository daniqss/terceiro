% Simulación de Modulaciones Digitales sobre Canales AWGN
% Práctica 1 - Software de Comunicaciones
clear;
close all;
clc;

N = 10000;
EbN0dB = 0:1:10;

modulations = {'BPSK', 'QPSK', '16-QAM', '64-QAM'};
M_values = [2, 4, 16, 64];
colors = {'b-o', 'r-s', 'g-d', 'm-^'};
linestyles = {'-', '--'};

BER_normal = zeros(length(modulations), length(EbN0dB));
BER_gray = zeros(length(modulations), length(EbN0dB));

for mod_idx = 1:length(modulations)
    M = M_values(mod_idx);
    k = log2(M);
    
    N_adjusted = floor(N/k)*k;
    
    % generar N bits de forma aleatoria con probabilidad p(0) = p(1) = 0.5
    bits = randn(1, N_adjusted) > 0.5;
    
    % reshape bits para procesar k bits a la vez
    bits_reshaped = reshape(bits, k, []);
    indices = bit2int(bits_reshaped, k);

    
    if M == 2 % BPSK
        constellation_normal = [-1, 1];
        constellation_gray = [-1, 1];
    elseif M == 4 % QPSK
        constellation_normal = [-1-1j, -1+1j, 1-1j, 1+1j] / sqrt(2);
        constellation_gray = [-1-1j, -1+1j, 1+1j, 1-1j] / sqrt(2);
    elseif M == 16 % 16-QAM
        constellation_normal = qammod(0:M-1, M, "bin");
        constellation_gray = qammod(0:M-1, M, "gray");
    elseif M == 64 % 64-QAM
        constellation_normal = qammod(0:M-1, M, "bin");
        constellation_gray = qammod(0:M-1, M, "gray");
    end
    
    symbols_normal = constellation_normal(indices + 1);
    Es_normal = mean(abs(constellation_normal).^2);
    Eb_normal = Es_normal / k;
    
    symbols_gray = constellation_gray(indices + 1);
    Es_gray = mean(abs(constellation_gray).^2);
    Eb_gray = Es_gray / k;
    
    for ebn0_idx = 1:length(EbN0dB)
        ebn0 = 10^(EbN0dB(ebn0_idx)/10);
        
        % SIMULACIÓN PARA CONSTELACIÓN NORMAL
        N0_normal = Eb_normal / ebn0;
        
        noise_real = sqrt(N0_normal/2) * randn(size(symbols_normal));
        noise_imag = sqrt(N0_normal/2) * randn(size(symbols_normal));
        noise_normal = noise_real + 1i*noise_imag;
        
        received_normal = symbols_normal + noise_normal;

        demod_indices_normal = zeros(1, length(received_normal));
        for i = 1:length(received_normal)
            [~, idx] = min(abs(received_normal(i) - constellation_normal));
            demod_indices_normal(i) = idx - 1;
        end
        
        % Convertir índices a bits
        demod_bits_normal = [];
        for i = 1:length(demod_indices_normal)
            b = de2bi(demod_indices_normal(i), k);
            demod_bits_normal = [demod_bits_normal, b];
        end
        
        errors_normal = sum(bits ~= demod_bits_normal);
        BER_normal(mod_idx, ebn0_idx) = errors_normal / N_adjusted;
        
        % SIMULACIÓN PARA CONSTELACIÓN GRAY
        N0_gray = Eb_gray / ebn0;
        
        noise_real = sqrt(N0_gray/2) * randn(size(symbols_gray));
        noise_imag = sqrt(N0_gray/2) * randn(size(symbols_gray));
        noise_gray = noise_real + 1i*noise_imag;
        
        received_gray = symbols_gray + noise_gray;
        
        demod_indices_gray = zeros(1, length(received_gray));
        for i = 1:length(received_gray)
            [~, idx] = min(abs(received_gray(i) - constellation_gray));
            demod_indices_gray(i) = idx - 1;
        end
        
        demod_bits_gray = [];
        for i = 1:length(demod_indices_gray)
            b = de2bi(demod_indices_gray(i), k);
            demod_bits_gray = [demod_bits_gray, b];
        end
        
        errors_gray = sum(bits ~= demod_bits_gray);
        BER_gray(mod_idx, ebn0_idx) = errors_gray / N_adjusted;
    end
    
    fprintf('Completada la simulación para %s (normal y Gray)\n', modulations{mod_idx});
end

figure;
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_normal(mod_idx, :), colors{mod_idx}, 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
end
grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('BER vs Eb/N0 para Modulaciones con Mapeo Binario');
legend(modulations, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

figure;
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_gray(mod_idx, :), colors{mod_idx}, 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
end
grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('BER vs Eb/N0 para Modulaciones con Mapeo Gray');
legend(modulations, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

figure;
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_normal(mod_idx, :), [colors{mod_idx}(1) linestyles{1} colors{mod_idx}(3:end)], 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
    
    semilogy(EbN0dB, BER_gray(mod_idx, :), [colors{mod_idx}(1) linestyles{2} colors{mod_idx}(3:end)], 'LineWidth', 1.5, 'MarkerSize', 6);
end

grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('Comparación de BER: Codificación Binaria vs Gray');
legend_entries = {};
for i = 1:length(modulations)
    legend_entries = [legend_entries, [modulations{i} ' Binario'], [modulations{i} ' Gray']];
end
legend(legend_entries, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

function bits = de2bi(decimal, num_bits)
    bits = zeros(1, num_bits);
    for i = 1:num_bits
        bits(i) = bitget(decimal, num_bits-i+1);
    end
end