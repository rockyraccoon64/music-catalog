package music.database.updates;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NStringUpdate extends Update {
    public NStringUpdate(String field, String value) {
        super(field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setNString(index, (String)getValue());
    }
}
