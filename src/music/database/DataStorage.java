package music.database;

import music.database.items.*;
import music.database.items.makers.DataItemMaker;
import music.database.updates.Update;
import music.database.updates.UpdateContainer;

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

    public static void initialize() {
        initializeItemNames();
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
            addInstruments();
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

    public static void insert(SQLItem item, UpdateContainer updateContainer) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(ITEM_NAMES.get(item));
        sb.append(" (");

        Vector<Update> updates = updateContainer.getUpdates();

        StringBuilder sb2 = new StringBuilder(") VALUES (");

        boolean isFirst = true;
        for (Update u : updates) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append(", ");
                sb2.append(", ");
            }

            sb.append(u.getField());
            sb2.append("?");
        }
        sb.append(sb2);
        sb.append(");");
        PreparedStatement pstmt = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            pstmt = connection.prepareStatement(sb.toString());
            int index = 1;
            for (Update u : updates) {
                u.prepareStatement(pstmt, index);
                index++;
            }
            pstmt.executeUpdate();
            // TODO Создать объект, соответствующий добавленному элементу
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
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

    public static void update(UpdateContainer updateContainer) throws SQLException {
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
            throw ex;
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

    private static void executeGetterQuery(SQLItem item) throws SQLException {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + ITEM_NAMES.get(item) + ";");
            MAPS.put(item, DataItemMaker.getInstance(item).makeAll(resultSet));
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

    private static void addInstruments() throws SQLException {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM MusicianInstrument;");
            while (resultSet.next()) {
                int musicianID = resultSet.getInt("MusicianID");
                int instrumentID = resultSet.getInt("InstrumentID");
                Musician musician = (Musician)getItemByID(SQLItem.MUSICIANS, musicianID);
                Instrument instrument = (Instrument)getItemByID(SQLItem.INSTRUMENTS, instrumentID);
                musician.addInstrument(instrument);
                System.out.println("Добавлен инструмент " + instrument.getName() + " у музыканта " + musician.getName());
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

    public static LocalDate makeDate(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }

    public static DataItem getItemByID(SQLItem item, int id) {
        TreeMap<Integer, DataItem> map = MAPS.get(item);
        return map.get(id);
    }

    public static Collection<DataItem> getItems(SQLItem item) {
        return MAPS.get(item).values();
    }
}
