package music.database.items.makers;

import music.database.DataStorage;
import music.database.SQLItem;
import music.database.items.Band;
import music.database.items.Musician;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class MusicianMaker extends DataItemMaker<Musician> {

    private static final MusicianMaker INSTANCE = new MusicianMaker();

    private MusicianMaker() {

    }

    public static MusicianMaker getInstance() {
        return INSTANCE;
    }

    @Override
    public Musician make(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("ID");
        String musicianName = resultSet.getNString("Name");
        LocalDate birthDate = DataStorage.makeDate(resultSet.getDate("DateOfBirth"));
        LocalDate deathDate = DataStorage.makeDate(resultSet.getDate("DateOfDeath"));
        int bandID = resultSet.getInt("Band");
        Band band = (Band) DataStorage.getItemByID(SQLItem.BANDS, bandID);

        Musician musician = new Musician(id, musicianName, band, birthDate, deathDate);
        band.addMusician(musician);

        return musician;
    }

    @Override
    public String toStringValue(Musician item) {
        return "Добавлен музыкант: " + item.getName();
    }
}
