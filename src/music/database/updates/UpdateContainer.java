package music.database.updates;

import music.database.items.DataItem;

import java.util.Vector;

public class UpdateContainer {
    private final DataItem ITEM;
    private final Vector<Update> UPDATES;

    public UpdateContainer(DataItem item, Vector<Update> updates) {
        ITEM = item;
        UPDATES = updates;
    }

    public DataItem getItem() {
        return ITEM;
    }

    public Vector<Update> getUpdates() {
        return UPDATES;
    }
}
