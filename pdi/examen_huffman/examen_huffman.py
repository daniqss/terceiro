import numpy as np

N = 4
X = np.matrix([[1, 1],
               [1, 1]])
a = 1 / 2 * np.sqrt(2)
A = np.matrix([
    [0, 0, 1, 0],
    [0, 0, 0, 1],
    [0, 1, 0, 0],
    [1, 0, 0, 0]
])
# A = np.random.randint(0, 2, size=(4, 4))

# A = a * A
I = np.eye(A.shape[0])

assert A.shape[0] % 2 == 0, "error must be even rows"
assert np.allclose(A @ A.T, I), f"error A is not orthogonal\n{A @ A.T}\n!=\n{I}"

print(f"A:\n {A}\n")
print(f"X:\n {X}\n")

block_size = 2
result = ""

for i in range(0, A.shape[0], block_size):
    for j in range(0, A.shape[1], block_size):
        Ai = A[i:i+block_size, j:j+block_size]
        Fi = Ai @ X @ Ai.T
        
        print(f"Block ({i}:{i+2}, {j}:{j+2}):")
        print(np.array2string(Fi, precision=3, suppress_small=True), "\n")

        for k, l in np.ndindex(Fi.shape):
            result += str(Fi[k, l])
        result += " "
    result += " "

print(result)