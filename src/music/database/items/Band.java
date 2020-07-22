package music.database.items;

import music.database.SQLItem;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

public class Band extends ImageContainer {
    private String m_name;
    private short m_formYear;
    private short m_disbandYear;
    private TreeSet<Musician> m_musicians;
    private TreeSet<Album> m_albums;

    public Band(int id, String name, short formYear, short disbandYear, byte[] image) {
        super(id, SQLItem.BANDS, image);
        m_name = name;
        m_formYear = formYear;
        m_disbandYear = disbandYear;
        m_musicians = new TreeSet<>(new Comparator<Musician>() {
           @Override
           public int compare(Musician o1, Musician o2) {
               int id1 = o1.getID();
               int id2 = o2.getID();
               return id1 - id2;
           }
        });
        m_albums = new TreeSet<>(new Comparator<Album>() {
           @Override
           public int compare(Album o1, Album o2) {
               LocalDate firstDate = o1.getReleaseDate();
               LocalDate secondDate = o2.getReleaseDate();
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
               return dateComparison == 0 ? o1.getName().compareTo(o2.getName()) : dateComparison;
           }
        });
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

    public void addMusician(Musician musician) {
        m_musicians.add(musician);
    }

    public void removeMusician(Musician musician) {
        m_musicians.remove(musician);
    }

    public void addAlbum(Album album) {
        m_albums.add(album);
    }

    public void removeAlbum(Album album) {
        m_albums.add(album);
    }

    public TreeSet<Album> getAlbums() {
        return m_albums;
    }

    public TreeSet<Musician> getMusicians() {
        return m_musicians;
    }

    public String toStringValue() {
        String result = "" + m_ID + ": " + m_name + " (" + m_formYear + "-";
        if (m_disbandYear != 0)
            result += m_disbandYear + ")";
        else result += "настоящее время)";

        return result;
    }
}
