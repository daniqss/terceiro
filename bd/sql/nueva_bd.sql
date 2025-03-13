-- 6. Indica, para cada curso, a edición que tivo máis estudantes rexistrados. Mostra o
-- código e nome do curso, e o número da edición. (Só cursos con estudantes
-- rexistrados).
SELECT c.nome, e.numero
FROM curso c JOIN edicion e ON c.cod_c = e.cod_c
    JOIN rexistrase r ON e.cod_c = r.cod_c AND e.numero = r.numero
GROUP BY c.cod_c, c.nome, e.numero
HAVING count(*) >= ALL(
    SELECT count(*)
    FROM rexistrase re
    WHERE re.cod_c = c.cod_c
    GROUP BY re.numero
)
-- +--------------+--------+
-- | nome         | numero |
-- +--------------+--------+
-- | Camuflaxe    |      1 |
-- | Tortas       |      2 |
-- | Pócimas      |      1 |
-- | Detecta Malo |      2 |
-- | Detecta Malo |      3 |
-- +--------------+--------+


-- 8. Mostrar para cada curso, o seu nome e a edición/s con maior prezo.
EXPLAIN SELECT c.nome, e.numero, e.prezo
FROM curso c JOIN edicion e ON c.cod_c = e.cod_c
GROUP BY c.cod_c, c.nome, e.numero, e.prezo
HAVING e.prezo >= ALL(
    SELECT prezo
    FROM edicion ed
    WHERE ed.cod_c = c.cod_c
)
-- podemos facer tamen
-- HAVING e.prezo = (
--     SELECT max(prezo)
--     FROM edicion ed
--     WHERE ed.cod_c = c.cod_c
-- )
-- +--------------+--------+-------+
-- | nome         | numero | prezo |
-- +--------------+--------+-------+
-- | Camuflaxe    |      3 |   350 |
-- | Tortas       |      1 |   150 |
-- | Tortas       |      2 |   150 |
-- | Tortas       |      3 |   150 |
-- | Pócimas      |      2 |   300 |
-- | Pócimas      |      3 |   300 |
-- | Detecta Malo |      3 |   600 |
-- +--------------+--------+-------+

-- 14. Queremos saber cantas edicións existen en promedio para un curso.
SELECT avg(nu)
FROM (
	SELECT e.cod_c, count(numero) as nu
	FROM edicion e
	GROUP BY e.cod_c
) ediciones;
-- tambien podemos
-- no funciona en mariaDB, creo
SELECT sum(count(*)) / count(distinct cod_c)
FROM edicion e RIGHT JOIN curso c ON e.cod_c = c.cod_c
GROUP BY c.cod_c;
