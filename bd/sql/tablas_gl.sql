-- Drop tables if they exist
DROP TABLE IF EXISTS pertenece;
DROP TABLE IF EXISTS men_foro;
DROP TABLE IF EXISTS tema;
DROP TABLE IF EXISTS rexistrase;
DROP TABLE IF EXISTS alumno;
DROP TABLE IF EXISTS edicion;
DROP TABLE IF EXISTS capacitado;
DROP TABLE IF EXISTS curso;
DROP TABLE IF EXISTS profesor;

-- Create tables
CREATE TABLE profesor(
      nss BIGINT(8) PRIMARY KEY,
      nome VARCHAR(12) NOT NULL,
      enderezo VARCHAR(20) NOT NULL,
      email VARCHAR(25),
      telefono VARCHAR(9),
      organizacion VARCHAR(9),
      data_alta DATE
);
      
CREATE TABLE curso(
      cod_c INT(2) PRIMARY KEY,
      nome VARCHAR(12) NOT NULL,
      descricion VARCHAR(20),
      horas INT(3),
      requisito INT(2),
      FOREIGN KEY (requisito) REFERENCES curso(cod_c)
);
      
CREATE TABLE capacitado(
      cod_c INT(2),
      nss BIGINT(8),
      PRIMARY KEY (cod_c, nss),
      FOREIGN KEY (cod_c) REFERENCES curso(cod_c),
      FOREIGN KEY (nss) REFERENCES profesor(nss)
);
      
CREATE TABLE edicion(
      cod_c INT(2),
      numero INT(3),
      prezo INT(5),
      data_comezo DATE,
      data_fin DATE,
      nss BIGINT(8),
      PRIMARY KEY (cod_c, numero),
      FOREIGN KEY (cod_c) REFERENCES curso(cod_c),
      FOREIGN KEY (nss) REFERENCES profesor(nss)
);
      
CREATE TABLE alumno(
      email VARCHAR(25) PRIMARY KEY,
      nome VARCHAR(12) NOT NULL,
      enderezo VARCHAR(20) NOT NULL,
      telefono VARCHAR(9),
      organizacion VARCHAR(9)
);
      
CREATE TABLE rexistrase(
      email VARCHAR(25),
      cod_c INT(2),
      numero INT(3),
      data DATE,
      metpago VARCHAR(13),
      PRIMARY KEY (email, cod_c, numero),
      FOREIGN KEY (cod_c, numero) REFERENCES edicion(cod_c, numero),
      FOREIGN KEY (email) REFERENCES alumno(email)
);
      
CREATE TABLE tema(
      id_tema INT(2) PRIMARY KEY,
      asunto VARCHAR(15) NOT NULL,
      descricion VARCHAR(30),
      data DATE,
      cod_c INT(2),
      numero INT(3),
      FOREIGN KEY (cod_c, numero) REFERENCES edicion(cod_c, numero)
);
      
CREATE TABLE men_foro(                        
      id_men INT(5) PRIMARY KEY,
      asunto VARCHAR(15) NOT NULL,
      corpo VARCHAR(25),
      data DATE,
      email VARCHAR(25),
      nss BIGINT(8),
      resposta_de INT(5),
      FOREIGN KEY (email) REFERENCES alumno(email),
      FOREIGN KEY (nss) REFERENCES profesor(nss),
      FOREIGN KEY (resposta_de) REFERENCES men_foro(id_men)
);
      
CREATE TABLE pertenece(
      id_men INT(5),
      id_tema INT(3),
      PRIMARY KEY (id_men, id_tema),
      FOREIGN KEY (id_men) REFERENCES men_foro(id_men),
      FOREIGN KEY (id_tema) REFERENCES tema(id_tema)
);

-- Insert data into tables
INSERT INTO profesor VALUES(11111111, 'Bacterio', 'Cuartel xeral TIA', 'bacterio@tia.ole', '686686686', 'T.I.A', '2005-03-12');
INSERT INTO profesor VALUES(22222222, 'Mona', 'TBEO', 'mona@tbeo.ole', '699699699', 'M.TRIUNFO', '2006-05-23');
INSERT INTO profesor VALUES(33333333, 'Vacarotti', 'TBEO', 'vacarotti@tbeo.ole', '655655655', 'M.TRIUNFO', '2008-07-01');
INSERT INTO profesor VALUES(44444444, 'Bacilez', 'Rebolling street 1', 'bacilez@abuela.ole', NULL, 'ABUELA', '2007-06-03');
INSERT INTO profesor VALUES(55555555, 'Bestiajez', 'Cuartel ABUELA', 'bestiajez@abuela.ole', NULL, 'ABUELA', '2005-06-03');

INSERT INTO curso VALUES (1, 'Camuflaxe', 'Técnicas camuflaxe', 50, NULL);
INSERT INTO curso VALUES (2, 'Tortas', 'Técnicas Tortas', 80, 1);
INSERT INTO curso VALUES (3, 'Pócimas', 'Pócimas varias', 70, 1);
INSERT INTO curso VALUES (4, 'Detecta Malo', 'Tec detecta malos', 40, 2);
INSERT INTO curso VALUES (5, 'Escaquearse', 'Técnicas  escaqueo', 90, 2);
INSERT INTO curso VALUES (6, 'T.N.T', 'Técnicas  T.N.T', 100, 4);

INSERT INTO capacitado VALUES(3, 11111111);
INSERT INTO capacitado VALUES(1, 22222222);
INSERT INTO capacitado VALUES(2, 22222222);
INSERT INTO capacitado VALUES(3, 22222222);
INSERT INTO capacitado VALUES(4, 22222222);
INSERT INTO capacitado VALUES(5, 22222222);
INSERT INTO capacitado VALUES(1, 33333333);
INSERT INTO capacitado VALUES(2, 33333333);
INSERT INTO capacitado VALUES(3, 33333333);
INSERT INTO capacitado VALUES(4, 33333333);
INSERT INTO capacitado VALUES(5, 33333333);
INSERT INTO capacitado VALUES(4, 44444444);
INSERT INTO capacitado VALUES(6, 22222222);

INSERT INTO edicion VALUES(1, 1, 300, '2011-01-01', '2011-04-01', 22222222);
INSERT INTO edicion VALUES(1, 2, 300, '2011-04-02', '2011-07-01', 33333333);
INSERT INTO edicion VALUES(1, 3, 350, '2011-07-02', '2011-10-01', 22222222);
INSERT INTO edicion VALUES(2, 1, 150, '2011-01-01', '2011-02-01', 22222222);
INSERT INTO edicion VALUES(2, 2, 150, '2011-02-02', '2011-04-01', 22222222);
INSERT INTO edicion VALUES(2, 3, 150, '2011-02-01', '2011-04-01', 22222222);
INSERT INTO edicion VALUES(3, 1, 250, '2011-02-02', '2011-04-01', 22222222);	
INSERT INTO edicion VALUES(3, 2, 300, '2011-12-01', '2012-02-01', 11111111);
INSERT INTO edicion VALUES(3, 3, 300, '2012-02-02', '2012-04-01', 11111111);	
INSERT INTO edicion VALUES(4, 1, 500, '2012-02-02', '2012-04-01', 33333333);	
INSERT INTO edicion VALUES(4, 2, 500, '2012-04-02', '2012-06-01', 22222222);	
INSERT INTO edicion VALUES(4, 3, 600, '2012-04-02', '2012-06-01', 22222222);	

INSERT INTO alumno VALUES('mortadelo@tia.ole', 'Mortadelo', 'Pensión el Calvario', '654654654', 'T.I.A');
INSERT INTO alumno VALUES('filemon@tia.ole', 'Filemon', 'Pensión el Calvario', '622654654', 'T.I.A');
INSERT INTO alumno VALUES('elsuper@tia.ole', 'El Super', 'Cuartel xeral TIA', NULL, 'T.I.A');
INSERT INTO alumno VALUES('ofelia@tia.ole', 'Ofelia', 'Cuartel xeral TIA', NULL, 'T.I.A');
INSERT INTO alumno VALUES('irma@tia.ole', 'Irma', 'Cuartel xeral TIA', NULL, 'T.I.A');
INSERT INTO alumno VALUES('bichez@abuela.ole', 'Bichez', 'Cuartel ABUELA', NULL, 'ABUELA');
INSERT INTO alumno VALUES('ladrillez@abuela.ole', 'Ladrillez', 'Cuartel ABUELA', NULL, 'ABUELA');

INSERT INTO rexistrase VALUES('mortadelo@tia.ole', 1, 1, '2010-12-28', 'tarxeta');
INSERT INTO rexistrase VALUES('mortadelo@tia.ole', 2, 2, '2011-01-01', 'tarxeta');
INSERT INTO rexistrase VALUES('mortadelo@tia.ole', 3, 1, '2011-01-01', 'tarxeta');
INSERT INTO rexistrase VALUES('mortadelo@tia.ole', 4, 2, '2012-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('filemon@tia.ole', 1, 2, '2011-03-01', 'tarxeta');
INSERT INTO rexistrase VALUES('filemon@tia.ole', 3, 2, '2011-10-01', 'transferencia');
INSERT INTO rexistrase VALUES('filemon@tia.ole', 4, 3, '2012-03-01', 'tarxeta');
INSERT INTO rexistrase VALUES('elsuper@tia.ole', 1, 1, '2010-12-29', 'tarxeta');
INSERT INTO rexistrase VALUES('elsuper@tia.ole', 1, 2, '2011-03-01', 'tarxeta');
INSERT INTO rexistrase VALUES('elsuper@tia.ole', 1, 3, '2011-06-01', 'tarxeta');
INSERT INTO rexistrase VALUES('elsuper@tia.ole', 2, 2, '2010-12-29', 'tarxeta');
INSERT INTO rexistrase VALUES('ofelia@tia.ole', 2, 1, '2010-12-29', 'tarxeta');
INSERT INTO rexistrase VALUES('ofelia@tia.ole', 2, 2, '2010-12-29', 'tarxeta');
INSERT INTO rexistrase VALUES('ofelia@tia.ole', 3, 1, '2011-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('ofelia@tia.ole', 4, 1, '2012-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('ofelia@tia.ole', 4, 2, '2012-03-01', 'transferencia');
INSERT INTO rexistrase VALUES('ofelia@tia.ole', 4, 3, '2012-03-01', 'transferencia');
INSERT INTO rexistrase VALUES('irma@tia.ole', 1, 1, '2011-01-01', 'tarxeta');
INSERT INTO rexistrase VALUES('irma@tia.ole', 3, 2, '2011-10-01', 'transferencia');
INSERT INTO rexistrase VALUES('irma@tia.ole', 3, 3, '2012-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('bichez@abuela.ole', 1, 1, '2011-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('bichez@abuela.ole', 3, 1, '2011-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('bichez@abuela.ole', 3, 3, '2012-01-01', 'tarxeta');
INSERT INTO rexistrase VALUES('bichez@abuela.ole', 4, 1, '2012-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('bichez@abuela.ole', 4, 2, '2012-03-01', 'transferencia');
INSERT INTO rexistrase VALUES('bichez@abuela.ole', 4, 3, '2012-03-01', 'tarxeta');
INSERT INTO rexistrase VALUES('ladrillez@abuela.ole', 2, 2, '2010-12-29', 'tarxeta');
INSERT INTO rexistrase VALUES('ladrillez@abuela.ole', 3, 1, '2011-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('ladrillez@abuela.ole', 4, 1, '2012-01-01', 'transferencia');
INSERT INTO rexistrase VALUES('ladrillez@abuela.ole', 4, 2, '2012-03-01', 'transferencia');
INSERT INTO rexistrase VALUES('ladrillez@abuela.ole', 4, 3, '2012-03-01', 'transferencia');

INSERT INTO tema VALUES(1, 'Tortas', 'Como librarse de tortas', '2011-01-02', 2, 1);
INSERT INTO tema VALUES(2, 'Bacterio', 'Convertiume en cucharacha', '2011-12-03', 3, 2);
INSERT INTO tema VALUES(3, 'Persiguen', 'Evitar ao Super', '2011-12-03', 1, 1);
INSERT INTO tema VALUES(4, 'O mellor', 'É mortadelo o mellor?', '2010-12-28', 1, 1);
INSERT INTO tema VALUES(5, 'Malo', 'Neste curso hai 1 malo', '2011-02-01', 4, 1);
INSERT INTO tema VALUES(6, 'Feo', 'Seguro que é o feo', '2011-02-03', 4, 2);

INSERT INTO men_foro VALUES(1, 'Do Super', 'Son durísimas', '2011-01-03', 'filemon@tia.ole', NULL, NULL);
INSERT INTO men_foro VALUES(2, 'De Bestiajez', 'Doen moito', '2011-01-04', 'filemon@tia.ole', NULL, NULL);
INSERT INTO men_foro VALUES(3, 'Nooon', 'Se son caricias', '2011-01-05', NULL, 44444444, 2);
INSERT INTO men_foro VALUES(4, 'Bacterio', 'As pócimas son malas', '2011-01-04', 'mortadelo@tia.ole', NULL, NULL);
INSERT INTO men_foro VALUES(5, 'Pillareite', 'Xa te pillarei', '2011-01-05', NULL, 11111111, 4);
INSERT INTO men_foro VALUES(6, 'Se me ves', 'Estou enfrente de ti', '2011-01-05', 'mortadelo@tia.ole', NULL, 5);
INSERT INTO men_foro VALUES(7, 'O Super', 'Quere buscar ladrillez', '2012-02-01', 'mortadelo@tia.ole', NULL, NULL);
INSERT INTO men_foro VALUES(8, 'E máis vale', 'Que o pilledes', '2012-02-01', 'elsuper@tia.ole', NULL, 7);
INSERT INTO men_foro VALUES(9, 'Si si', 'Xa imos', '2012-02-01', 'filemon@tia.ole', NULL, 8);
INSERT INTO men_foro VALUES(10, 'Mortadelooooo', 'Matoooooote', '2012-04-01', 'elsuper@tia.ole', NULL, NULL);
INSERT INTO men_foro VALUES(11, 'Xefe', 'Necesita unha tila', '2012-04-01', 'mortadelo@tia.ole', NULL, 10);
INSERT INTO men_foro VALUES(12, 'Busque', 'Ofelia atopa a eses 2', '2012-05-01', 'elsuper@tia.ole', NULL, NULL);
INSERT INTO men_foro VALUES(13, 'Son bos', 'Foron curso de camuflaxe', '2012-05-01', 'ofelia@tia.ole', NULL, 12);
INSERT INTO men_foro VALUES(14, 'Os mellores', 'Recibindo tortas', '2012-06-01', NULL, 22222222, NULL);
INSERT INTO men_foro VALUES(15, 'Práctica', 'Con práctica mellórase', '2012-06-01', 'mortadelo@tia.ole', NULL, 14);
INSERT INTO men_foro VALUES(16, 'Talento', 'Tedes talento natural', '2012-07-01', NULL, 22222222, 15);
INSERT INTO men_foro VALUES(17, 'So', 'Atopo os dentes', '2012-07-08', 'ofelia@tia.ole', NULL, 12);
INSERT INTO men_foro VALUES(18, 'JuaJua', 'Ven ven', '2011-01-05', NULL, 55555555, 2);

INSERT INTO pertenece VALUES(1, 1);
INSERT INTO pertenece VALUES(2, 1);
INSERT INTO pertenece VALUES(3, 1);
INSERT INTO pertenece VALUES(4, 2);
INSERT INTO pertenece VALUES(5, 2);
INSERT INTO pertenece VALUES(6, 2);
INSERT INTO pertenece VALUES(6, 4);
INSERT INTO pertenece VALUES(7, 3);
INSERT INTO pertenece VALUES(7, 1);
INSERT INTO pertenece VALUES(8, 3);
INSERT INTO pertenece VALUES(8, 1);
INSERT INTO pertenece VALUES(9, 3);
INSERT INTO pertenece VALUES(9, 1);
INSERT INTO pertenece VALUES(10, 4);
INSERT INTO pertenece VALUES(11, 4);
INSERT INTO pertenece VALUES(10, 2);
INSERT INTO pertenece VALUES(12, 3);
INSERT INTO pertenece VALUES(13, 3);
INSERT INTO pertenece VALUES(14, 1);
INSERT INTO pertenece VALUES(15, 1);
INSERT INTO pertenece VALUES(16, 1);
INSERT INTO pertenece VALUES(17, 3);
INSERT INTO pertenece VALUES(18, 1);