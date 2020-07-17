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
        SONGS
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

            Band currentBand = new Band(id, bandName, formYear, disbandYear);

            bandMap.put(id, currentBand);

            System.out.println("Добавлена группа: " + currentBand.toStringValue());
        }
    }

    private static void getGenres(ResultSet resultSet) throws SQLException {
        HashMap<Integer, Genre> genreMap = MAPS.get(SQLItem.GENRES);
        genreMap.clear();
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String genreName = resultSet.getNString("Name");

            Genre currentGenre = new Genre(id, genreName);

            genreMap.put(id, currentGenre);

            System.out.println("Добавлен жанр: " + currentGenre.getName());
        }
    }

    private static void getAlbums(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            int bandID = resultSet.getInt("Band");
            String albumName = resultSet.getNString("Name");
            LocalDate releaseDate = resultSet.getDate("ReleaseDate").toLocalDate();
            int genreID = resultSet.getInt("Genre");

            HashMap<Integer, Genre> genreMap = MAPS.get(SQLItem.GENRES);
            Genre genre = genreMap.get(genreID);

            HashMap<Integer, Band> bandMap = MAPS.get(SQLItem.BANDS);
            Band band = bandMap.get(bandID);

            Album currentAlbum = new Album(id, band, albumName, releaseDate, genre);

            band.addAlbum(currentAlbum);

            System.out.println("Добавлен альбом: " + currentAlbum.getName() + " (" + currentAlbum.getReleaseDate().getYear() + ")");
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
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String musicianName = resultSet.getNString("Name");
            LocalDate birthDate = resultSet.getDate("DateOfBirth").toLocalDate();
            LocalDate deathDate = resultSet.getDate("DateOfDeath").toLocalDate();
            int bandID = resultSet.getInt("Band");

            System.out.println("Добавлен музыкант: " + musicianName);
        }
    }

    private static void refreshData() throws SQLException {
        executeGetterQuery(SQLItem.BANDS);
        executeGetterQuery(SQLItem.GENRES);
        executeGetterQuery(SQLItem.ALBUMS);
        executeGetterQuery(SQLItem.INSTRUMENTS);
        executeGetterQuery(SQLItem.MUSICIANS);
        executeGetterQuery(SQLItem.SONGS);
    }

    private static void initializeQueryMap() {
        QUERIES.put(SQLItem.BANDS, "SELECT * FROM Bands;");
        QUERIES.put(SQLItem.GENRES, "SELECT * FROM Genres;");
        QUERIES.put(SQLItem.ALBUMS, "SELECT * FROM Albums;");
        QUERIES.put(SQLItem.INSTRUMENTS, "SELECT * FROM Instruments;");
        QUERIES.put(SQLItem.MUSICIANS, "SELECT * FROM Musicians;");
        QUERIES.put(SQLItem.SONGS, "SELECT * FROM Songs;");
    }

    private static void initializeItemMaps() {
        MAPS.put(SQLItem.BANDS, new HashMap<Integer, Band>());
        MAPS.put(SQLItem.GENRES, new HashMap<Integer, Genre>());
        MAPS.put(SQLItem.ALBUMS, new HashMap<Integer, Album>());;
        MAPS.put(SQLItem.INSTRUMENTS, new HashMap<Integer, Instrument>());;
        MAPS.put(SQLItem.MUSICIANS, new HashMap<Integer, Musician>());;
        MAPS.put(SQLItem.SONGS, new HashMap<Integer, Song>());
    }
}
