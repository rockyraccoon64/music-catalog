DROP DATABASE SummerProject;
CREATE DATABASE SummerProject;
USE SummerProject;

CREATE TABLE Bands (
	ID INT NOT NULL AUTO_INCREMENT,
    Name NVARCHAR(50) NOT NULL,
    YearOfFormation YEAR NOT NULL,
    YearOfDisbanding YEAR,
    Photo BLOB,
    PRIMARY KEY (ID)
);

CREATE TABLE Musicians (
	ID INT NOT NULL AUTO_INCREMENT,
    Name NVARCHAR(50) NOT NULL,
    DateOfBirth DATE,
    DateOfDeath DATE,
    Band INT NOT NULL REFERENCES Bands (ID),
    PRIMARY KEY (ID)
);

CREATE TABLE Instruments (
	ID INT NOT NULL AUTO_INCREMENT,
    Name NVARCHAR(30) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE Albums (
	ID INT NOT NULL AUTO_INCREMENT,
    Band INT NOT NULL REFERENCES Bands (ID),
    Name NVARCHAR(50) NOT NULL,
    ReleaseDate DATE,
    CoverImage BLOB,
    PRIMARY KEY (ID)
);

CREATE TABLE Genres (
	ID INT NOT NULL AUTO_INCREMENT,
	Name NVARCHAR(30) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE Songs (
	ID INT NOT NULL AUTO_INCREMENT,
    Band INT NOT NULL REFERENCES Bands (ID),
    Name NVARCHAR(100) NOT NULL,
    Genre INT REFERENCES Genres (ID),
    Album INT REFERENCES Albums (ID),
    PRIMARY KEY (ID)
);

CREATE TABLE MusicianInstrument (
	MusicianID INT NOT NULL REFERENCES Musicians (ID),
    InstrumentID INT NOT NULL REFERENCES Instruments (ID),
    PRIMARY KEY (MusicianID, InstrumentID)
);

INSERT INTO Bands (Name, YearOfFormation, YearOfDisbanding) VALUES
(N'The Beatles', 1960, 1970),
(N'The Byrds', 1964, 1973),
(N'The Band', 1968, 1999),
(N'Creedence Clearwater Revival', 1967, 1972),
(N'The Kinks', 1964, NULL);

INSERT INTO Musicians (Name, Band, DateOfBirth, DateOfDeath) VALUES
(N'John Lennon', 1, '1940-10-09', '1980-12-08'),
(N'Paul McCartney', 1, '1942-06-18', NULL),
(N'George Harrison', 1, '1943-02-25', '2001-11-29'),
(N'Ringo Starr', 1, '1940-07-07', NULL),
(N'Roger McGuinn', 2, '1942-07-13', NULL),

(N'Gene Clark', 2, '1944-11-17', '1991-05-24'),
(N'David Crosby', 2, '1941-08-14', NULL),
(N'Michael Clarke', 2, '1946-06-03', '1993-12-19'),
(N'Chris Hillman', 2, '1944-12-04', NULL),

(N'Rick Danko', 3, '1943-12-29', '1999-12-10'),
(N'Levon Helm', 3, '1940-05-26', '2012-04-19'),
(N'Garth Hudson', 3, '1937-08-02', NULL),
(N'Richard Manuel', 3, '1943-04-03', '1986-03-04'),
(N'Robbie Robertson', 3, '1943-07-05', NULL),

(N'John Fogerty', 4, '1945-05-28', NULL),
(N'Tom Fogerty', 4, '1941-11-09', '1990-09-06'),
(N'Stu Cook', 4, '1945-04-25', NULL),
(N'Doug Clifford', 4, '1945-04-24', NULL),

(N'Ray Davies', 5, '1944-06-21', NULL),
(N'Dave Davies', 5, '1947-02-03', NULL),
(N'Mick Avory', 5, '1944-02-15', NULL),
(N'Pete Quaife', 5, '1943-12-31', '2010-06-23');

INSERT INTO Instruments (Name) VALUES
(N'Гитара'),
(N'Клавишные'),
(N'Ударные'),
(N'Бас-гитара'),
(N'Вокал'),
(N'Бэк-вокал'),
(N'Губная гармоника'),
(N'Саксофон'),
(N'Контрабас'),
(N'Аккордеон'),
(N'Банджо'),
(N'Ситар');

INSERT INTO Albums (Band, Name, ReleaseDate) VALUES
(1, N'Please Please Me', '1963-03-22'),
(1, N'With the Beatles', '1963-11-22'),
(1, N'A Hard Day''s Night', '1964-07-10'),
(1, N'Beatles for Sale', '1964-12-04'),
(1, N'Help!', '1965-08-06'),
(1, N'Rubber Soul', '1965-12-03'),
(1, N'Revolver', '1966-08-05'),
(1, N'Sgt. Pepper''s Lonely Hearts Club Band', '1967-05-26'),
(1, N'Magical Mystery Tour', '1967-11-27'),
(1, N'The Beatles', '1968-11-22'),
(1, N'Yellow Submarine', '1969-01-13'),
(1, N'Abbey Road', '1969-09-26'),
(1, N'Let It Be', '1970-05-08');