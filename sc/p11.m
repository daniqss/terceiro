% Simulación de Modulaciones Digitales sobre Canales AWGN
% Práctica 1 - Software de Comunicaciones

clear;
close all;

N = 10000;
EbN0dB = 0:1:10;

modulations = {'BPSK', 'QPSK', '16-QAM', '64-QAM'};
M_values = [2, 4, 16, 64];
colors = {'b-o', 'r-s', 'g-d', 'm-^'};
linestyles = {'-', '--'};

BER_normal = zeros(length(modulations), length(EbN0dB));
BER_gray = zeros(length(modulations), length(EbN0dB));
BER_theoretical = zeros(length(modulations), length(EbN0dB));

for mod_idx = 1:length(modulations)
    M = M_values(mod_idx);
    b = log2(M);
    
    N_adjusted = floor(N/b)*b;
    
    % generar N bits de forma aleatoria con probabilidad p(0) = p(1) = 0.5
    bits = randn(1, N_adjusted) > 0.5;
    
    % reshape bits para procesar b bits a la vez
    bits_reshaped = reshape(bits, b, []);
    indices = bit2int(bits_reshaped, b);

    
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
    Eb_normal = Es_normal / b;
    
    symbols_gray = constellation_gray(indices + 1);
    Es_gray = mean(abs(constellation_gray).^2);
    Eb_gray = Es_gray / b;
    
    for ebn0_idx = 1:length(EbN0dB)
        ebn0 = 10^(EbN0dB(ebn0_idx)/10);
        
        if M == 2
            BER_theoretical(mod_idx, ebn0_idx) = 0.5 * erfc(sqrt(ebn0));
        elseif M == 4
            BER_theoretical(mod_idx, ebn0_idx) = erfc(sqrt(ebn0));
        elseif M == 16 || M == 64
            BER_theoretical(mod_idx, ebn0_idx) = 2 * ((sqrt(M) - 1) / sqrt(M)) * erfc(sqrt((3 * log2(M) / (2*M - 2)) * ebn0));
        end
        
        % SIMULACIÓN PARA CONSTELACIÓN NORMAL
        N0_normal = Eb_normal / ebn0;
        
        if M == 2 || M == 4
            noise_normal = sqrt(N0_normal/2) * randn(1, length(symbols_normal));
        else
            noise_normal = sqrt(N0_normal/2) * (randn(1, length(symbols_normal)) + 1j*randn(1, length(symbols_normal)));
        end
        received_normal = symbols_normal + noise_normal;


        % distancia euclídea para calculo del error
        demod_indices_normal = zeros(1, length(received_normal));
        for ii = 1:length(received_normal)
            % ~ para non gardar o valor
            [~, p] = min(abs(received_normal(ii) - constellation_normal));
            demod_indices_normal(ii) = p - 1;
        end
        
        % Convertir índices a bits
        demod_bits_normal = [];
        for ii = 1:length(demod_indices_normal)
            demod_bit = de2bi(demod_indices_normal(ii), b);
            demod_bits_normal = [demod_bits_normal, demod_bit];
        end
        
        errors_normal = sum(bits ~= demod_bits_normal);
        BER_normal(mod_idx, ebn0_idx) = errors_normal / N_adjusted;
        
        % SIMULACIÓN PARA CONSTELACIÓN GRAY
        N0_gray = Eb_gray / ebn0;
        
        
        if M == 2 || M == 4
            noise_gray = sqrt(N0_gray/2) * randn(1, length(symbols_gray));
        else
            noise_gray = sqrt(N0_gray/2) * (randn(1, length(symbols_gray)) + 1j*randn(1, length(symbols_gray)));
        end

        received_gray = symbols_gray + noise_gray;

        
        demod_indices_gray = zeros(1, length(received_gray));
        for ii = 1:length(received_gray)
            [~, idx] = min(abs(received_gray(ii) - constellation_gray));
            demod_indices_gray(ii) = idx - 1;
        end
        
        demod_bits_gray = [];
        for ii = 1:length(demod_indices_gray)
            demod_bit = de2bi(demod_indices_gray(ii), b);
            demod_bits_gray = [demod_bits_gray, demod_bit];
        end
        
        errors_gray = sum(bits ~= demod_bits_gray);
        BER_gray(mod_idx, ebn0_idx) = errors_gray / N_adjusted;
    end    
end

% Gráfica BER Binario
figure;
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_normal(mod_idx, :), colors{mod_idx}, 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
end
grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('BER vs Eb/N0 usando binario');
legend(modulations, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

% Gráfica BER Gray
figure;
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_gray(mod_idx, :), colors{mod_idx}, 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
end
grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('BER vs Eb/N0 usando mapeo Gray');
legend(modulations, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

% Gráfica comparativa Binario vs Gray
figure;
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_normal(mod_idx, :), [colors{mod_idx}(1) linestyles{1} colors{mod_idx}(3:end)], 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
    
    semilogy(EbN0dB, BER_gray(mod_idx, :), [colors{mod_idx}(1) linestyles{2} colors{mod_idx}(3:end)], 'LineWidth', 1.5, 'MarkerSize', 6);
end

grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('Comparación de BER: Binario vs Gray');
legend_entries = {};
for ii = 1:length(modulations)
    legend_entries = [legend_entries, [modulations{ii} ' Binario'], [modulations{ii} ' Gray']];
end
legend(legend_entries, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

% Gráfica comparativa Experimental (Gray) vs Teórico
figure;
markers = {'o', 's', 'd', '^'};
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_gray(mod_idx, :), [colors{mod_idx}(1) '-' markers{mod_idx}], 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
    
    semilogy(EbN0dB, BER_theoretical(mod_idx, :), [colors{mod_idx}(1) ':'], 'LineWidth', 2);
end

grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('Comparación de BER: Experimental (Gray) vs Teórico');
legend_entries = {};
for ii = 1:length(modulations)
    legend_entries = [legend_entries, [modulations{ii} ' Experimental'], [modulations{ii} ' Teórico']];
end
legend(legend_entries, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

figure;
markers = {'o', 's', 'd', '^'};
for mod_idx = 1:length(modulations)
    semilogy(EbN0dB, BER_normal(mod_idx, :), [colors{mod_idx}(1) '-' markers{mod_idx}], 'LineWidth', 1.5, 'MarkerSize', 6);
    hold on;
    
    semilogy(EbN0dB, BER_theoretical(mod_idx, :), [colors{mod_idx}(1) ':'], 'LineWidth', 2);
end

grid on;
xlabel('E_b/N_0 (dB)');
ylabel('BER');
title('Comparación de BER: Experimental (Binario) vs Teórico');
legend_entries = {};
for ii = 1:length(modulations)
    legend_entries = [legend_entries, [modulations{ii} ' Experimental (Binario)'], [modulations{ii} ' Teórico']];
end
legend(legend_entries, 'Location', 'southwest');
axis([min(EbN0dB) max(EbN0dB) 1e-6 1]);
set(gca, 'FontSize', 12);

function bits = de2bi(decimal, num_bits)
    bits = zeros(1, num_bits);
    for ii = 1:num_bits
        bits(ii) = bitget(decimal, num_bits - ii + 1);
    end
end