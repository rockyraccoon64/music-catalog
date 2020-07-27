package music.database.items;

import music.database.DataStorage;
import music.database.SQLItem;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;

public class Band extends ImageContainer {
    private String m_name;
    private short m_formYear;
    private short m_disbandYear;

    public Band(int id, String name, short formYear, short disbandYear, byte[] image) {
        super(id, SQLItem.BANDS, image);
        m_name = name;
        m_formYear = formYear;
        m_disbandYear = disbandYear;
    }

    public String getName() {
        return m_name;
    }

    public short getFormYear() {
        return m_formYear;
    }

    public Short getDisbandYear() {
        return m_disbandYear;
    }

    public void setName(String name) {
        m_name = name;
    }

    public void setFormYear(short year) {
        m_formYear = year;
    }

    public void setDisbandYear(short year) {
        m_disbandYear = year;
    }

    public Vector<Album> getAlbums() {
        Collection<DataItem> albumCollection = DataStorage.getItems(SQLItem.ALBUMS);
        albumCollection.removeIf(item -> {
            Album album = (Album)item;
            return album.getBand().getID() != getID();
        });
        Vector<Album> albums = new Vector<>();
        for (DataItem item : albumCollection) {
            albums.add((Album)item);
        }
        return albums;
    }

    public Vector<Musician> getMusicians() {
        Collection<DataItem> musicianCollection = DataStorage.getItems(SQLItem.MUSICIANS);
        musicianCollection.removeIf(item -> {
            Musician musician = (Musician)item;
            return musician.getBand().getID() != getID();
        });
        Vector<Musician> musicians = new Vector<>();
        for (DataItem item : musicianCollection) {
            musicians.add((Musician)item);
        }
        return musicians;
    }

    public String toStringValue() {
        String result = "" + m_ID + ": " + m_name + " (" + m_formYear + "-";
        if (m_disbandYear != 0)
            result += m_disbandYear + ")";
        else result += "настоящее время)";

        return result;
    }
}
