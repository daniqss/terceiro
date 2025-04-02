-- 1. Muestra los empleados que ganan más del salario medio de la empresa. Muestra
-- también dicho salario medio.
EXPLAIN SELECT ename, sal, (SELECT avg(sal) FROM Emp)
FROM Emp
WHERE sal > (SELECT avg(sal) FROM Emp)
-- +-------+----------------------------+
-- | ename | (SELECT avg(sal) FROM Emp) |
-- +-------+----------------------------+
-- | JONES |                2073.214286 |
-- | BLAKE |                2073.214286 |
-- | CLARK |                2073.214286 |
-- | SCOTT |                2073.214286 |
-- | KING  |                2073.214286 |
-- | FORD  |                2073.214286 |
-- +-------+----------------------------+
-- tambien lo podemos hacer con joins
-- SELECT ename, sal, sub.media
-- FROM Emp CROSS JOIN (
--     SELECT avg(sal) AS media
--     FROM Emp
-- ) AS sub
-- WHERE sal > sub.media
--
-- -- en posgres e en mariadb (entendo q en mysql tamen), podemos usar EXPLAIN para q nos dea informacion sobre como de eficiente é unha subconsulta
-- +------+-------------+-------+------+---------------+------+---------+------+------+-------------+
-- | id   | select_type | table | type | possible_keys | key  | key_len | ref  | rows | Extra       |
-- +------+-------------+-------+------+---------------+------+---------+------+------+-------------+
-- |    1 | PRIMARY     | Emp   | ALL  | NULL          | NULL | NULL    | NULL | 14   | Using where |
-- |    3 | SUBQUERY    | Emp   | ALL  | NULL          | NULL | NULL    | NULL | 14   |             |
-- |    2 | SUBQUERY    | Emp   | ALL  | NULL          | NULL | NULL    | NULL | 14   |             |
-- +------+-------------+-------+------+---------------+------+---------+------+------+-------------+

-- 2. Muestra los empleados que ganan más del salario medio de su departamento.
-- Muestra también dicho salario medio.
SELECT ename, sal, deptno, sub.salario_medio
FROM Emp e JOIN (
    SELECT deptno as depart, avg(sal) AS salario_medio
    FROM Emp
    GROUP BY deptno
) AS sub ON deptno = depart
WHERE sal > sub.salario_medio
-- +-------+---------+--------+---------------+
-- | ename | sal     | deptno | salario_medio |
-- +-------+---------+--------+---------------+
-- | ALLEN | 1600.00 |     30 |   1566.666667 |
-- | JONES | 2975.00 |     20 |   2175.000000 |
-- | BLAKE | 2850.00 |     30 |   1566.666667 |
-- | SCOTT | 3000.00 |     20 |   2175.000000 |
-- | KING  | 5000.00 |     10 |   2916.666667 |
-- | FORD  | 3000.00 |     20 |   2175.000000 |
-- +-------+---------+--------+---------------+
-- en oracle sql no hace falta utilizar aliases, mientras que en mariadb si

-- 3. Muestra de cada proyecto, el empleado que más horas trabaja.
SELECT prono, ename, hours
FROM Emp NATURAL JOIN Emppro
WHERE (prono, hours) = SOME (
    SELECT prono, max(hours)
    FROM Emppro
    GROUP BY prono
)
-- +-------+--------+-------+
-- | prono | ename  | hours |
-- +-------+--------+-------+
-- |  1004 | ALLEN  |    15 |
-- |  1005 | ALLEN  |    12 |
-- |  1008 | WARD   |     8 |
-- |  1001 | MARTIN |    16 |
-- |  1006 | MARTIN |    15 |
-- +-------+--------+-------+


-- 4. El empleado que ha trabajado más horas en cada ciudad. Muestra los nombres del
-- empleado y de la ciudad.
SELECT sum(hours) horas, loc, e.ename
FROM Emppro ep JOIN Pro p ON p.prono = ep.prono JOIN Emp e ON e.empno = ep.empno
GROUP BY ep.empno,e.ename, p.loc
HAVING sum(hours) >= ALL(
    SELECT sum(hours)
    FROM Emppro ep1 JOIN Pro p1 ON ep1.prono = p1.prono
    WHERE loc = p.loc
    GROUP BY ep1.empno
)
-- +-------+-------------+--------+
-- | horas | loc         | ename  |
-- +-------+-------------+--------+
-- |    27 | CHICAGO     | ALLEN  |
-- |     8 | NEW YORK    | WARD   |
-- |    16 | BOSTON      | MARTIN |
-- |    15 | LOS ANGELES | MARTIN |
-- +-------+-------------+--------+

-- 5. Muestra la media de horas dedicadas a proyectos (sumando las horas de todos los
-- empleados que trabajan en el proyecto).
SELECT avg(sub.horas_medias)
FROM (
    SELECT sum(hours) AS horas_medias, prono
    FROM Emppro
    GROUP BY prono
) AS sub
-- +-----------------------+
-- | avg(sub.horas_medias) |
-- +-----------------------+
-- |               18.2000 |
-- +-----------------------+
-- lo siguiente esta bien en oracle y en posgres, pero no es sql standard
-- SELECT avg(sum(hours))
-- FROM Emppro
-- GROUP BY prono

-- 6. Muestra los proyectos cuya suma de horas es mayor que la cantidad de horas
-- media dedicadas a proyectos. Debes mostrar dicha suma.
SELECT pname, sum(hours)
FROM Pro p JOIN Emppro ep ON ep.prono = p.prono
GROUP BY p.prono, pname
HAVING sum(hours) > (
    SELECT avg(sub.horas_medias)
    FROM (
        SELECT sum(hours) AS horas_medias
        FROM Emppro
        GROUP BY prono
    ) AS sub
)
ORDER BY p.prono
-- +-------+------------+
-- | pname | sum(hours) |
-- +-------+------------+
-- | P1    |         20 |
-- | P4    |         25 |
-- +-------+------------+