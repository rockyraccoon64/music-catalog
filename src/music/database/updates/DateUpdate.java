package music.database.updates;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DateUpdate extends Update {

    public DateUpdate(String field, Date value) {
        super(field, value);
    }

    @Override
    public void prepareStatement(PreparedStatement pstmt, int index) throws SQLException {
        Date date = (Date)getValue();
        long dateMilliseconds = date.getTime();
        pstmt.setDate(index, new java.sql.Date(dateMilliseconds));
    }
}
