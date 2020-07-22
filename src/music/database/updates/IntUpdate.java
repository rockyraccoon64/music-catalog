package music.database.updates;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntUpdate extends Update {

    public IntUpdate(String field, int value) {
        super(UpdateType.INT, field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setInt(index, (int)getValue());
    }
}
