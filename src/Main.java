import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    // Данные для подключения к БД
    private static final String url = "jdbc:mysql://localhost:3306/summerproject";
    private static final String user = "root";
    private static final String password = "sgtPeppers";

    // Переменные для работы с БД
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) {
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

    private static void getBands() {
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

                System.out.println(currentBand.toStringValue());
            }

        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        finally {
            try { statement.close(); } catch(SQLException se) { /*can't do anything */ }
            try { resultSet.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }
}
