package music.database;

import music.database.items.Album;
import music.database.items.Band;
import music.database.items.Genre;

import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;

public class MusicApp extends Frame {

    enum SQLOperation {
        GET_BANDS,
        GET_GENRES,
        GET_ALBUMS
    }

    private static final HashMap<SQLOperation, String> QUERIES = new HashMap<>();

    // Данные для подключения к БД
    private static final String url = "jdbc:mysql://localhost:3306/summerproject?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "sgtPeppers";

    // Переменные для работы с БД
    private static Connection connection;
    private static Statement statement;

    private static TreeMap<Integer, Band> m_bands = new TreeMap<>();
    private static TreeMap<Integer, Genre> m_genres = new TreeMap<>();

    public MusicApp() {
        setLayout(new BorderLayout());

        setTitle("Музыкальная база данных");
        setSize(1000, 750);

        setVisible(true);
    }

    public static void main(String[] args) {
        initializeQueryMap();
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

    private static void executeQuery(SQLOperation operation) throws SQLException {
        ResultSet resultSet = null;
        try {
            // getting Statement object to execute query
            statement = connection.createStatement();
            // executing SELECT query
            resultSet = statement.executeQuery(QUERIES.get(operation));

            switch (operation) {
                case GET_BANDS:
                    getBands(resultSet);
                    break;
                case GET_ALBUMS:
                    getAlbums(resultSet);
                    break;
                case GET_GENRES:
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
        m_bands.clear();
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String bandName = resultSet.getNString("Name");
            short formYear = resultSet.getShort("YearOfFormation");
            short disbandYear = resultSet.getShort("YearOfDisbanding");

            Band currentBand = new Band(id, bandName, formYear, disbandYear);

            m_bands.put(id, currentBand);

            System.out.println(currentBand.toStringValue());
        }
    }

    private static void getGenres(ResultSet resultSet) throws SQLException {
        m_genres.clear();
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String genreName = resultSet.getNString("Name");

            Genre currentGenre = new Genre(id, genreName);

            m_genres.put(id, currentGenre);

            System.out.println(currentGenre.getName());
        }
    }

    private static void getAlbums(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            int bandID = resultSet.getInt("Band");
            String albumName = resultSet.getNString("Name");
            LocalDate releaseDate = resultSet.getDate("ReleaseDate").toLocalDate();
            int genreID = resultSet.getInt("Genre");
            Genre genre = m_genres.get(genreID);

            Band band = m_bands.get(bandID);

            Album currentAlbum = new Album(id, band, albumName, releaseDate, genre);

            band.addAlbum(currentAlbum);

            System.out.println(currentAlbum.getName() + " (" + currentAlbum.getReleaseDate().getYear() + ")");
        }
    }

    private static void refreshData() throws SQLException {
        executeQuery(SQLOperation.GET_BANDS);
        executeQuery(SQLOperation.GET_GENRES);
        executeQuery(SQLOperation.GET_ALBUMS);
    }

    private static void initializeQueryMap() {
        QUERIES.put(SQLOperation.GET_BANDS, "SELECT * FROM Bands;");
        QUERIES.put(SQLOperation.GET_GENRES, "SELECT * FROM Genres;");
        QUERIES.put(SQLOperation.GET_ALBUMS, "SELECT * FROM Albums;");
    }
}
