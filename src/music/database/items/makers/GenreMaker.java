package music.database.items.makers;

import music.database.items.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMaker extends DataItemMaker<Genre> {

    private static final GenreMaker INSTANCE = new GenreMaker();

    private GenreMaker() {

    }

    public static GenreMaker getInstance() {
        return INSTANCE;
    }

    @Override
    public Genre make(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("ID");
        String genreName = resultSet.getNString("Name");
        return new Genre(id, genreName);
    }

    @Override
    public String toStringValue(Genre item) {
        return "Добавлен жанр: " + item.getName();
    }
}
