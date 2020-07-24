package music.database.items.makers;

import music.database.DataStorage;
import music.database.SQLItem;
import music.database.items.Album;
import music.database.items.Song;

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

        Song song = new Song(id, songName, album, trackNo);
        album.addSong(song);

        return song;
    }

    @Override
    public String toStringValue(Song item) {
        return "Добавлена песня: " + item.getName();
    }
}
