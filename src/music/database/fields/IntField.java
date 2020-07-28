package music.database.fields;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntField extends Field {

    public IntField(String field, int value) {
        super(field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setInt(index, (int)getValue());
    }
}
