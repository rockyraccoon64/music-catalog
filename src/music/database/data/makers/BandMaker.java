package music.database.data.makers;

import music.database.data.items.Band;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BandMaker extends DataItemMaker<Band> {

    private static final BandMaker INSTANCE = new BandMaker();

    private BandMaker() {

    }

    public static BandMaker getInstance() {
        return INSTANCE;
    }

    @Override
    public Band make(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("ID");
        String bandName = resultSet.getNString("Name");
        short formYear = resultSet.getShort("YearOfFormation");
        short disbandYear = resultSet.getShort("YearOfDisbanding");
        byte[] image = resultSet.getBytes("Photo");

        return new Band(id, bandName, formYear, disbandYear, image);
    }

    @Override
    public String toStringValue(Band item) {
        return "Добавлена группа: " + item.getName();
    }
}
