import numpy as np
import heapq
from typing import List, Dict, Optional, Tuple
import matplotlib.pyplot as plt
import math


class HuffmanNode:
    def __init__(self, symbol: Optional[int] = None, frequency: Optional[int] = None):
        self.symbol: Optional[int] = symbol
        self.frequency: Optional[int] = frequency
        self.left: Optional[HuffmanNode] = None
        self.right: Optional[HuffmanNode] = None

    def __lt__(self, other: 'HuffmanNode') -> bool:
        return self.frequency < other.frequency


def build_huffman_tree(chars: List[int], freq: List[int]) -> HuffmanNode:
    priority_queue: List[HuffmanNode] = [HuffmanNode(char, f) for char, f in zip(chars, freq)]
    heapq.heapify(priority_queue)

    while len(priority_queue) > 1:
        left_child = heapq.heappop(priority_queue)
        right_child = heapq.heappop(priority_queue)
        merged_node = HuffmanNode(frequency=left_child.frequency + right_child.frequency)
        merged_node.left = left_child
        merged_node.right = right_child
        heapq.heappush(priority_queue, merged_node)

    return priority_queue[0]


def generate_huffman_codes(
    node: Optional[HuffmanNode],
    code: str,
    huffman_codes: Dict[int, str]
) -> Dict[int, str]:
    if node is not None:
        if node.symbol is not None:
            huffman_codes[node.symbol] = code
        generate_huffman_codes(node.left, code + "0", huffman_codes)
        generate_huffman_codes(node.right, code + "1", huffman_codes)
    return huffman_codes


def uniform_quantize(signal: np.ndarray, Xmax: float, N: int) -> Tuple[np.ndarray, np.ndarray]:
    delta = 2 * Xmax / N
    levels = np.linspace(-Xmax + delta / 2, Xmax - delta / 2, N)
    indices = np.digitize(signal, levels) - 1
    indices = np.clip(indices, 0, N - 1)
    quantized_signal = levels[indices]
    return indices, quantized_signal


def calculate_entropy(freqs: List[int]) -> float:
    h = 0.0
    sumFreqs = sum(freqs)

    for f in freqs:
        if f > 0:
            pi = f / sumFreqs
            h += pi * math.log2(pi)
    return -h


def fixed_length_encode(indices: np.ndarray, num_levels: int) -> str:
    bits_per_symbol = math.ceil(np.log2(num_levels))
    return ''.join(format(i, f'0{bits_per_symbol}b') for i in indices)


def huffman_encode(indices: np.ndarray) -> Tuple[str, Dict[int, str], List[int], List[int]]:
    values, counts = np.unique(indices, return_counts=True)
    root = build_huffman_tree(values.tolist(), counts.tolist())
    codes = generate_huffman_codes(root, "", {})
    encoded = ''.join(codes[i] for i in indices)
    return encoded, codes, values.tolist(), counts.tolist()


def main() -> None:
    f: int = 20
    fs: int = 1000
    Xmax: float = 1.1
    duracion = 1000
    nx = np.arange(duracion) / fs
    x = np.cos(2 * np.pi * f * nx)

    mse_list = []
    bits_fixed_list = []
    bits_huffman_list = []
    entropies = []
    longitudes_teoricas = []
    niveles = [2, 4, 8, 16, 32, 64, 128]

    for N in niveles:
        indices, quantized = uniform_quantize(x, Xmax, N)
        mse = np.mean((x - quantized) ** 2)
        cycle_samples = fs // f

        # codificación fija
        fixed_encoded = fixed_length_encode(indices, N)
        bits_fixed = len(fixed_encoded) / cycle_samples

        # codificación Huffman
        huffman_encoded, codes, symbols, freqs = huffman_encode(indices)
        bits_huffman = len(huffman_encoded) / cycle_samples

        # entropía
        entropy = calculate_entropy(freqs)

        # longitud teórica promedio del código Huffman
        total_freq = sum(freqs)
        p = [f / total_freq for f in freqs]
        l_teorica = sum(p[i] * len(codes[symbols[i]]) for i in range(len(symbols)))

        mse_list.append(mse)
        bits_fixed_list.append(bits_fixed)
        bits_huffman_list.append(bits_huffman)
        entropies.append(entropy)
        longitudes_teoricas.append(l_teorica)

        if N == 8:
            print(f"=== Resultados para f={f}Hz, fs={fs}Hz, N={N} ===")
            print(f"entropía: {entropy:.4f}")
            print(f"longitud teórica promedio del código Huffman: {l_teorica:.4f}")
            print(f"error cuadrático medio (MSE): {mse:.6f}")
            print(f"bits por ciclo (PCM longitud fija): {bits_fixed:.4f}")
            print(f"bits por ciclo (Huffman): {bits_huffman:.4f}")
            print(f"codigos: {codes}")

    # curva MSE vs número medio de bits por ciclo
    plt.figure(figsize=(8, 5))
    plt.plot(bits_fixed_list, mse_list, label="Longitud fija", marker='o')
    plt.plot(bits_huffman_list, mse_list, label="Huffman", marker='s')
    plt.xlabel("Número medio de bits por ciclo")
    plt.ylabel("Error cuadrático medio (MSE)")
    plt.title("MSE vs Número medio de bits por ciclo")
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    main()
