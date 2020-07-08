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

CREATE TABLE Genres (
	ID INT NOT NULL AUTO_INCREMENT,
	Name NVARCHAR(30) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE Albums (
	ID INT NOT NULL AUTO_INCREMENT,
    Band INT NOT NULL REFERENCES Bands (ID),
    Name NVARCHAR(100) NOT NULL,
    ReleaseDate DATE,
    Genre INT REFERENCES Genres (ID),
    CoverImage BLOB,
    PRIMARY KEY (ID)
);

CREATE TABLE Songs (
	ID INT NOT NULL AUTO_INCREMENT,
    Name NVARCHAR(100) NOT NULL,
    Album INT REFERENCES Albums (ID),
    TrackNo INT,
    PRIMARY KEY (ID)
);

CREATE TABLE MusicianInstrument (
	MusicianID INT NOT NULL REFERENCES Musicians (ID),
    InstrumentID INT NOT NULL REFERENCES Instruments (ID),
    PRIMARY KEY (MusicianID, InstrumentID)
);

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

INSERT INTO Genres (Name) VALUES
(N'Рок-н-ролл'),
(N'Фолк-рок'),
(N'Рок'),
(N'Арт-рок'),
(N'Психоделический рок'),
(N'Саундтрек'),
(N'Рутс-рок'),
(N'Кантри-рок'),
(N'Свомп-рок'),
(N'Барокко-поп'),
(N'Поп');

INSERT INTO Bands (Name, YearOfFormation, YearOfDisbanding) VALUES
(N'The Beatles', 1960, 1970),
(N'The Byrds', 1964, 1973),
(N'The Band', 1968, 1999),
(N'Creedence Clearwater Revival', 1967, 1972),
(N'The Kinks', 1964, NULL);

SELECT @beatles:=ID FROM Bands
WHERE Name = N'The Beatles';
SELECT @byrds:=ID FROM Bands
WHERE Name = N'The Byrds';
SELECT @band:=ID FROM Bands
WHERE Name = N'The Band';
SELECT @ccr:=ID FROM Bands
WHERE Name = N'Creedence Clearwater Revival';
SELECT @kinks:=ID FROM Bands
WHERE Name = N'The Kinks';

INSERT INTO Musicians (Name, Band, DateOfBirth, DateOfDeath) VALUES
(N'John Lennon', @beatles, '1940-10-09', '1980-12-08'),
(N'Paul McCartney', @beatles, '1942-06-18', NULL),
(N'George Harrison', @beatles, '1943-02-25', '2001-11-29'),
(N'Ringo Starr', @beatles, '1940-07-07', NULL),
(N'Roger McGuinn', @beatles, '1942-07-13', NULL),

(N'Gene Clark', @byrds, '1944-11-17', '1991-05-24'),
(N'David Crosby', @byrds, '1941-08-14', NULL),
(N'Michael Clarke', @byrds, '1946-06-03', '1993-12-19'),
(N'Chris Hillman', @byrds, '1944-12-04', NULL),

(N'Rick Danko', @band, '1943-12-29', '1999-12-10'),
(N'Levon Helm', @band, '1940-05-26', '2012-04-19'),
(N'Garth Hudson', @band, '1937-08-02', NULL),
(N'Richard Manuel', @band, '1943-04-03', '1986-03-04'),
(N'Robbie Robertson', @band, '1943-07-05', NULL),

(N'John Fogerty', @ccr, '1945-05-28', NULL),
(N'Tom Fogerty', @ccr, '1941-11-09', '1990-09-06'),
(N'Stu Cook', @ccr, '1945-04-25', NULL),
(N'Doug Clifford', @ccr, '1945-04-24', NULL),

(N'Ray Davies', @kinks, '1944-06-21', NULL),
(N'Dave Davies', @kinks, '1947-02-03', NULL),
(N'Mick Avory', @kinks, '1944-02-15', NULL),
(N'Pete Quaife', @kinks, '1943-12-31', '2010-06-23');

SELECT @rocknroll:=ID FROM Genres
WHERE Name = N'Рок-н-ролл';
SELECT @folkrock:=ID FROM Genres
WHERE Name = N'Фолк-рок';
SELECT @rock:=ID FROM Genres
WHERE Name = N'Рок';
SELECT @artrock:=ID FROM Genres
WHERE Name = N'Арт-рок';
SELECT @psych:=ID FROM Genres
WHERE Name = N'Психоделический рок';
SELECT @soundtrack:=ID FROM Genres
WHERE Name = N'Саундтрек';
SELECT @roots:=ID FROM Genres
WHERE Name = N'Рутс-рок';
SELECT @countryrock:=ID FROM Genres
WHERE Name = N'Кантри-рок';
SELECT @swamp:=ID FROM Genres
WHERE Name = N'Свомп-рок';
SELECT @baroque:=ID FROM Genres
WHERE Name = N'Барокко-поп';
SELECT @pop:=ID FROM Genres
WHERE Name = N'Поп';

INSERT INTO Albums (Band, Name, ReleaseDate, Genre) VALUES
(@beatles, N'Please Please Me', '1963-03-22', @rocknroll),
(@beatles, N'With the Beatles', '1963-11-22', @rocknroll),
(@beatles, N'A Hard Day''s Night', '1964-07-10', @rock),
(@beatles, N'Beatles for Sale', '1964-12-04', @folkrock),
(@beatles, N'Help!', '1965-08-06', @folkrock),
(@beatles, N'Rubber Soul', '1965-12-03', @folkrock),
(@beatles, N'Revolver', '1966-08-05', @psych),
(@beatles, N'Sgt. Pepper''s Lonely Hearts Club Band', '1967-05-26', @artrock),
(@beatles, N'Magical Mystery Tour', '1967-11-27', @psych),
(@beatles, N'The Beatles', '1968-11-22', @rock),
(@beatles, N'Yellow Submarine', '1969-01-13', @soundtrack),
(@beatles, N'Abbey Road', '1969-09-26', @rock),
(@beatles, N'Let It Be', '1970-05-08', @rock),

(@byrds, N'Mr. Tambourine Man', '1965-06-21', @folkrock),
(@byrds, N'Turn! Turn! Turn!', '1965-12-06', @folkrock),
(@byrds, N'Fifth Dimension', '1966-07-18', @psych),
(@byrds, N'Younger Than Yesterday', '1967-02-06', @folkrock),
(@byrds, N'The Notorious Byrd Brothers', '1968-01-15', @psych),
(@byrds, N'Sweetheart of the Rodeo', '1968-07-30', @countryrock),
(@byrds, N'Dr. Byrds & Mr. Hyde', '1969-03-05', @countryrock),
(@byrds, N'Ballad of Easy Rider', '1969-11-10', @countryrock),
(@byrds, N'(Untitled)', '1970-09-14', @countryrock),
(@byrds, N'Byrdmaniax', '1971-06-23', @countryrock),
(@byrds, N'Farther Along', '1971-11-17', @countryrock),
(@byrds, N'Byrds', '1973-03-07', @rock),

(@band, N'Music from Big Pink', '1968-07-01', @roots),
(@band, N'The Band', '1969-09-22', @roots),
(@band, N'Stage Fright', '1970-08-17', @roots),
(@band, N'Cahoots', '1971-09-15', @roots),
(@band, N'Moondog Matinee', '1973-10-15', @rocknroll),
(@band, N'Northern Lights - Southern Cross', '1975-11-01', @roots),
(@band, N'Islands', '1977-03-15', @rock),

(@ccr, N'Creedence Clearwater Revival', '1968-05-28', @psych),
(@ccr, N'Bayou Country', '1969-01-05', @swamp),
(@ccr, N'Green River', '1969-08-03', @swamp),
(@ccr, N'Willy and the Poor Boys', '1969-11-02', @roots),
(@ccr, N'Cosmo''s Factory', '1970-07-16', @roots),
(@ccr, N'Pendulum', '1970-12-09', @roots),
(@ccr, N'Mardi Gras', '1972-04-11', @countryrock),

(@kinks, N'Face to Face', '1966-10-28', @baroque),
(@kinks, N'Something Else by the Kinks', '1967-09-15', @baroque),
(@kinks, N'The Kinks Are the Village Green Preservation Society', '1968-11-22', @baroque),
(@kinks, N'Arthur (Or the Decline and Fall of the British Empire', '1969-10-10', @rock),
(@kinks, N'Lola Versus Powerman and the Moneygoround, Part One', '1970-11-27', @rock),
(@kinks, N'Muswell Hillbillies', '1971-11-24', @countryrock);

SELECT @beatles_please:=ID FROM Albums
WHERE Name = N'Please Please Me';
SELECT @beatles_with:=ID FROM Albums
WHERE Name = N'With the Beatles';
SELECT @beatles_hard:=ID FROM Albums
WHERE Name = N'A Hard Day''s Night';
SELECT @beatles_sale:=ID FROM Albums
WHERE Name = N'Beatles For Sale';

INSERT INTO Songs (Album, Name, TrackNo) VALUES
(@beatles_please, N'I Saw Her Standing There', 1),
(@beatles_please, N'Misery', 2),
(@beatles_please, N'Anna (Go to Him', 3),
(@beatles_please, N'Chains', 4),
(@beatles_please, N'Boys', 5),
(@beatles_please, N'Ask Me Why', 6),
(@beatles_please, N'Please Please Me', 7),
(@beatles_please, N'Love Me Do', 8),
(@beatles_please, N'P.S. I Love You', 9),
(@beatles_please, N'Baby It''s You', 10),
(@beatles_please, N'Do You Want to Know a Secret', 11),
(@beatles_please, N'A Taste of Honey', 12),
(@beatles_please, N'There''s a Place', 13),
(@beatles_please, N'Twist and Shout', 14),

(@beatles_with, N'It Won''t Be Long', 1),
(@beatles_with, N'All I''ve Got to Do', 2),
(@beatles_with, N'All My Loving', 3),
(@beatles_with, N'Don''t Bother Me', 4),
(@beatles_with, N'Little Child', 5),
(@beatles_with, N'Till There Was You', 6),
(@beatles_with, N'Please Mr. Postman', 7),
(@beatles_with, N'Roll Over Beethoven', 8),
(@beatles_with, N'Hold Me Tight', 9),
(@beatles_with, N'You Really Got a Hold on Me', 10),
(@beatles_with, N'I Wanna Be Your Man', 11),
(@beatles_with, N'Devil in Her Heart', 12),
(@beatles_with, N'Not a Second Time', 13),
(@beatles_with, N'Money (That''s What I Want', 14),

(@beatles_hard, N'A Hard Day''s Night', 1),
(@beatles_hard, N'I Should Have Known Better', 2),
(@beatles_hard, N'If I Fell', 3),
(@beatles_hard, N'I''m Happy Just to Dance With You', 4),
(@beatles_hard, N'And I Love Her', 5),
(@beatles_hard, N'Tell Me Why', 6),
(@beatles_hard, N'Can''t Buy Me Love', 7),
(@beatles_hard, N'Any Time At All', 8),
(@beatles_hard, N'I''ll Cry Instead', 9),
(@beatles_hard, N'Things We Said Today', 10),
(@beatles_hard, N'When I Get Home', 11),
(@beatles_hard, N'You Can''t Do That', 12),
(@beatles_hard, N'I''ll Be Back', 13),

(@beatles_sale, N'No Reply', 1),
(@beatles_sale, N'I''m a Loser', 2),
(@beatles_sale, N'Baby''s in Black', 3),
(@beatles_sale, N'Rock and Roll Music', 4),
(@beatles_sale, N'I''ll Follow the Sun', 5),
(@beatles_sale, N'Mr. Moonlight', 6),
(@beatles_sale, N'Kansas City/Hey, Hey, Hey, Hey', 7),
(@beatles_sale, N'Eight Days a Week', 8),
(@beatles_sale, N'Words of Love', 9),
(@beatles_sale, N'Honey Don''t', 10),
(@beatles_sale, N'Every Little Thing', 11),
(@beatles_sale, N'I Don''t Want to Spoil the Party', 12),
(@beatles_sale, N'What You''re Doing', 13),
(@beatles_sale, N'Everybody''s Trying to Be My Baby', 14);