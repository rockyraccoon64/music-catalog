package music.database.items.makers;

import music.database.SQLItem;
import music.database.items.DataItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.TreeMap;

public abstract class DataItemMaker<T extends DataItem> {

    private static final HashMap<SQLItem, DataItemMaker> MAP = new HashMap<>();

    public TreeMap<Integer, T> makeAll(ResultSet resultSet) throws SQLException {
        TreeMap<Integer, T> map = new TreeMap<>();
        while (resultSet.next()) {
            T item = make(resultSet);
            map.put(item.getID(), item);
            System.out.println(toStringValue(item));
        }
        return map;
    }

    public static DataItemMaker getInstance(SQLItem type) {
        DataItemMaker maker = MAP.get(type);
        if (maker == null) {
            switch (type) {
                case BANDS:
                    maker = BandMaker.getInstance();
                    break;
                case ALBUMS:
                    maker = AlbumMaker.getInstance();
                    break;
                case MUSICIANS:
                    maker = MusicianMaker.getInstance();
                    break;
                case GENRES:
                    maker = GenreMaker.getInstance();
                    break;
                case INSTRUMENTS:
                    maker = InstrumentMaker.getInstance();
                    break;
                case SONGS:
                    maker = SongMaker.getInstance();
                default:
                    break;
            }
            if (maker != null) {
                MAP.put(type, maker);
            }
        }
        return maker;
    }

    public abstract T make(ResultSet resultSet) throws SQLException;

    public abstract String toStringValue(T item);

}
