package music.database.items;

import music.database.SQLItem;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.TreeSet;

public class Album extends DataItem implements ImageContainer {
    private WeakReference<Band> m_band;
    private String m_name;
    private LocalDate m_releaseDate;
    private Genre m_genre;
    private byte[] m_image;
    private TreeSet<Song> m_songs;

    public Album(int id, Band band, String name, LocalDate releaseDate, Genre genre, byte[] image) {
        super(id, SQLItem.ALBUMS);
        m_band = new WeakReference<>(band);
        m_name = name;
        m_releaseDate = releaseDate;
        m_genre = genre;
        m_image = image;
        m_songs = new TreeSet<>(new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                int trackNo1 = o1.getTrackNo();
                int trackNo2 = o2.getTrackNo();
                return trackNo1 - trackNo2;
            }
        });
    }

    public Band getBand() {
        return m_band.get();
    }

    public String getName() {
        return m_name;
    }

    public LocalDate getReleaseDate() {
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

    public void setReleaseDate(LocalDate releaseDate) {
        m_releaseDate = releaseDate;
    }

    public void setGenre(Genre genre) {
        m_genre = genre;
    }

    public void addSong(Song song) {
        m_songs.add(song);
    }

    public void removeSong(Song song) {
        m_songs.remove(song);
    }

    public TreeSet<Song> getSongs() {
        return m_songs;
    }

    public byte[] getImage() {
        return m_image;
    }

    public void setImage(byte[] image) {
        m_image = image;
    }
}
