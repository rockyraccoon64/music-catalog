package music.database.items;

import music.database.SQLItem;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.TreeSet;

public class Musician extends DataItem {
    private String m_name;
    private LocalDate m_birthDate;
    private LocalDate m_deathDate;
    private WeakReference<Band> m_band;
    private TreeSet<Instrument> m_instruments;

    public Musician(int id, String name, Band band, LocalDate birthDate, LocalDate deathDate) {
        super(id, SQLItem.MUSICIANS);
        m_name = name;
        m_band = new WeakReference<>(band);
        m_birthDate = birthDate;
        m_deathDate = deathDate;
        m_instruments = new TreeSet<>(new Comparator<Instrument>() {
           @Override
           public int compare(Instrument o1, Instrument o2) {
               String name1 = o1.getName();
               String name2 = o2.getName();
               return name1.compareTo(name2);
           }
        });
    }

    public String getName() {
        return m_name;
    }

    public LocalDate getBirthDate() {
        return m_birthDate;
    }

    public LocalDate getDeathDate() {
        return m_deathDate;
    }

    public Band getBand() {
        return m_band.get();
    }

    public TreeSet<Instrument> getInstruments() {
        return m_instruments;
    }

    public void setName(String name) {
        m_name = name;
    }

    public void setBand(Band band) {
        m_band = new WeakReference<>(band);
    }

    public void setBirthDate(LocalDate birthDate) {
        m_birthDate = birthDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        m_deathDate = deathDate;
    }

    public void addInstrument(Instrument instrument) {
        m_instruments.add(instrument);
    }

    public void removeInstrument(Instrument instrument) {
        m_instruments.remove(instrument);
    }

    public String toStringValue() {
        if (m_instruments.isEmpty()) {
            return m_name;
        }

        StringBuilder sb = new StringBuilder(m_name);

        sb.append(" - ");

        boolean isFirstInstrument = true;

        for (Instrument instrument : m_instruments) {
            if (!isFirstInstrument) {
                sb.append(", ");
            }
            sb.append(instrument.getName());
            if (isFirstInstrument) {
                isFirstInstrument = false;
            }
        }

        return sb.toString();
    }
}
