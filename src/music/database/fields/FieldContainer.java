package music.database.fields;

import music.database.data.items.DataItem;

import java.util.Vector;

public class FieldContainer {
    private final DataItem ITEM;
    private final Vector<Field> Fields;

    public FieldContainer(DataItem item, Vector<Field> fields) {
        ITEM = item;
        Fields = fields;
    }

    public DataItem getItem() {
        return ITEM;
    }

    public Vector<Field> getUpdates() {
        return Fields;
    }
}
