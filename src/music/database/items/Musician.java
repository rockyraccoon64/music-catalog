package music.database.items;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Set;

public class Musician {
    private final int m_ID;
    private String m_name;
    private Date m_birthDate;
    private Date m_deathDate;
    private WeakReference<Band> m_band;
    private Set<Instrument> m_instruments;

    public Musician(int id, String name, Band band, Date birthDate, Date deathDate) {
        m_ID = id;
        m_name = name;
        m_band = new WeakReference<>(band);
        m_birthDate = birthDate;
        m_deathDate = deathDate;
    }

    public int getID() {
        return m_ID;
    }

    public String getName() {
        return m_name;
    }

    public Date getBirthDate() {
        return m_birthDate;
    }

    public Date getDeathDate() {
        return m_deathDate;
    }

    public Band getBand() {
        return m_band.get();
    }

    public Instrument[] getInstruments() {
        return (Instrument[])(m_instruments.toArray());
    }

    public void setName(String name) {
        m_name = name;
    }

    public void setBand(Band band) {
        m_band = new WeakReference<>(band);
    }

    public void setBirthDate(Date birthDate) {
        m_birthDate = birthDate;
    }

    public void setDeathDate(Date deathDate) {
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
