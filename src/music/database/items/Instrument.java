package music.database.items;

public class Instrument extends DataItem {
    private String m_name;

    public Instrument(int id, String name) {
        super(id);
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }
}
