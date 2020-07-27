package music.database.items;

import music.database.SQLItem;

public class Instrument extends DataItem implements Comparable<Instrument> {
    private String m_name;

    public Instrument(int id, String name) {
        super(id, SQLItem.INSTRUMENTS);
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    @Override
    public int compareTo(Instrument o) {
        return this.getName().compareTo(o.getName());
    }
}
