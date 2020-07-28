package music.database.fields;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShortField extends Field {

    public ShortField(String field, short value) {
        super(field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setShort(index, (short)getValue());
    }
}
