package music.database.updates;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlobUpdate extends Update {

    public BlobUpdate(String field, byte[] value) {
        super(field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setBinaryStream(index, new ByteArrayInputStream((byte[])getValue()));
    }
}
