DROP DATABASE SummerProject;
CREATE DATABASE SummerProject;
USE SummerProject;

/************************************************
 *				Создание таблиц			    	*
 ***********************************************/

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
    Band INT NOT NULL,
    CHECK (DateOfBirth < DateOfDeath),
    FOREIGN KEY (Band) REFERENCES Bands (ID) ON DELETE CASCADE,
    PRIMARY KEY (ID)
);

CREATE TABLE Instruments (
	ID INT NOT NULL AUTO_INCREMENT,
    Name NVARCHAR(30) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE Genres (
	ID INT NOT NULL AUTO_INCREMENT,
	Name NVARCHAR(30) NOT NULL UNIQUE,
    PRIMARY KEY (ID)
);

CREATE TABLE Albums (
	ID INT NOT NULL AUTO_INCREMENT,
    Band INT NOT NULL,
    Name NVARCHAR(100) NOT NULL,
    ReleaseDate DATE,
    Genre INT REFERENCES Genres (ID),
    CoverImage BLOB,
    FOREIGN KEY (Band) REFERENCES Bands (ID) ON DELETE CASCADE,
    PRIMARY KEY (ID)
);

CREATE TABLE Songs (
	ID INT NOT NULL AUTO_INCREMENT,
    Name NVARCHAR(100) NOT NULL,
    Album INT NOT NULL,
    TrackNo INT NOT NULL,
    UNIQUE (Album, TrackNo),
    FOREIGN KEY (Album) REFERENCES Albums (ID) ON DELETE CASCADE,
    PRIMARY KEY (ID)
);

CREATE TABLE MusicianInstrument (
	MusicianID INT NOT NULL,
    InstrumentID INT NOT NULL,
    UNIQUE (MusicianID, InstrumentID),
    FOREIGN KEY (MusicianID) REFERENCES Musicians (ID) ON DELETE CASCADE,
    FOREIGN KEY (InstrumentID) REFERENCES Instruments (ID) ON DELETE CASCADE,
    PRIMARY KEY (MusicianID, InstrumentID)
);

/************************************************
*				Создание триггеров				*
************************************************/

delimiter //
CREATE TRIGGER MusicianDeathBeforeFormation BEFORE INSERT ON Musicians
FOR EACH ROW
BEGIN
	SELECT YearOfFormation INTO @band_form_year FROM Bands
		WHERE Bands.ID = NEW.Band;
    IF NEW.DateOfDeath < MAKEDATE(@band_form_year, 1) THEN
		SIGNAL SQLSTATE '45000' SET message_text = N'Дата смерти музыканта - до даты создания его группы';
	END IF;
END;//

CREATE TRIGGER MusicianBirthAfterDisbanding BEFORE INSERT ON Musicians
FOR EACH ROW FOLLOWS MusicianDeathBeforeFormation
BEGIN
    SELECT YearOfDisbanding INTO @disband_year FROM Bands
		WHERE Bands.ID = NEW.Band;
	IF NEW.DateOfBirth >= MAKEDATE(@disband_year + 1, 1) THEN
		SIGNAL SQLSTATE '45001' SET message_text = N'Дата рождения музыканта - после распада группы';
	END IF;
END;//

CREATE TRIGGER AlbumReleaseBeforeFormation BEFORE INSERT ON Albums
FOR EACH ROW
BEGIN
	SELECT YearOfFormation INTO @band_form_year FROM Bands
		WHERE Bands.ID = NEW.Band;
    IF NEW.ReleaseDate < MAKEDATE(@band_form_year, 1) THEN
		SIGNAL SQLSTATE '45002' SET message_text = N'Дата выпуска альбома - до даты создания группы';
	END IF;
END;//
delimiter ;

/************************************************
 *				Создание функций		    	*
 ***********************************************/
 
 delimiter //
 CREATE FUNCTION FormatAlbum (album_id INT)
 RETURNS NVARCHAR(170)
 READS SQL DATA
 BEGIN
	SELECT Name, Band, ReleaseDate INTO @album_name, @band_id, @release_date FROM Albums
		WHERE Albums.ID = album_id;
	SELECT Name INTO @band_name FROM Bands
		WHERE Bands.ID = @band_id;
	SET @result = CONCAT(@band_name, N' - ', @album_name);
	IF @release_date IS NOT NULL THEN
		RETURN CONCAT(@result, N' (', YEAR(@release_date), N')');
	ELSE RETURN @result;
    END IF;
 END;//
 delimiter ;

/************************************************
 *				Заполнение таблиц		    	*
 ***********************************************/

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
(@byrds, N'Byrds', '1973-03-07', @rock),

(@band, N'Music from Big Pink', '1968-07-01', @roots),
(@band, N'The Band', '1969-09-22', @roots),
(@band, N'Stage Fright', '1970-08-17', @roots),
(@band, N'Cahoots', '1971-09-15', @roots),
(@band, N'Northern Lights - Southern Cross', '1975-11-01', @roots),

(@ccr, N'Creedence Clearwater Revival', '1968-05-28', @psych),
(@ccr, N'Bayou Country', '1969-01-05', @swamp),
(@ccr, N'Green River', '1969-08-03', @swamp),
(@ccr, N'Willy and the Poor Boys', '1969-11-02', @roots),
(@ccr, N'Cosmo''s Factory', '1970-07-16', @roots),
(@ccr, N'Pendulum', '1970-12-09', @roots),

(@kinks, N'Face to Face', '1966-10-28', @baroque),
(@kinks, N'Something Else by the Kinks', '1967-09-15', @baroque),
(@kinks, N'The Kinks Are the Village Green Preservation Society', '1968-11-22', @baroque),
(@kinks, N'Arthur (Or the Decline and Fall of the British Empire)', '1969-10-10', @rock),
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
SELECT @beatles_help:=ID FROM Albums
WHERE Name = N'Help!';
SELECT @beatles_soul:=ID FROM Albums
WHERE Name = N'Rubber Soul';
SELECT @beatles_revolver:=ID FROM Albums
WHERE Name = N'Revolver';
SELECT @beatles_pepper:=ID FROM Albums
WHERE Name = N'Sgt. Pepper''s Lonely Hearts Club Band';
SELECT @beatles_tour:=ID FROM Albums
WHERE Name = N'Magical Mystery Tour';
SELECT @beatles_white:=ID FROM Albums
WHERE Name = N'The Beatles';
SELECT @beatles_sub:=ID FROM Albums
WHERE Name = N'Yellow Submarine';
SELECT @beatles_abbey:=ID FROM Albums
WHERE Name = N'Abbey Road';
SELECT @beatles_let:=ID FROM Albums
WHERE Name = N'Let It Be';

SELECT @byrds_tambourine:= ID FROM Albums
WHERE Name = N'Mr. Tambourine Man';
SELECT @byrds_turn:= ID FROM Albums
WHERE Name = N'Turn! Turn! Turn!';
SELECT @byrds_5d:= ID FROM Albums
WHERE Name = N'Fifth Dimension';
SELECT @byrds_younger:= ID FROM Albums
WHERE Name = N'Younger Than Yesterday';
SELECT @byrds_brothers:= ID FROM Albums
WHERE Name = N'The Notorious Byrd Brothers';
SELECT @byrds_rodeo:= ID FROM Albums
WHERE Name = N'Sweetheart of the Rodeo';
SELECT @byrds_hyde:= ID FROM Albums
WHERE Name = N'Dr. Byrds & Mr. Hyde';
SELECT @byrds_byrds:= ID FROM Albums
WHERE Name = N'Byrds';

SELECT @band_pink:= ID FROM Albums
WHERE Name = N'Music from Big Pink';
SELECT @band_band:= ID FROM Albums
WHERE Name = N'The Band';
SELECT @band_fright:= ID FROM Albums
WHERE Name = N'Stage Fright';
SELECT @band_cahoots:= ID FROM Albums
WHERE Name = N'Cahoots';
SELECT @band_lights:= ID FROM Albums
WHERE Name = N'Northern Lights - Southern Cross';

SELECT @ccr_ccr:= ID FROM Albums
WHERE Name = N'Creedence Clearwater Revival';
SELECT @ccr_bayou:= ID FROM Albums
WHERE Name = N'Bayou Country';
SELECT @ccr_river:= ID FROM Albums
WHERE Name = N'Green River';
SELECT @ccr_willy:= ID FROM Albums
WHERE Name = N'Willy and the Poor Boys';
SELECT @ccr_cosmo:= ID FROM Albums
WHERE Name = N'Cosmo''s Factory';
SELECT @ccr_pendulum:= ID FROM Albums
WHERE Name = N'Pendulum';

SELECT @kinks_face:= ID FROM Albums
WHERE Name = N'Face to Face';
SELECT @kinks_something:= ID FROM Albums
WHERE Name = N'Something Else by the Kinks';
SELECT @kinks_village:= ID FROM Albums
WHERE Name = N'The Kinks Are the Village Green Preservation Society';
SELECT @kinks_arthur:= ID FROM Albums
WHERE Name = N'Arthur (Or the Decline and Fall of the British Empire)';
SELECT @kinks_lola:= ID FROM Albums
WHERE Name = N'Lola Versus Powerman and the Moneygoround, Part One';
SELECT @kinks_muswell:= ID FROM Albums
WHERE Name = N'Muswell Hillbillies';

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
(@beatles_sale, N'Everybody''s Trying to Be My Baby', 14),

(@beatles_help, N'Help!', 1),
(@beatles_help, N'The Night Before', 2),
(@beatles_help, N'You''ve Got to Hide Your Love Away', 3),
(@beatles_help, N'I Need You', 4),
(@beatles_help, N'Another Girl', 5),
(@beatles_help, N'You''re Going to Lose That Girl', 6),
(@beatles_help, N'Ticket to Ride', 7),
(@beatles_help, N'Act Naturally', 8),
(@beatles_help, N'It''s Only Love', 9),
(@beatles_help, N'You Like Me Too Much', 10),
(@beatles_help, N'Tell Me What You See', 11),
(@beatles_help, N'I''ve Just Seen a Face', 12),
(@beatles_help, N'Yesterday', 13),
(@beatles_help, N'Dizzy Miss Lizzy', 14),

(@beatles_soul, N'Drive My Car', 1),
(@beatles_soul, N'Norwegian Wood (This Bird Has Flown', 2),
(@beatles_soul, N'You Won''t See Me', 3),
(@beatles_soul, N'Nowhere Man', 4),
(@beatles_soul, N'Think for Yourself', 5),
(@beatles_soul, N'The Word', 6),
(@beatles_soul, N'Michelle', 7),
(@beatles_soul, N'What Goes On', 8),
(@beatles_soul, N'Girl', 9),
(@beatles_soul, N'I''m Looking Through You', 10),
(@beatles_soul, N'In My Life', 11),
(@beatles_soul, N'Wait', 12),
(@beatles_soul, N'If I Needed Someone', 13),
(@beatles_soul, N'Run for Your Life', 14),

(@beatles_revolver, N'Taxman', 1),
(@beatles_revolver, N'Eleanor Rigby', 2),
(@beatles_revolver, N'I''m Only Sleeping', 3),
(@beatles_revolver, N'Love You To', 4),
(@beatles_revolver, N'Here, There and Everywhere', 5),
(@beatles_revolver, N'Yellow Submarine', 6),
(@beatles_revolver, N'She Said She Said', 7),
(@beatles_revolver, N'Good Day Sunshine', 8),
(@beatles_revolver, N'And Your Bird Can Sing', 9),
(@beatles_revolver, N'For No One', 10),
(@beatles_revolver, N'Doctor Robert', 11),
(@beatles_revolver, N'I Want to Tell You', 12),
(@beatles_revolver, N'Got to Get You into My Life', 13),
(@beatles_revolver, N'Tomorrow Never Knows', 14),

(@beatles_pepper, N'Sgt. Pepper''s Lonely Hearts Club Band', 1),
(@beatles_pepper, N'With a Little Help from My Friends', 2),
(@beatles_pepper, N'Lucy in the Sky with Diamonds', 3),
(@beatles_pepper, N'Getting Better', 4),
(@beatles_pepper, N'Fixing a Hole', 5),
(@beatles_pepper, N'She''s Leaving Home', 6),
(@beatles_pepper, N'Being for the Benefit of Mr. Kite!', 7),
(@beatles_pepper, N'Within You Without You', 8),
(@beatles_pepper, N'When I''m Sixty-Four', 9),
(@beatles_pepper, N'Lonely Rita', 10),
(@beatles_pepper, N'Good Morning Good Morning', 11),
(@beatles_pepper, N'Sgt. Pepper''s Lonely Hearts Club Band (Reprise)', 12),
(@beatles_pepper, N'A Day in the Life', 13),

(@beatles_tour, N'Magical Mystery Tour', 1),
(@beatles_tour, N'The Fool on the Hill', 2),
(@beatles_tour, N'Flying', 3),
(@beatles_tour, N'Blue Jay Way', 4),
(@beatles_tour, N'Your Mother Should Know', 5),
(@beatles_tour, N'I Am the Walrus', 6),
(@beatles_tour, N'Hello, Goodbye', 7),
(@beatles_tour, N'Strawberry Fields Forever', 8),
(@beatles_tour, N'Penny Lane', 9),
(@beatles_tour, N'Baby, You''re a Rich Man', 10),
(@beatles_tour, N'All You Need Is Love', 11),

(@beatles_white, N'Back in the U.S.S.R.', 1),
(@beatles_white, N'Dear Prudence', 2),
(@beatles_white, N'Glass Onion', 3),
(@beatles_white, N'Ob-La-Di, Ob-La-Da', 4),
(@beatles_white, N'Wild Honey Pie', 5),
(@beatles_white, N'The Continuing Story of Bungalow Bill', 6),
(@beatles_white, N'While My Guitar Gently Weeps', 7),
(@beatles_white, N'Happiness is a Warm Gun', 8),
(@beatles_white, N'Martha My Dear', 9),
(@beatles_white, N'I''m So Tired', 10),
(@beatles_white, N'Blackbird', 11),
(@beatles_white, N'Piggies', 12),
(@beatles_white, N'Rocky Raccoon', 13),
(@beatles_white, N'Don''t Pass Me By', 14),
(@beatles_white, N'Why Don''t We Do It in the Road?', 15),
(@beatles_white, N'I Will', 16),
(@beatles_white, N'Julia', 17),
(@beatles_white, N'Birthday', 18),
(@beatles_white, N'Yer Blues', 19),
(@beatles_white, N'Mother Nature''s Son', 20),
(@beatles_white, N'Everybody''s Got Something to Hide Except Me and My Monkey', 21),
(@beatles_white, N'Sexy Sadie', 22),
(@beatles_white, N'Helter Skelter', 23),
(@beatles_white, N'Long, Long, Long', 24),
(@beatles_white, N'Revolution 1', 25),
(@beatles_white, N'Honey Pie', 26),
(@beatles_white, N'Savoy Truffle', 27),
(@beatles_white, N'Cry Baby Cry', 28),
(@beatles_white, N'Revolution 9', 29),
(@beatles_white, N'Good Night', 30),

(@beatles_sub, N'Yellow Submarine', 1),
(@beatles_sub, N'Only a Northern Song', 2),
(@beatles_sub, N'All Together Now', 3),
(@beatles_sub, N'Hey Bulldog', 4),
(@beatles_sub, N'It''s All Too Much', 5),
(@beatles_sub, N'All You Need Is Love', 6),
(@beatles_sub, N'Pepperland', 7),
(@beatles_sub, N'Sea of Time', 8),
(@beatles_sub, N'Sea of Holes', 9),
(@beatles_sub, N'Sea of Monsters', 10),
(@beatles_sub, N'March of the Meanies', 11),
(@beatles_sub, N'Pepperland Laid Waste', 12),
(@beatles_sub, N'Yellow Submarine in Pepperland', 13),

(@beatles_abbey, N'Come Together', 1),
(@beatles_abbey, N'Something', 2),
(@beatles_abbey, N'Maxwell''s Silver Hammer', 3),
(@beatles_abbey, N'Oh! Darling', 4),
(@beatles_abbey, N'Octopus''s Garden', 5),
(@beatles_abbey, N'I Want You (She''s So Heavy)', 6),
(@beatles_abbey, N'Here Comes the Sun', 7),
(@beatles_abbey, N'Because', 8),
(@beatles_abbey, N'You Never Give Me Your Money', 9),
(@beatles_abbey, N'Sun King', 10),
(@beatles_abbey, N'Mean Mr. Mustard', 11),
(@beatles_abbey, N'Polythene Pam', 12),
(@beatles_abbey, N'She Came In Through the Bathroom Window', 13),
(@beatles_abbey, N'Golden Slumbers', 14),
(@beatles_abbey, N'Carry That Weight', 15),
(@beatles_abbey, N'The End', 16),
(@beatles_abbey, N'Her Majesty', 17),

(@beatles_let, N'Two of Us', 1),
(@beatles_let, N'Dig a Pony', 2),
(@beatles_let, N'Across the Universe', 3),
(@beatles_let, N'I Me Mine', 4),
(@beatles_let, N'Dig It', 5),
(@beatles_let, N'Let It Be', 6),
(@beatles_let, N'Maggie Mae', 7),
(@beatles_let, N'I''ve Got a Feeling', 8),
(@beatles_let, N'One After 909', 9),
(@beatles_let, N'The Long and Winding Road', 10),
(@beatles_let, N'For You Blue', 11),
(@beatles_let, N'Get Back', 12),

(@byrds_tambourine, N'Mr. Tambourine Man', 1),
(@byrds_tambourine, N'I''ll Feel a Whole Lot Better', 2),
(@byrds_tambourine, N'Spanish Harlem Incident', 3),
(@byrds_tambourine, N'You Won''t Have to Cry', 4),
(@byrds_tambourine, N'Here Without You', 5),
(@byrds_tambourine, N'The Bells of Rhymney', 6),
(@byrds_tambourine, N'All I Really Want to Do', 7),
(@byrds_tambourine, N'I Knew I''d Want You', 8),
(@byrds_tambourine, N'It''s No Use', 9),
(@byrds_tambourine, N'Don''t Doubt Yourself, Babe', 10),
(@byrds_tambourine, N'Chimes of Freedom', 11),
(@byrds_tambourine, N'We''ll Meet Again', 12),

(@byrds_turn, N'Turn! Turn! Turn! (To Everything There is a Season)', 1),
(@byrds_turn, N'It Won''t Be Wrong', 2),
(@byrds_turn, N'Set You Free This Time', 3),
(@byrds_turn, N'Lay Down Your Weary Tune', 4),
(@byrds_turn, N'He Was a Friend of Mine', 5),
(@byrds_turn, N'The World Turns All Around Her', 6),
(@byrds_turn, N'Satisfied Mind', 7),
(@byrds_turn, N'If You''re Gone', 8),
(@byrds_turn, N'The Times They Are a-Changin''', 9),
(@byrds_turn, N'Wait and See', 10),
(@byrds_turn, N'Oh! Susannah', 11),

(@byrds_5d, N'5D (Fifth Dimension', 1),
(@byrds_5d, N'Wild Mountain Thyme', 2),
(@byrds_5d, N'Mr. Spaceman', 3),
(@byrds_5d, N'I See You', 4),
(@byrds_5d, N'What''s Happening?!?!', 5),
(@byrds_5d, N'I Come and Stand at Every Door', 6),
(@byrds_5d, N'Eight Miles High', 7),
(@byrds_5d, N'Hey Joe (Where You Gonna Go)', 8),
(@byrds_5d, N'Captain Soul', 9),
(@byrds_5d, N'John Riley', 10),
(@byrds_5d, N'2-4-2 Fox Trot (The Lear Jet Song)', 11),

(@byrds_younger, N'So You Want to Be a Rock''n''Roll Star', 1),
(@byrds_younger, N'Have You Seen Her Face', 2),
(@byrds_younger, N'C.T.A.-102', 3),
(@byrds_younger, N'Renaissance Fair', 4),
(@byrds_younger, N'Time Between', 5),
(@byrds_younger, N'Everybody''s Been Burned', 6),
(@byrds_younger, N'Thoughts and Words', 7),
(@byrds_younger, N'Mind Gardens', 8),
(@byrds_younger, N'My Back Pages', 9),
(@byrds_younger, N'The Girl with No Name', 10),
(@byrds_younger, N'Why', 11),

(@byrds_brothers, N'Artificial Energy', 1),
(@byrds_brothers, N'Goin'' Back', 2),
(@byrds_brothers, N'Natural Harmony', 3),
(@byrds_brothers, N'Draft Morning', 4),
(@byrds_brothers, N'Wasn''t Born to Follow', 5),
(@byrds_brothers, N'Get to You', 6),
(@byrds_brothers, N'Change Is Now', 7),
(@byrds_brothers, N'Old John Robertson', 8),
(@byrds_brothers, N'Tribal Gathering', 9),
(@byrds_brothers, N'Dolphin''s Smile', 10),
(@byrds_brothers, N'Space Odyssey', 11),

(@byrds_rodeo, N'You Ain''t Goin'' Nowhere', 1),
(@byrds_rodeo, N'I Am a Pilgrim', 2),
(@byrds_rodeo, N'The Christian Life', 3),
(@byrds_rodeo, N'You Don''t Miss Your Water', 4),
(@byrds_rodeo, N'You''re Still on My Mind', 5),
(@byrds_rodeo, N'Pretty Boy Floyd', 6),
(@byrds_rodeo, N'Hickory Wind', 7),
(@byrds_rodeo, N'One Hundred Years from Now', 8),
(@byrds_rodeo, N'Blue Canadian Rockies', 9),
(@byrds_rodeo, N'Life in Prison', 10),
(@byrds_rodeo, N'Nothing Was Delivered', 11),

(@byrds_hyde, N'This Wheel''s on Fire', 1),
(@byrds_hyde, N'Old Blue', 2),
(@byrds_hyde, N'Your Gentle Way of Loving Me', 3),
(@byrds_hyde, N'Child of the Universe', 4),
(@byrds_hyde, N'Nashville West', 5),
(@byrds_hyde, N'Drug Store Truck Drivin'' Man', 6),
(@byrds_hyde, N'King Apathy III', 7),
(@byrds_hyde, N'Candy', 8),
(@byrds_hyde, N'Bad Night at the Whiskey', 9),
(@byrds_hyde, N'Medley: My Back Pages/B.J. Blues/Baby What You Want Me to Do', 10),

(@byrds_byrds, N'Full Circle', 1),
(@byrds_byrds, N'Sweet Mary', 2),
(@byrds_byrds, N'Changing Heart', 3),
(@byrds_byrds, N'For Free', 4),
(@byrds_byrds, N'Born to Rock ''n'' Roll', 5),
(@byrds_byrds, N'Things Will Be Better', 6),
(@byrds_byrds, N'Cowgirl in the Sand', 7),
(@byrds_byrds, N'Long Live the King', 8),
(@byrds_byrds, N'Borrowing Time', 9),
(@byrds_byrds, N'Laughing', 10),
(@byrds_byrds, N'(See the Sky) About to Rain', 11),

(@band_pink, N'Tears of Rage', 1),
(@band_pink, N'To Kingdom Come', 2),
(@band_pink, N'In a Station', 3),
(@band_pink, N'Caledonia Mission', 4),
(@band_pink, N'The Weight', 5),
(@band_pink, N'We Can Talk', 6),
(@band_pink, N'Long Black Veil', 7),
(@band_pink, N'Chest Fever', 8),
(@band_pink, N'Lonesome Suzie', 9),
(@band_pink, N'This Wheel''s on Fire', 10),
(@band_pink, N'I Shall Be Released', 11),

(@band_band, N'Across the Great Divide', 1),
(@band_band, N'Rag Mama Rag', 2),
(@band_band, N'The Night They Drove Old Dixie Down', 3),
(@band_band, N'When You Awake', 4),
(@band_band, N'Up on Cripple Creek', 5),
(@band_band, N'Whispering Pines', 6),
(@band_band, N'Jemima Surrender', 7),
(@band_band, N'Rockin'' Chair', 8),
(@band_band, N'Look Out Cleveland', 9),
(@band_band, N'Jawbone', 10),
(@band_band, N'The Unfaithful Servant', 11),
(@band_band, N'King Harvest (Has Surely Come)', 12),

(@band_fright, N'Strawberry Wine', 1),
(@band_fright, N'Sleeping', 2),
(@band_fright, N'Time to Kill', 3),
(@band_fright, N'Just Another Whistle Stop', 4),
(@band_fright, N'All La Glory', 5),
(@band_fright, N'The Shape I''m In', 6),
(@band_fright, N'The W.S. Walcott Medicine Show', 7),
(@band_fright, N'Daniel and the Sacred Harp', 8),
(@band_fright, N'Stage Fright', 9),
(@band_fright, N'The Rumor', 10),

(@band_cahoots, N'Life Is a Carnival', 1),
(@band_cahoots, N'When I Paint My Masterpiece', 2),
(@band_cahoots, N'Last of the Blacksmiths', 3),
(@band_cahoots, N'Where Do We Go From Here?', 4),
(@band_cahoots, N'4% Pantomime', 5),
(@band_cahoots, N'Shoot Out in Chinatown', 6),
(@band_cahoots, N'The Moon Struck One', 7),
(@band_cahoots, N'Thinkin'' Out Loud', 8),
(@band_cahoots, N'Smoke Signal', 9),
(@band_cahoots, N'Volcano', 10),
(@band_cahoots, N'The River Hymn', 11),

(@band_lights, N'Forbidden Fruit', 1),
(@band_lights, N'Hobo Jungle', 2),
(@band_lights, N'Ophelia', 3),
(@band_lights, N'Acadian Driftwood', 4),
(@band_lights, N'Ring Your Bell', 5),
(@band_lights, N'It Makes No Difference', 6),
(@band_lights, N'Jupiter Hollow', 7),
(@band_lights, N'Rags and Bones', 8),

(@ccr_ccr, N'I Put a Spell on You', 1),
(@ccr_ccr, N'The Working Man', 2),
(@ccr_ccr, N'Suzie Q', 3),
(@ccr_ccr, N'Ninety-Nine and a Half (Won''t Do)', 4),
(@ccr_ccr, N'Get Down Woman', 5),
(@ccr_ccr, N'Porterville', 6),
(@ccr_ccr, N'Gloomy', 7),
(@ccr_ccr, N'Walk on the Water', 8),

(@ccr_bayou, N'Born on the Bayou', 1),
(@ccr_bayou, N'Bootleg', 2),
(@ccr_bayou, N'Graveyard Train', 3),
(@ccr_bayou, N'Good Golly, Miss Molly', 4),
(@ccr_bayou, N'Penthouse Pauper', 5),
(@ccr_bayou, N'Proud Mary', 6),
(@ccr_bayou, N'Keep on Chooglin''', 7),

(@ccr_river, N'Green River', 1),
(@ccr_river, N'Commotion', 2),
(@ccr_river, N'Tombstone Shadow', 3),
(@ccr_river, N'Wrote a Song for Everyone', 4),
(@ccr_river, N'Bad Moon Rising', 5),
(@ccr_river, N'Lodi', 6),
(@ccr_river, N'Cross-Tie Walker', 7),
(@ccr_river, N'Sinister Purpose', 8),
(@ccr_river, N'The Night Time Is the Right Time', 9),

(@ccr_willy, N'Down on the Corner', 1),
(@ccr_willy, N'It Came Out of the Sky', 2),
(@ccr_willy, N'Cotton Fields', 3),
(@ccr_willy, N'Poorboy Shuffle', 4),
(@ccr_willy, N'Feelin'' Blue', 5),
(@ccr_willy, N'Fortunate Son', 6),
(@ccr_willy, N'Don''t Look Now (It Ain''t You or Me)', 7),
(@ccr_willy, N'The Midnight Special', 8),
(@ccr_willy, N'Side o'' the Road', 9),
(@ccr_willy, N'Effigy', 10),

(@ccr_cosmo, N'Ramble Tamble', 1),
(@ccr_cosmo, N'Before You Accuse Me', 2),
(@ccr_cosmo, N'Travelin'' Band', 3),
(@ccr_cosmo, N'Ooby Dooby', 4),
(@ccr_cosmo, N'Lookin'' Out My Back Door', 5),
(@ccr_cosmo, N'Run Through the Jungle', 6),
(@ccr_cosmo, N'Up Around the Bend', 7),
(@ccr_cosmo, N'My Baby Left Me', 8),
(@ccr_cosmo, N'Who''ll Stop the Rain', 9),
(@ccr_cosmo, N'I Heard It Through the Grapevine', 10),
(@ccr_cosmo, N'Long as I Can See the Light', 11),

(@ccr_pendulum, N'Pagan Baby', 1),
(@ccr_pendulum, N'Sailor''s Lament', 2),
(@ccr_pendulum, N'Chameleon', 3),
(@ccr_pendulum, N'Have You Ever Seen the Rain', 4),
(@ccr_pendulum, N'(Wish I Could) Hideaway', 5),
(@ccr_pendulum, N'Born to Move', 6),
(@ccr_pendulum, N'Hey Tonight', 7),
(@ccr_pendulum, N'It''s Just a Thought', 8),
(@ccr_pendulum, N'Molina', 9),
(@ccr_pendulum, N'Rude Awakening #2', 10),

(@kinks_face, N'Party Line', 1),
(@kinks_face, N'Rosy Won''t You Please Come Home', 2),
(@kinks_face, N'Dandy', 3),
(@kinks_face, N'Too Much on My Mind', 4),
(@kinks_face, N'Session Man', 5),
(@kinks_face, N'Rainy Day in June', 6),
(@kinks_face, N'A House in the Country', 7),
(@kinks_face, N'Holiday in Waikiki', 8),
(@kinks_face, N'Most Exclusive Residence for Sale', 9),
(@kinks_face, N'Fancy', 10),
(@kinks_face, N'Little Miss Queen of Darkness', 11),
(@kinks_face, N'You''re Lookin'' Fine', 12),
(@kinks_face, N'Sunny Afternoon', 13),
(@kinks_face, N'I''ll Remember', 14),

(@kinks_something, N'David Watts', 1),
(@kinks_something, N'Death of a Clown', 2),
(@kinks_something, N'Two Sisters', 3),
(@kinks_something, N'No Return', 4),
(@kinks_something, N'Harry Rag', 5),
(@kinks_something, N'Tin Soldier Man', 6),
(@kinks_something, N'Situation Vacant', 7),
(@kinks_something, N'Love Me Till the Sun Shines', 8),
(@kinks_something, N'Lazy Old Sun', 9),
(@kinks_something, N'Afternoon Tea', 10),
(@kinks_something, N'Funny Face', 11),
(@kinks_something, N'End of the Season', 12),
(@kinks_something, N'Waterloo Sunset', 13),

(@kinks_village, N'The Village Green Preservation Society', 1),
(@kinks_village, N'Do You Remember Walter?', 2),
(@kinks_village, N'Picture Book', 3),
(@kinks_village, N'Johnny Thunder', 4),
(@kinks_village, N'Last of the Steam-Powered Trains', 5),
(@kinks_village, N'Big Sky', 6),
(@kinks_village, N'Sitting by the Riverside', 7),
(@kinks_village, N'Animal Farm', 8),
(@kinks_village, N'Village Green', 9),
(@kinks_village, N'Starstruck', 10),
(@kinks_village, N'Phenomenal Cat', 11),
(@kinks_village, N'All of My Friends Were There', 12),
(@kinks_village, N'Wicked Annabella', 13),
(@kinks_village, N'Monica', 14),
(@kinks_village, N'People Take Pictures of Each Other', 15),

(@kinks_arthur, N'Victoria', 1),
(@kinks_arthur, N'Yes Sir, No Sir', 2),
(@kinks_arthur, N'Some Mother''s Son', 3),
(@kinks_arthur, N'Drivin''', 4),
(@kinks_arthur, N'Brainwashed', 5),
(@kinks_arthur, N'Australia', 6),
(@kinks_arthur, N'Shangri-La', 7),
(@kinks_arthur, N'Mr. Churchill Says', 8),
(@kinks_arthur, N'She''s Bought a Hat Like Princess Marina', 9),
(@kinks_arthur, N'Young and Innocent Days', 10),
(@kinks_arthur, N'Nothing to Say', 11),
(@kinks_arthur, N'Arthur', 12),

(@kinks_lola, N'The Contenders', 1),
(@kinks_lola, N'Strangers', 2),
(@kinks_lola, N'Denmark Street', 3),
(@kinks_lola, N'Get Back in Line', 4),
(@kinks_lola, N'Lola', 5),
(@kinks_lola, N'Top of the Pops', 6),
(@kinks_lola, N'The Moneygoround', 7),
(@kinks_lola, N'This Time Tomorrow', 8),
(@kinks_lola, N'A Long Way from Home', 9),
(@kinks_lola, N'Rats', 10),
(@kinks_lola, N'Apeman', 11),
(@kinks_lola, N'Powerman', 12),
(@kinks_lola, N'Got to Be Free', 13),

(@kinks_muswell, N'20th Century Man', 1),
(@kinks_muswell, N'Acute Schizophrenia Paranoia Blues', 2),
(@kinks_muswell, N'Holiday', 3),
(@kinks_muswell, N'Skin and Bone', 4),
(@kinks_muswell, N'Alcohol', 5),
(@kinks_muswell, N'Complicated Life', 6),
(@kinks_muswell, N'Here Come the People in Grey', 7),
(@kinks_muswell, N'Have a Cuppa Tea', 8),
(@kinks_muswell, N'Holloway Jail', 9),
(@kinks_muswell, N'Oklahoma U.S.A.', 10),
(@kinks_muswell, N'Uncle Son', 11),
(@kinks_muswell, N'Muswell Hillbilly', 12);

SELECT @guitar:=ID FROM Instruments
WHERE Name = N'Гитара';
SELECT @keys:=ID FROM Instruments
WHERE Name = N'Клавишные';
SELECT @drums:=ID FROM Instruments
WHERE Name = N'Ударные';
SELECT @bass:=ID FROM Instruments
WHERE Name = N'Бас-гитара';
SELECT @vocals:=ID FROM Instruments
WHERE Name = N'Вокал';
SELECT @backvox:=ID FROM Instruments
WHERE Name = N'Бэк-вокал';
SELECT @harmonica:=ID FROM Instruments
WHERE Name = N'Губная гармоника';
SELECT @sax:=ID FROM Instruments
WHERE Name = N'Саксофон';
SELECT @dbass:=ID FROM Instruments
WHERE Name = N'Контрабас';
SELECT @accordion:=ID FROM Instruments
WHERE Name = N'Аккордеон';
SELECT @banjo:=ID FROM Instruments
WHERE Name = N'Банджо';
SELECT @sitar:=ID FROM Instruments
WHERE Name = N'Ситар';

SELECT @lennon:=ID FROM Musicians
WHERE Name = N'John Lennon';
SELECT @macca:=ID FROM Musicians
WHERE Name = N'Paul McCartney';
SELECT @harrison:=ID FROM Musicians
WHERE Name = N'George Harrison';
SELECT @starr:=ID FROM Musicians
WHERE Name = N'Ringo Starr';

SELECT @mcguinn:=ID FROM Musicians
WHERE Name = N'Roger McGuinn';
SELECT @clark:=ID FROM Musicians
WHERE Name = N'Gene Clark';
SELECT @crosby:=ID FROM Musicians
WHERE Name = N'David Crosby';
SELECT @clarke:=ID FROM Musicians
WHERE Name = N'Michael Clarke';
SELECT @hillman:=ID FROM Musicians
WHERE Name = N'Chris Hillman';


SELECT @danko:=ID FROM Musicians
WHERE Name = N'Rick Danko';
SELECT @helm:=ID FROM Musicians
WHERE Name = N'Levon Helm';
SELECT @hudson:=ID FROM Musicians
WHERE Name = N'Garth Hudson';
SELECT @manuel:=ID FROM Musicians
WHERE Name = N'Richard Manuel';
SELECT @robertson:=ID FROM Musicians
WHERE Name = N'Robbie Robertson';

SELECT @jfogerty:=ID FROM Musicians
WHERE Name = N'John Fogerty';
SELECT @tfogerty:=ID FROM Musicians
WHERE Name = N'Tom Fogerty';
SELECT @cook:=ID FROM Musicians
WHERE Name = N'Stu Cook';
SELECT @clifford:=ID FROM Musicians
WHERE Name = N'Doug Clifford';

SELECT @rdavies:=ID FROM Musicians
WHERE Name = N'Ray Davies';
SELECT @ddavies:=ID FROM Musicians
WHERE Name = N'Dave Davies';
SELECT @avory:=ID FROM Musicians
WHERE Name = N'Mick Avory';
SELECT @quaife:=ID FROM Musicians
WHERE Name = N'Pete Quaife';

INSERT INTO MusicianInstrument (MusicianID, InstrumentID) VALUES
(@lennon, @guitar),
(@lennon, @keys),
(@lennon, @harmonica),
(@lennon, @vocals),
(@lennon, @bass),
(@macca, @vocals),
(@macca, @bass),
(@macca, @guitar),
(@macca, @keys),
(@macca, @drums),
(@harrison, @vocals),
(@harrison, @guitar),
(@harrison, @sitar),
(@harrison, @keys),
(@harrison, @bass),
(@starr, @drums),
(@starr, @vocals),

(@mcguinn, @guitar),
(@mcguinn, @banjo),
(@mcguinn, @keys),
(@mcguinn, @vocals),
(@clark, @guitar),
(@clark, @harmonica),
(@clark, @vocals),
(@crosby, @guitar),
(@crosby, @vocals),
(@clarke, @drums),
(@hillman, @bass),
(@hillman, @guitar),
(@hillman, @vocals),

(@danko, @bass),
(@danko, @vocals),
(@danko, @dbass),
(@danko, @guitar),
(@helm, @drums),
(@helm, @vocals),
(@helm, @guitar),
(@hudson, @keys),
(@hudson, @accordion),
(@hudson, @sax),
(@manuel, @keys),
(@manuel, @drums),
(@manuel, @vocals),
(@robertson, @guitar),
(@robertson, @vocals),

(@clifford, @drums),
(@clifford, @backvox),
(@cook, @bass),
(@cook, @backvox),
(@jfogerty, @guitar),
(@jfogerty, @vocals),
(@jfogerty, @keys),
(@jfogerty, @harmonica),
(@jfogerty, @sax),
(@tfogerty, @guitar),
(@tfogerty, @backvox),

(@rdavies, @vocals),
(@rdavies, @backvox),
(@rdavies, @guitar),
(@rdavies, @keys),
(@rdavies, @harmonica),
(@ddavies, @guitar),
(@ddavies, @vocals),
(@ddavies, @backvox),
(@ddavies, @keys),
(@avory, @drums),
(@quaife, @bass),
(@quaife, @backvox);

/************************************************
*				Проверка ограничений			*
************************************************/

/*
-- Смерть до рождения
INSERT INTO Musicians (Name, Band, DateOfBirth, DateOfDeath) VALUES
(N'Invalid Musician', @beatles, '1970-01-01', '1965-01-01');

-- Смерть до вступления в группу
INSERT INTO Musicians (Name, Band, DateOfBirth, DateOfDeath) VALUES
(N'Invalid Musician', @beatles, '1900-01-01', '1950-01-01');

-- Рождение после распада группы
INSERT INTO Musicians (Name, Band, DateOfBirth, DateOfDeath) VALUES
(N'Invalid Musician', @beatles, '1975-01-01', '1990-01-01');

-- Выпуск альбома до создания группы
INSERT INTO Albums (Band, Name, ReleaseDate, Genre) VALUES
(@beatles, N'Invalid Album', '1959-05-05', @rocknroll);

-- Добавление одновременно валидных и невалидных записей (не должно выполняться)
INSERT INTO Albums (Band, Name, ReleaseDate, Genre) VALUES
(@beatles, N'Valid Album', '1969-05-05', @rocknroll),
(@beatles, N'Invalid Album', '1940-01-01', @psych);
*/

/************************************************
*				Проверка функций				*
************************************************/

SELECT FormatAlbum(35);