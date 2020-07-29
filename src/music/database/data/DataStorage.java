package music.database.data;

import music.database.data.items.DataItem;
import music.database.data.items.Instrument;
import music.database.data.items.Musician;
import music.database.data.items.SQLItem;
import music.database.data.makers.DataItemMaker;
import music.database.fields.Field;
import music.database.fields.FieldContainer;

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

    public static void delete(SQLItem item, FieldContainer fieldContainer) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(ITEM_NAMES.get(item));
        sb.append(" WHERE ");

        Vector<Field> fields = fieldContainer.getUpdates();
        boolean isFirst = true;
        for (Field f : fields) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sb.append(" AND ");
            }
            sb.append(f.getField());
            sb.append(" = ?");
        }
        sb.append(";");

        PreparedStatement pstmt = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            pstmt = connection.prepareStatement(sb.toString());
            int index = 1;
            for (Field f : fields) {
                f.prepareStatement(pstmt, index);
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

    public static void insert(SQLItem item, FieldContainer fieldContainer) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(ITEM_NAMES.get(item));
        sb.append(" (");

        Vector<Field> fields = fieldContainer.getUpdates();

        StringBuilder sb2 = new StringBuilder(") VALUES (");

        boolean isFirst = true;
        for (Field u : fields) {
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
            for (Field u : fields) {
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

    public static void update(FieldContainer fieldContainer) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        DataItem item = fieldContainer.getItem();
        Vector<Field> fields = fieldContainer.getUpdates();

        sb.append(ITEM_NAMES.get(item.getType()));
        sb.append(" SET ");

        boolean isFirst = true;

        for (Field field : fields) {
            if (isFirst) {
                isFirst = false;
            }
            else sb.append(", ");
            sb.append(field.getField());
            sb.append(" = ?");
        }

        sb.append(" WHERE ID = ?");

        PreparedStatement pstmt = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            pstmt = connection.prepareStatement(sb.toString());

            int fieldIdx = 1;
            for (Field field : fields) {
                field.prepareStatement(pstmt, fieldIdx);
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
