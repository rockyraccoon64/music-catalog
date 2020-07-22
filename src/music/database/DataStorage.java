package music.database;

import music.database.items.*;
import music.database.updates.Update;
import music.database.updates.UpdateContainer;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DataStorage {

    // Данные для подключения к БД
    private static final String url = "jdbc:mysql://localhost:3306/summerproject?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "sgtPeppers";

    // Переменные для работы с БД
    private static Connection connection;
    private static Statement statement;

    private static HashMap<SQLItem, TreeMap> MAPS = new HashMap<>();
    private static final HashMap<SQLItem, String> QUERIES = new HashMap<>();
    private static final HashMap<SQLItem, String> ITEM_NAMES = new HashMap<>();
    private static final HashMap<SQLItem, String> BLOB_NAMES = new HashMap<>();

    public static void initialize() {
        initializeItemNames();
        initializeBlobNames();
        initializeQueryMap();
        initializeItemMaps();
    }

    private static void initializeBlobNames() {
        BLOB_NAMES.put(SQLItem.BANDS, "Photo");
        BLOB_NAMES.put(SQLItem.ALBUMS, "CoverImage");
    }

    private static void initializeItemNames() {
        ITEM_NAMES.put(SQLItem.BANDS, "Bands");
        ITEM_NAMES.put(SQLItem.ALBUMS, "Albums");
        ITEM_NAMES.put(SQLItem.MUSICIANS, "Musicians");
        ITEM_NAMES.put(SQLItem.INSTRUMENTS, "Instruments");
        ITEM_NAMES.put(SQLItem.GENRES, "Genres");
        ITEM_NAMES.put(SQLItem.SONGS, "Songs");
    }

    public static void refreshData() {
        try {
            connection = DriverManager.getConnection(url, user, password);

            executeGetterQuery(SQLItem.BANDS);
            executeGetterQuery(SQLItem.GENRES);
            executeGetterQuery(SQLItem.ALBUMS);
            executeGetterQuery(SQLItem.INSTRUMENTS);
            executeGetterQuery(SQLItem.MUSICIANS);
            executeGetterQuery(SQLItem.SONGS);
            executeGetterQuery(SQLItem.MUSICIAN_INSTRUMENT);
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

    public static void executeQuery(String query) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeQuery(query);
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

    public static void insertBlobFromFile(SQLItem item, int id, File file) throws IOException {
        String query = "UPDATE " + ITEM_NAMES.get(item)
                + " SET " + BLOB_NAMES.get(item) + " = ? "
                + " WHERE ID = ?";
        PreparedStatement pstmt = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            pstmt = connection.prepareStatement(query);
            FileInputStream input = new FileInputStream(file);
            byte[] bytes = input.readAllBytes();
            pstmt.setBinaryStream(1, new ByteArrayInputStream(bytes));
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            DataItem currentItem = getItemByID(item, id);

            switch (item) {
                case BANDS:
                    Band band = (Band)currentItem;
                    band.setImage(bytes);
                    break;
                case ALBUMS:
                    Album album = (Album)currentItem;
                    album.setImage(bytes);
                default:
                    break;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                connection.close();
                pstmt.close();
            }
            catch (SQLException ex) {

            }
        }
    }

    public static void insertBlob(ImageContainer item, byte[] bytes) throws IOException {
        String query = "UPDATE " + ITEM_NAMES.get(item.getType())
                + " SET " + BLOB_NAMES.get(item.getType()) + " = ? "
                + " WHERE ID = ?";
        PreparedStatement pstmt = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            pstmt = connection.prepareStatement(query);
            pstmt.setBinaryStream(1, new ByteArrayInputStream(bytes));
            pstmt.setInt(2, item.getID());
            pstmt.executeUpdate();
            item.setImage(bytes);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                connection.close();
                pstmt.close();
            }
            catch (SQLException ex) {

            }
        }
    }

    public static void update(UpdateContainer updateContainer) throws IOException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        DataItem item = updateContainer.getItem();
        Vector<Update> updates = updateContainer.getUpdates();

        sb.append(ITEM_NAMES.get(item.getType()));
        sb.append(" SET ");

        boolean isFirst = true;

        for (Update update : updates) {
            if (isFirst) {
                isFirst = false;
            }
            else sb.append(", ");
            sb.append(update.getField());
            sb.append(" = ?");
        }

        sb.append(" WHERE ID = ?");

        PreparedStatement pstmt = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            pstmt = connection.prepareStatement(sb.toString());

            int fieldIdx = 1;
            for (Update update : updates) {
                update.prepareStatement(pstmt, fieldIdx);
                fieldIdx++;
            }
            pstmt.setInt(fieldIdx, item.getID());
            pstmt.executeUpdate();
            refreshItem(item);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                connection.close();
                pstmt.close();
            }
            catch (SQLException ex) {

            }
        }
    }

    private static void refreshItem(DataItem item) throws SQLException {
        //TODO
        statement = connection.createStatement();
        // executing SELECT query
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + ITEM_NAMES.get(item.getType())
            + " WHERE ID = " + item.getID());

        while (resultSet.next()) {
            switch (item.getType()) {
                case ALBUMS:
                    int bandID = resultSet.getInt("Band");
                    String albumName = resultSet.getNString("Name");
                    LocalDate releaseDate = makeDate(resultSet.getDate("ReleaseDate"));
                    int genreID = resultSet.getInt("Genre");
                    byte[] image = resultSet.getBytes("CoverImage");

                    Genre genre = (Genre)getItemByID(SQLItem.GENRES, genreID);
                    Band band = (Band)getItemByID(SQLItem.BANDS, bandID);

                    Album album = (Album)item;
                    album.setName(albumName);
                    album.setReleaseDate(releaseDate);
                    album.setGenre(genre);
                    album.setBand(band);
                    album.setImage(image);

                    System.out.println("Обновлён альбом: " + album.getName());
                    break;
                default:
                    break;
            }

        }
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
        MAPS.put(SQLItem.BANDS, new TreeMap<Integer, Band>());
        MAPS.put(SQLItem.GENRES, new TreeMap<Integer, Genre>());
        MAPS.put(SQLItem.INSTRUMENTS, new TreeMap<Integer, Instrument>());
        MAPS.put(SQLItem.MUSICIANS, new TreeMap<Integer, Musician>());
        MAPS.put(SQLItem.ALBUMS, new TreeMap<Integer, Album>());
        MAPS.put(SQLItem.SONGS, new TreeMap<Integer, Song>());
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
        TreeMap<Integer, Band> bandMap = MAPS.get(SQLItem.BANDS);
        bandMap.clear();
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String bandName = resultSet.getNString("Name");
            short formYear = resultSet.getShort("YearOfFormation");
            short disbandYear = resultSet.getShort("YearOfDisbanding");
            byte[] image = resultSet.getBytes("Photo");

            Band band = new Band(id, bandName, formYear, disbandYear, image);

            bandMap.put(id, band);

            System.out.println("Добавлена группа: " + band.getName());
        }
    }

    private static void getGenres(ResultSet resultSet) throws SQLException {
        TreeMap<Integer, Genre> genreMap = MAPS.get(SQLItem.GENRES);
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
        TreeMap<Integer, Album> albumMap = MAPS.get(SQLItem.ALBUMS);
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            int bandID = resultSet.getInt("Band");
            String albumName = resultSet.getNString("Name");
            LocalDate releaseDate = makeDate(resultSet.getDate("ReleaseDate"));
            int genreID = resultSet.getInt("Genre");
            byte[] image = resultSet.getBytes("CoverImage");

            Genre genre = (Genre)getItemByID(SQLItem.GENRES, genreID);
            Band band = (Band)getItemByID(SQLItem.BANDS, bandID);

            Album album = new Album(id, band, albumName, releaseDate, genre, image);
            band.addAlbum(album);
            albumMap.put(id, album);

            System.out.println("Добавлен альбом: " + album.getName());
        }
    }

    private static void getInstruments(ResultSet resultSet) throws SQLException {
        TreeMap<Integer, Instrument> instrumentMap = MAPS.get(SQLItem.INSTRUMENTS);
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String instrumentName = resultSet.getNString("Name");
            Instrument instrument = new Instrument(id, instrumentName);
            instrumentMap.put(id, instrument);
            System.out.println("Добавлен инструмент: " + instrumentName);
        }
    }

    private static void getMusicians(ResultSet resultSet) throws SQLException {
        TreeMap<Integer, Musician> musicianMap = MAPS.get(SQLItem.MUSICIANS);
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
        TreeMap<Integer, Song> songMap = MAPS.get(SQLItem.SONGS);
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

    public static DataItem getItemByID(SQLItem item, int id) {
        TreeMap<Integer, DataItem> map = MAPS.get(item);
        return map.get(id);
    }

    public static Collection<DataItem> getItems(SQLItem item) {
        switch (item) {
            case BANDS:
                return MAPS.get(SQLItem.BANDS).values();
            default:
                return null;
        }
    }
}
