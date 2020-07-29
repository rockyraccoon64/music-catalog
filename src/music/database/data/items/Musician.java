package music.database.data.items;

import music.database.data.DataStorage;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.Vector;

public class Musician extends DataItem {
    private String m_name;
    private LocalDate m_birthDate;
    private LocalDate m_deathDate;
    private WeakReference<Band> m_band;

    public Musician(int id, String name, Band band, LocalDate birthDate, LocalDate deathDate) {
        super(id, SQLItem.MUSICIANS);
        m_name = name;
        m_band = new WeakReference<>(band);
        m_birthDate = birthDate;
        m_deathDate = deathDate;
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

    public Vector<Instrument> getInstruments() {
        Vector<Instrument> instruments = new Vector<>();
        Vector<Integer> idSet = DataStorage.getInstrumentIDs(this);
        for (Integer id : idSet) {
            instruments.add((Instrument) DataStorage.getItemByID(SQLItem.INSTRUMENTS, id));
        }
        return instruments;
    }

//    public String toStringValue() {
//        if (m_instruments.isEmpty()) {
//            return m_name;
//        }
//
//        StringBuilder sb = new StringBuilder(m_name);
//
//        sb.append(" - ");
//
//        boolean isFirstInstrument = true;
//
//        for (Instrument instrument : m_instruments) {
//            if (!isFirstInstrument) {
//                sb.append(", ");
//            }
//            sb.append(instrument.getName());
//            if (isFirstInstrument) {
//                isFirstInstrument = false;
//            }
//        }
//
//        return sb.toString();
//    }
}
