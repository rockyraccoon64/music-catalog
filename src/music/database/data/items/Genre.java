package music.database.data.items;

public class Genre extends DataItem {
    private String m_name;

    public Genre(int id, String name) {
        super(id, SQLItem.GENRES);
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }
}
