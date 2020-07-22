package music.database.updates;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Update {

    enum UpdateType {
        SHORT,
        INT,
        STRING,
        BLOB
    }

    private UpdateType m_type;
    private String m_field;
    private Object m_value;

    public Update(UpdateType type, String field, Object value) {
        m_type = type;
        m_field = field;
        m_value = value;
    }

    public UpdateType getType() {
        return m_type;
    }

    public String getField() {
        return m_field;
    }

    public Object getValue() {
        return m_value;
    }

    public abstract void prepareStatement(PreparedStatement pstmt, int index) throws SQLException;
}
