package music.database.items;

import music.database.TrackNumberException;
import music.database.WrongAlbumException;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

public class Album {
    private final int m_ID;
    private WeakReference<Band> m_band;
    private String m_name;
    private Date m_releaseDate;
    private Genre m_genre;
    private TreeSet<Song> m_songs;

    public Album(int id, Band band, String name, Date releaseDate, Genre genre) {
        m_ID = id;
        m_band = new WeakReference<>(band);
        m_name = name;
        m_releaseDate = releaseDate;
        m_genre = genre;
        m_songs = new TreeSet<>(new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                int trackNo1 = o1.getTrackNo();
                int trackNo2 = o2.getTrackNo();
                return trackNo1 - trackNo2;
            }
        });
    }

    public int getID() {
        return m_ID;
    }

    public Band getBand() {
        return m_band.get();
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
        m_band = new WeakReference<>(band);
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
        m_songs.add(song);
    }

    public void removeSong(Song song) {
        m_songs.remove(song);
    }
}
