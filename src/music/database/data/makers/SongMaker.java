package music.database.data.makers;

import music.database.data.DataStorage;
import music.database.data.items.SQLItem;
import music.database.data.items.Album;
import music.database.data.items.Song;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SongMaker extends DataItemMaker<Song> {

    private static final SongMaker INSTANCE = new SongMaker();

    private SongMaker() {

    }

    public static SongMaker getInstance() {
        return INSTANCE;
    }

    @Override
    public Song make(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("ID");
        String songName = resultSet.getNString("Name");
        int albumID = resultSet.getInt("Album");
        int trackNo = resultSet.getInt("TrackNo");
        Album album = (Album)DataStorage.getItemByID(SQLItem.ALBUMS, albumID);

        return new Song(id, songName, album, trackNo);
    }

    @Override
    public String toStringValue(Song item) {
        return "Добавлена песня: " + item.getName();
    }
}
