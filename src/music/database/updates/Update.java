package music.database.updates;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Update {

    private String m_field;
    private Object m_value;

    public Update(String field, Object value) {
        m_field = field;
        m_value = value;
    }

    public String getField() {
        return m_field;
    }

    public Object getValue() {
        return m_value;
    }

    public abstract void prepareStatement(PreparedStatement pstmt, int index) throws SQLException;
}
