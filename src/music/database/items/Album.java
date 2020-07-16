package music.database.items;

import music.database.TrackNumberException;
import music.database.WrongAlbumException;

import java.util.Date;
import java.util.HashMap;

public class Album {
    private final int m_ID;
    private Band m_band;
    private String m_name;
    private Date m_releaseDate;
    private Genre m_genre;
    private HashMap<Integer, Song> m_songs;

    public Album(int id, Band band, String name, Date releaseDate, Genre genre) {
        m_ID = id;
        m_band = band;
        m_name = name;
        m_releaseDate = releaseDate;
        m_genre = genre;
    }

    public int getID() {
        return m_ID;
    }

    public Band getBand() {
        return m_band;
    }

    public String getName() {
        return m_name;
    }

    public Date getReleaseDate() {
        return m_releaseDate;
    }

    public Genre getGenre() {
        return m_genre;
    }

    public void setBand(Band band) {
        m_band = band;
    }

    public void setName(String name) {
        m_name = name;
    }

    public void setReleaseDate(Date releaseDate) {
        m_releaseDate = releaseDate;
    }

    public void setGenre(Genre genre) {
        m_genre = genre;
    }

    public void addSong(Song song) throws WrongAlbumException, TrackNumberException {
        if (!song.getAlbum().equals(this)) {
            throw new WrongAlbumException();
        }

        int trackNo = song.getTrackNo();

        if (m_songs.containsKey(trackNo)) {
            throw new TrackNumberException();
        }

        m_songs.put(trackNo, song);
    }
}
