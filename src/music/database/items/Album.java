package music.database.items;

import music.database.SQLItem;
import music.database.WeakSet;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.TreeSet;

public class Album extends ImageContainer {
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
        Band currentBand = m_band.get();
        currentBand.removeAlbum(this);
        band.addAlbum(this);
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

}
