-- 1. Para cada departamento mostrar su nombre y cuántos empleados tiene, si no
-- tiene ninguno, indicarlo con un 0.

-- MAL MAL FATAL
-- SELECT dname, COALESCE(COUNT(empno), 0)
-- FROM Dept d JOIN Emp e ON d.deptno = e.deptno
-- solo me sale esto
-- +------------+---------------------------+
-- | dname      | COALESCE(COUNT(empno), 0) |
-- +------------+---------------------------+
-- | ACCOUNTING |                        14 |
-- +------------+---------------------------+

SELECT dname, COUNT(empno)
FROM Dept d LEFT JOIN Emp e ON d.deptno = e.deptno
GROUP BY d.deptno, dname;
-- dname si lo queremos mostrar debe estar en el group by
-- no hace falta coalesce porque el left o right join ya nos da 0 si no hay registros
-- hay q añadir left o rigth(exterior) join segun tengamos en el join el el Dept a la derecha o a la izquierda
-- como en este caso el Dept esta a la izquierda, ponemos left join
-- el left join fuerza a q se muestren las filas de la tabla de la izquierda
-- si le pones count(*) en vez de count(empno) cuanta filas en vez de los valores de empno, por lo tanto en OPERATIONS saldria 1
-- 
-- +------------+--------------+
-- | dname      | COUNT(empno) |
-- +------------+--------------+
-- | ACCOUNTING |            3 |
-- | RESEARCH   |            5 |
-- | SALES      |            6 |
-- | OPERATIONS |            0 |
-- +------------+--------------+


-- 2. Para cada empleado mostrar las horas que trabajó en proyectos, si no trabajó en
-- ninguno, indicarlo con un 0.
SELECT ename, COALESCE(SUM(hours), 0)
FROM Emp e LEFT JOIN Emppro ep ON e.empno = ep.empno
GROUP BY e.empno, ename;
-- wtf porque ahora si hace falta coalesce, sino sale null
-- count funciona contando filas q no sean null
-- sum funciona sumando valores, si encuentra un null devuelve null, la suma de elementos nulos es null
-- +--------+-------------------------+
-- | ename  | COALESCE(SUM(hours), 0) |
-- +--------+-------------------------+
-- | SMITH  |                       0 |
-- | ALLEN  |                      27 |
-- | WARD   |                      18 |
-- | JONES  |                       0 |
-- | MARTIN |                      36 |
-- | BLAKE  |                       0 |
-- | CLARK  |                       0 |
-- | SCOTT  |                       0 |
-- | KING   |                       0 |
-- | TURNER |                       6 |
-- | ADAMS  |                       0 |
-- | JAMES  |                       0 |
-- | FORD   |                       0 |
-- | MILLER |                       4 |
-- +--------+-------------------------+

-- 3. Para cada empleado muestra su nombre, el nombre de su jefe, y el departamento
-- para el que trabaja su jefe. Si el empleado no tiene jefe, debe aparecer con nulos
-- en los datos del jefe.
SELECT e.ename, j.ename AS jname, j.deptno
FROM Emp e LEFT JOIN Emp j ON e.mgr = j.empno
GROUP BY e.empno, e.ename, j.ename, j.deptno;
-- +--------+-------+--------+
-- | ename  | jname | deptno |
-- +--------+-------+--------+
-- | SMITH  | FORD  |     20 |
-- | ALLEN  | BLAKE |     30 |
-- | WARD   | BLAKE |     30 |
-- | JONES  | KING  |     10 |
-- | MARTIN | BLAKE |     30 |
-- | BLAKE  | KING  |     10 |
-- | CLARK  | KING  |     10 |
-- | SCOTT  | JONES |     20 |
-- | KING   | NULL  |   NULL |
-- | TURNER | BLAKE |     30 |
-- | ADAMS  | SCOTT |     20 |
-- | JAMES  | BLAKE |     30 |
-- | FORD   | JONES |     20 |
-- | MILLER | CLARK |     10 |
-- +--------+-------+--------+
-- como el fockin enunciado está mal redactado, lo vuelvo a hacer pero mostrando el nombre del departamento donde trabaja el jefe
-- no hace falta group by porque no estamos haciendo operaciones colectivas
--
SELECT e.ename, j.ename AS jname, d.dname
FROM Emp e LEFT JOIN Emp j ON e.mgr = j.empno
    LEFT JOIN Dept d ON j.deptno = d.deptno;
-- +--------+-------+------------+
-- | ename  | jname | dname      |
-- +--------+-------+------------+
-- | SMITH  | FORD  | RESEARCH   |
-- | ALLEN  | BLAKE | SALES      |
-- | WARD   | BLAKE | SALES      |
-- | JONES  | KING  | ACCOUNTING |
-- | MARTIN | BLAKE | SALES      |
-- | BLAKE  | KING  | ACCOUNTING |
-- | CLARK  | KING  | ACCOUNTING |
-- | SCOTT  | JONES | RESEARCH   |
-- | KING   | NULL  | NULL       |
-- | TURNER | BLAKE | SALES      |
-- | ADAMS  | SCOTT | RESEARCH   |
-- | JAMES  | BLAKE | SALES      |
-- | FORD   | JONES | RESEARCH   |
-- | MILLER | CLARK | ACCOUNTING |
-- +--------+-------+------------+

-- 4. Para cada empleado muestra en cuántas ciudades distintas ha trabajado. Si no ha
-- trabajado en ninguna, debe mostrar un cero. Muestra el nombre del empleado.
SELECT ename, count(DISTINCT loc)
FROM Emp e LEFT JOIN Emppro ep ON e.empno = ep.empno
    LEFT JOIN Pro p ON ep.prono = p.prono
GROUP BY e.empno, ename;
-- +--------+---------------------+
-- | ename  | count(DISTINCT loc) |
-- +--------+---------------------+
-- | SMITH  |                   0 |
-- | ALLEN  |                   1 |
-- | WARD   |                   2 |
-- | JONES  |                   0 |
-- | MARTIN |                   3 |
-- | BLAKE  |                   0 |
-- | CLARK  |                   0 |
-- | SCOTT  |                   0 |
-- | KING   |                   0 |
-- | TURNER |                   1 |
-- | ADAMS  |                   0 |
-- | JAMES  |                   0 |
-- | FORD   |                   0 |
-- | MILLER |                   1 |
-- +--------+---------------------+

-- 5. Considerando los proyectos controlados por los departamentos 30 y 40, muestra
-- cuántos empleados distintos han trabajado en cada departamento. Si no han
-- trabajado empleados, debe mostrar un cero. Muestra el nombre del
-- departamento.
SELECT d.deptno, dname, count(DISTINCT e.empno)
FROM Dept d LEFT JOIN Emp e ON d.deptno = e.deptno
WHERE d.deptno IN (30, 40)
GROUP BY d.deptno, d.dname;
-- esto esta mal porque he sudado de la parte de los proyectos, a parte no deberia hacer un WHERE, sino un HAVING porque el deptno esta en el el GROUP BY
-- bueno sale lo mismo xd
SELECT d.dname, count(DISTINCT ep.empno)
FROM Dept d LEFT JOIN Pro p on d.deptno = p.deptno
    LEFT JOIN Emppro ep on ep.prono = p.prono
GROUP BY d.deptno, d.dname
HAVING d.deptno = 30 or d.deptno = 40
-- +------------+--------------------------+
-- | dname      | count(DISTINCT ep.empno) |
-- +------------+--------------------------+
-- | SALES      |                        4 |
-- | OPERATIONS |                        0 |
-- +------------+--------------------------+
