package music.database.items;

import music.database.SQLItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

public abstract class DataItem {
    protected final int m_ID;
    protected final SQLItem m_type;

    protected DataItem(int id, SQLItem type) {
        m_ID = id;
        m_type = type;
    }

    public int getID() {
        return m_ID;
    }

    public SQLItem getType() {
        return m_type;
    }

    public abstract String getName();
}
