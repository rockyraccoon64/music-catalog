package music.database;

import music.database.items.*;

import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;

public class MusicApp extends Frame {

    enum SQLItem {
        BANDS,
        GENRES,
        ALBUMS,
        INSTRUMENTS,
        MUSICIANS,
        SONGS,
        MUSICIAN_INSTRUMENT
    }

    private static final HashMap<SQLItem, String> QUERIES = new HashMap<>();

    // Данные для подключения к БД
    private static final String url = "jdbc:mysql://localhost:3306/summerproject?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "sgtPeppers";

    // Переменные для работы с БД
    private static Connection connection;
    private static Statement statement;

    private static HashMap<SQLItem, HashMap> MAPS = new HashMap<>();

    public MusicApp() {
        setLayout(new BorderLayout());

        setTitle("Музыкальная база данных");
        setSize(1000, 750);

        setVisible(true);
    }

    public static void main(String[] args) {
        initializeQueryMap();
        initializeItemMaps();
        new MusicApp();
        try {
            connection = DriverManager.getConnection(url, user, password);
            refreshData();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex) {

            }
        }
    }

    private static void executeGetterQuery(SQLItem item) throws SQLException {
        ResultSet resultSet = null;
        try {
            // getting Statement object to execute query
            statement = connection.createStatement();
            // executing SELECT query
            resultSet = statement.executeQuery(QUERIES.get(item));

            switch (item) {
                case BANDS:
                    getBands(resultSet);
                    break;
                case ALBUMS:
                    getAlbums(resultSet);
                    break;
                case GENRES:
                    getGenres(resultSet);
                    break;
                case INSTRUMENTS:
                    getInstruments(resultSet);
                    break;
                case MUSICIANS:
                    getMusicians(resultSet);
                    break;
                case SONGS:
                    getSongs(resultSet);
                    break;
                case MUSICIAN_INSTRUMENT:
                    getMusicianInstruments(resultSet);
                    break;
                default:
                    break;
            }
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            throw sqlEx;
        }
        finally {
            try { statement.close(); } catch(SQLException se) { /*can't do anything */ }
            if (resultSet != null) {
                try { resultSet.close(); } catch(SQLException se) { /*can't do anything */ }
            }
        }
    }

    private static void getBands(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Band> bandMap = MAPS.get(SQLItem.BANDS);
        bandMap.clear();
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String bandName = resultSet.getNString("Name");
            short formYear = resultSet.getShort("YearOfFormation");
            short disbandYear = resultSet.getShort("YearOfDisbanding");

            Band band = new Band(id, bandName, formYear, disbandYear);

            bandMap.put(id, band);

            System.out.println("Добавлена группа: " + band.getName());
        }
    }

    private static void getGenres(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Genre> genreMap = MAPS.get(SQLItem.GENRES);
        genreMap.clear();
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String genreName = resultSet.getNString("Name");
            Genre genre = new Genre(id, genreName);
            genreMap.put(id, genre);
            System.out.println("Добавлен жанр: " + genre.getName());
        }
    }

    private static void getAlbums(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Album> albumMap = MAPS.get(SQLItem.ALBUMS);
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            int bandID = resultSet.getInt("Band");
            String albumName = resultSet.getNString("Name");
            LocalDate releaseDate = makeDate(resultSet.getDate("ReleaseDate"));
            int genreID = resultSet.getInt("Genre");

            Genre genre = (Genre)getItemByID(SQLItem.GENRES, genreID);
            Band band = (Band)getItemByID(SQLItem.BANDS, bandID);

            Album album = new Album(id, band, albumName, releaseDate, genre);
            band.addAlbum(album);
            albumMap.put(id, album);

            System.out.println("Добавлен альбом: " + album.getName());
        }
    }

    private static void getInstruments(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Instrument> instrumentMap = MAPS.get(SQLItem.INSTRUMENTS);
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String instrumentName = resultSet.getNString("Name");
            Instrument instrument = new Instrument(id, instrumentName);
            instrumentMap.put(id, instrument);
            System.out.println("Добавлен инструмент: " + instrumentName);
        }
    }

    private static void getMusicians(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Musician> musicianMap = MAPS.get(SQLItem.MUSICIANS);
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String musicianName = resultSet.getNString("Name");
            LocalDate birthDate = makeDate(resultSet.getDate("DateOfBirth"));
            LocalDate deathDate = makeDate(resultSet.getDate("DateOfDeath"));
            int bandID = resultSet.getInt("Band");
            Band band = (Band)getItemByID(SQLItem.BANDS, bandID);

            Musician musician = new Musician(id, musicianName, band, birthDate, deathDate);
            band.addMusician(musician);
            musicianMap.put(id, musician);

            System.out.println("Добавлен музыкант: " + musicianName);
        }
    }

    private static void getSongs(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Song> songMap = MAPS.get(SQLItem.SONGS);
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String songName = resultSet.getNString("Name");
            int albumID = resultSet.getInt("Album");
            int trackNo = resultSet.getInt("TrackNo");
            Album album = (Album)getItemByID(SQLItem.ALBUMS, albumID);

            Song song = new Song(id, songName, album, trackNo);
            album.addSong(song);
            songMap.put(id, song);

            System.out.println("Добавлена песня: " + songName);
        }
    }

    private static void getMusicianInstruments(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int musicianID = resultSet.getInt("MusicianID");
            int instrumentID = resultSet.getInt("InstrumentID");
            Musician musician = (Musician)getItemByID(SQLItem.MUSICIANS, musicianID);
            Instrument instrument = (Instrument)getItemByID(SQLItem.INSTRUMENTS, instrumentID);
            musician.addInstrument(instrument);
            System.out.println("Добавлен инструмент " + instrument.getName() + " у музыканта " + musician.getName());
        }
    }

    private static LocalDate makeDate(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private static DataItem getItemByID(SQLItem item, int id) {
        HashMap<Integer, DataItem> map = MAPS.get(item);
        return map.get(id);
    }

    private static void refreshData() throws SQLException {
        executeGetterQuery(SQLItem.BANDS);
        executeGetterQuery(SQLItem.GENRES);
        executeGetterQuery(SQLItem.ALBUMS);
        executeGetterQuery(SQLItem.INSTRUMENTS);
        executeGetterQuery(SQLItem.MUSICIANS);
        executeGetterQuery(SQLItem.SONGS);
        executeGetterQuery(SQLItem.MUSICIAN_INSTRUMENT);
    }

    private static void initializeQueryMap() {
        QUERIES.put(SQLItem.BANDS, "SELECT * FROM Bands;");
        QUERIES.put(SQLItem.GENRES, "SELECT * FROM Genres;");
        QUERIES.put(SQLItem.ALBUMS, "SELECT * FROM Albums;");
        QUERIES.put(SQLItem.INSTRUMENTS, "SELECT * FROM Instruments;");
        QUERIES.put(SQLItem.MUSICIANS, "SELECT * FROM Musicians;");
        QUERIES.put(SQLItem.SONGS, "SELECT * FROM Songs;");
        QUERIES.put(SQLItem.MUSICIAN_INSTRUMENT, "SELECT * FROM MusicianInstrument;");
    }

    private static void initializeItemMaps() {
        MAPS.put(SQLItem.BANDS, new HashMap<Integer, Band>());
        MAPS.put(SQLItem.GENRES, new HashMap<Integer, Genre>());
        MAPS.put(SQLItem.INSTRUMENTS, new HashMap<Integer, Instrument>());
        MAPS.put(SQLItem.MUSICIANS, new HashMap<Integer, Musician>());
        MAPS.put(SQLItem.ALBUMS, new HashMap<Integer, Album>());
        MAPS.put(SQLItem.SONGS, new HashMap<Integer, Song>());
    }
}
