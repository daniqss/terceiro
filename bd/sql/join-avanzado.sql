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
-- para poder hacer q aparezcan todos los departamentos pero siguiendo con la condicion de 30 y 40, pasamos la condicion al ON
SELECT d.dname, count(DISTINCT ep.empno)
FROM Dept d LEFT JOIN Pro p on d.deptno = p.deptno AND d.deptno IN (30, 40)
    LEFT JOIN Emppro ep on ep.prono = p.prono
GROUP BY d.deptno, d.dname;
-- +------------+--------------------------+
-- | dname      | count(DISTINCT ep.empno) |
-- +------------+--------------------------+
-- | ACCOUNTING |                        0 |
-- | RESEARCH   |                        0 |
-- | SALES      |                        4 |
-- | OPERATIONS |                        0 |
-- +------------+--------------------------+


-- 1. Muestra cuántos empleados subordinados tiene cada jefe, que fueran contratados
-- el mismo año. Si no tiene ninguno debe mostrar un cero.
SELECT j.ename, count(e.empno)
FROM Emp e RIGHT JOIN Emp j ON j.empno = e.mgr
    AND to_char(e.hiredate, "YYYY") = to_char(j.hiredate, "YYYY")
WHERE j.empno IN (SELECT mgr FROM Emp) 
GROUP BY j.empno, j.ename
ORDER BY 1;
-- +-------+----------------+
-- | ename | count(e.empno) |
-- +-------+----------------+
-- | BLAKE |              5 |
-- | CLARK |              0 |
-- | FORD  |              0 |
-- | JONES |              1 |
-- | KING  |              3 |
-- | SCOTT |              1 |
-- +-------+----------------+


-- 2. Muestra los empleados que han trabajado en proyectos ubicados en la misma
-- localidad donde está su departamento.
SELECT DISTINCT e.ename, d.loc
FROM Emp e JOIN Dept d ON e.deptno = d.deptno
    JOIN Emppro ep ON e.empno = ep.empno
    JOIN Pro p ON ep.prono = p.prono
WHERE p.loc = d.loc;
-- MAL
-- hay q acordarse del DISTINCT
-- Lo q estaba mal es q estaba pillando Pro segun el departamente, y no segun q su numero de proyecto fuera el mismo q el de Emppro
-- +--------+
-- | ename  |
-- +--------+
-- | MARTIN |
-- | ALLEN  |
-- | WARD   |
-- | TURNER |
-- +--------+
SELECT DISTINCT ename, d.loc
FROM Pro p JOIN Emppro ep ON ep.prono = p.prono
    JOIN Emp e ON ep.empno = e.empno
    JOIN Dept d ON d.deptno = e.deptno
WHERE p.loc = d.loc
-- +--------+--------------+
-- | ename  | departamento |
-- +--------+--------------+
-- | ALLEN  | CHICAGO      |
-- | WARD   | CHICAGO      |
-- | TURNER | CHICAGO      |
-- +--------+--------------+

-- 3. Para cada empleado mostrar cuántas veces trabajó en proyectos ubicados en la
-- misma ciudad donde está su departamento.
SELECT ename, d.loc, count(e.empno) -- o cualquier cosa realmente, o asterisco
FROM Pro p JOIN Emppro ep ON ep.prono = p.prono
    JOIN Emp e ON ep.empno = e.empno
    JOIN Dept d ON d.deptno = e.deptno
WHERE p.loc = d.loc
GROUP BY e.empno, e.ename;
-- +--------+---------+----------------+
-- | ename  | loc     | count(e.empno) |
-- +--------+---------+----------------+
-- | ALLEN  | CHICAGO |              2 |
-- | WARD   | CHICAGO |              1 |
-- | TURNER | CHICAGO |              1 |
-- +--------+---------+----------------+


-- 4. Idem anterior, pero mostrando un cero cuando nunca ocurrió.
SELECT ename, d.loc, count(d.deptno)
FROM Pro p JOIN Emppro ep ON ep.prono = p.prono
    RIGHT JOIN Emp e ON ep.empno = e.empno
    LEFT JOIN Dept d ON d.deptno = e.deptno AND p.loc = d.loc
GROUP BY e.empno, e.ename;
-- +--------+---------+-----------------+
-- | ename  | loc     | count(d.deptno) |
-- +--------+---------+-----------------+
-- | SMITH  | NULL    |               0 |
-- | ALLEN  | CHICAGO |               2 |
-- | WARD   | CHICAGO |               1 |
-- | JONES  | NULL    |               0 |
-- | MARTIN | NULL    |               0 |
-- | BLAKE  | NULL    |               0 |
-- | CLARK  | NULL    |               0 |
-- | SCOTT  | NULL    |               0 |
-- | KING   | NULL    |               0 |
-- | TURNER | CHICAGO |               1 |
-- | ADAMS  | NULL    |               0 |
-- | JAMES  | NULL    |               0 |
-- | FORD   | NULL    |               0 |
-- | MILLER | NULL    |               0 |
-- +--------+---------+-----------------+