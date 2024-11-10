DROP TABLE IF EXISTS Inscription;
DROP TABLE IF EXISTS Course;

CREATE TABLE Course
(
    courseId     BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(200) NOT NULL,
    city         VARCHAR(200) NOT NULL,
    creationDate DATETIME     NOT NULL,
    startDate    DATETIME     NOT NULL,
    price        FLOAT        NOT NULL,
    maxSpots     INT          NOT NULL,
    vacantSpots  INT          NOT NULL,
    PRIMARY KEY (courseId)
) ENGINE = InnoDB;

CREATE TABLE Inscription
(
    inscriptionId   BIGINT       NOT NULL AUTO_INCREMENT,
    courseId        BIGINT       NOT NULL,
    inscriptionDate DATETIME     NOT NULL,
    cancelationDate DATETIME,
    userEmail       VARCHAR(200) NOT NULL,
    PRIMARY KEY (inscriptionId),
    FOREIGN KEY (courseId)
        REFERENCES Course (courseId)
        ON DELETE CASCADE
) ENGINE = InnoDB;