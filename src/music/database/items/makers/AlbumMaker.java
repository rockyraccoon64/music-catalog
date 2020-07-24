package music.database.items.makers;

import music.database.DataStorage;
import music.database.SQLItem;
import music.database.items.Album;
import music.database.items.Band;
import music.database.items.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AlbumMaker extends DataItemMaker<Album> {

    private static final AlbumMaker INSTANCE = new AlbumMaker();

    private AlbumMaker() {

    }

    public static AlbumMaker getInstance() {
        return INSTANCE;
    }

    @Override
    public Album make(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("ID");
        int bandID = resultSet.getInt("Band");
        String albumName = resultSet.getNString("Name");
        LocalDate releaseDate = DataStorage.makeDate(resultSet.getDate("ReleaseDate"));
        int genreID = resultSet.getInt("Genre");
        byte[] image = resultSet.getBytes("CoverImage");

        Genre genre = (Genre)DataStorage.getItemByID(SQLItem.GENRES, genreID);
        Band band = (Band)DataStorage.getItemByID(SQLItem.BANDS, bandID);

        Album album = new Album(id, band, albumName, releaseDate, genre, image);
        band.addAlbum(album);

        return album;
    }

    @Override
    public String toStringValue(Album item) {
        return "Добавлен альбом: " + item.getName();
    }
}
