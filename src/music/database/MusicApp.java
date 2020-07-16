package music.database;

import music.database.items.Band;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.TreeSet;

public class MusicApp extends Frame {

    // Данные для подключения к БД
    private static final String url = "jdbc:mysql://localhost:3306/summerproject";
    private static final String user = "root";
    private static final String password = "sgtPeppers";

    // Переменные для работы с БД
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    private static TreeSet<Band> m_bands = new TreeSet<>(new Comparator<Band>() {
        @Override
        public int compare(Band o1, Band o2) {
            String name1 = o1.getName();
            String name2 = o2.getName();
            return name1.compareTo(name2);
        }
    });

    public MusicApp() {
        setLayout(new BorderLayout());

        setTitle("Музыкальная база данных");
        setSize(1000, 750);

        setVisible(true);
    }

    public static void main(String[] args) {

        new MusicApp();

        try {
            connection = DriverManager.getConnection(url, user, password);
            getBands();
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

    private static void getBands() throws SQLException {
        String query = "SELECT * FROM Bands;";
        try {
            // getting Statement object to execute query
            statement = connection.createStatement();

            // executing SELECT query
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String bandName = resultSet.getNString("Name");
                short formYear = resultSet.getShort("YearOfFormation");
                short disbandYear = resultSet.getShort("YearOfDisbanding");

                Band currentBand = new Band(id, bandName, formYear, disbandYear);

                m_bands.add(currentBand);

                System.out.println(currentBand.toStringValue());
            }

        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();

            throw sqlEx;
        }
        finally {
            try { statement.close(); } catch(SQLException se) { /*can't do anything */ }
            try { resultSet.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }
}
