package music.database.items;

import music.database.SQLItem;

public class Instrument extends DataItem {
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
}
