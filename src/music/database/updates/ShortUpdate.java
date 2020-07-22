package music.database.updates;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShortUpdate extends Update {

    public ShortUpdate(String field, short value) {
        super(UpdateType.SHORT, field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setShort(index, (short)getValue());
    }
}
