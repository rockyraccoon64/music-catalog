package music.database.items;

import music.database.DataStorage;
import music.database.SQLItem;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Vector;

public class Album extends ImageContainer implements Comparable<Album> {
    private WeakReference<Band> m_band;
    private String m_name;
    private LocalDate m_releaseDate;
    private Genre m_genre;

    public Album(int id, Band band, String name, LocalDate releaseDate, Genre genre, byte[] image) {
        super(id, SQLItem.ALBUMS, image);
        m_band = new WeakReference<>(band);
        m_name = name;
        m_releaseDate = releaseDate;
        m_genre = genre;
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

    public Vector<Song> getSongs() {
        Vector<DataItem> songCollection = new Vector<>(DataStorage.getItems(SQLItem.SONGS));
        songCollection.removeIf(item -> {
            Song song = (Song)item;
            return song.getAlbum().getID() != getID();
        });
        Vector<Song> songs = new Vector<>();
        for (DataItem item : songCollection) {
            songs.add((Song)item);
        }
        return songs;
    }

    @Override
    public int compareTo(Album o) {
        if (getBand().getID() != o.getBand().getID()) {
            return 0;
        }
        LocalDate firstDate = getReleaseDate();
        LocalDate secondDate = o.getReleaseDate();
        int dateComparison = 0;
        if (firstDate != null) {
            if (secondDate != null) {
                dateComparison = firstDate.compareTo(secondDate);
            }
            else dateComparison = -1;
        }
        else if (secondDate != null) {
            dateComparison = 1;
        }
        return dateComparison == 0 ? getName().compareTo(o.getName()) : dateComparison;
    }
}
