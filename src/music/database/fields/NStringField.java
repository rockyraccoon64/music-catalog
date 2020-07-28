package music.database.fields;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NStringField extends Field {
    public NStringField(String field, String value) {
        super(field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setNString(index, (String)getValue());
    }
}
