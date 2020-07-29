package music.database.data.makers;

import music.database.data.items.Instrument;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstrumentMaker extends DataItemMaker<Instrument> {

    private static final InstrumentMaker INSTANCE = new InstrumentMaker();

    private InstrumentMaker() {

    }

    public static InstrumentMaker getInstance() {
        return INSTANCE;
    }

    @Override
    public Instrument make(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("ID");
        String instrumentName = resultSet.getNString("Name");
        return new Instrument(id, instrumentName);
    }

    @Override
    public String toStringValue(Instrument item) {
        return "Добавлен инструмент: " + item.getName();
    }
}
