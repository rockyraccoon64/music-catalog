package music.database.fields;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlobField extends Field {

    public BlobField(String field, byte[] value) {
        super(field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setBinaryStream(index, new ByteArrayInputStream((byte[])getValue()));
    }
}
