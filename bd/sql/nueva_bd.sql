-- 1. Obter os datos completos dos alumnos que nunca escribiron no foro.
SELECT email 
FROM alumno a
WHERE NOT EXISTS (
    SELECT *
    FROM mem_foro f
    WHERE f.email = a.email
);
-- select *
-- from alumno
-- where email not in (
--     select email
--     from men_foro
--     where email is not null
-- )

-- 2. Obtén o profesor (nome) que máis mensaxes escribiu.
SELECT p.nome 
FROM profesor p JOIN men_foro f ON p.nss = f.nss
GROUP BY p.nss, p.nome
HAVING count(*) >= ALL(
    SELECT count(*)
    FROM men_foro f2
    WHERE f2.nss IS NOT NULL
    GROUP BY f2.nss
)

-- 3. Para cada profesor (nome) mostra o nome do curso, de entre todos os que está
-- capacitado para impartir, que dura máis horas
SELECT p.nome as profesor, cu.nome as curso, cu.horas
FROM profesor p JOIN capacitado ca ON p.nss = ca.nss
    JOIN curso cu ON ca.cod_c = cu.cod_c
WHERE cu.horas >= (
    SELECT max(cu2.horas)
    FROM capacitado ca2 JOIN curso cu2 ON ca2.cod_c=cu2.cod_c
    WHERE ca.nss = ca2.nss
)



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


