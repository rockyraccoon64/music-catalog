package music.database.items;

public class Genre {
    private final int m_ID;
    private String m_name;

    public Genre(int id, String name) {
        m_ID = id;
        m_name = name;
    }

    public int getID() {
        return m_ID;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }
}
