package music.database.items;

public class DataItem {
    protected final int m_ID;

    protected DataItem(int id) {
        m_ID = id;
    }

    public int getID() {
        return m_ID;
    }
}
