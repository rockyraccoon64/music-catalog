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
    private static final HashMap<SQLItem, String> ITEM_NAMES = new HashMap<>();
    private static HashMap<Integer, TreeSet<Integer>> MUSICIAN_INSTRUMENT_MAP = new HashMap<>();

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
        ITEM_NAMES.put(SQLItem.MUSICIAN_INSTRUMENT, "MusicianInstrument");
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
            connectInstruments();
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

    public static void delete(SQLItem item, int id) throws SQLException {
        String query = "DELETE FROM " + ITEM_NAMES.get(item)
            + " WHERE ID = ?";

        PreparedStatement pstmt = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            MAPS.get(item).remove(id);
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
            refreshData();
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
            refreshData();
            //refreshItem(item.getType(), item.getID());
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

    private static void refreshItem(SQLItem item, int id) throws SQLException {
        //TODO
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + ITEM_NAMES.get(item)
            + " WHERE ID = " + id);
        DataItem updatedItem = DataItemMaker.getInstance(item).make(resultSet);
        MAPS.get(item).put(id, updatedItem);
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

    private static void connectInstruments() throws SQLException {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM MusicianInstrument;");
            while (resultSet.next()) {
                int musicianID = resultSet.getInt("MusicianID");
                int instrumentID = resultSet.getInt("InstrumentID");

                if (!MUSICIAN_INSTRUMENT_MAP.containsKey(musicianID)) {
                    MUSICIAN_INSTRUMENT_MAP.put(musicianID, new TreeSet<>());
                }
                TreeSet<Integer> set = MUSICIAN_INSTRUMENT_MAP.get(musicianID);
                set.add(instrumentID);

                Musician musician = (Musician)getItemByID(SQLItem.MUSICIANS, musicianID);
                Instrument instrument = (Instrument)getItemByID(SQLItem.INSTRUMENTS, instrumentID);
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
    public static Vector<Integer> getInstrumentIDs(Musician musician) {
        TreeSet<Integer> set = MUSICIAN_INSTRUMENT_MAP.get(musician.getID());
        if (set != null) {
            return new Vector<>(set);
        }
        else return new Vector<Integer>();
    }
    public static Collection<DataItem> getItems(SQLItem item) {
        return MAPS.get(item).values();
    }
}
