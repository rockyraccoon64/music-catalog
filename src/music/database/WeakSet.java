package music.database;

import music.database.items.DataItem;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.TreeSet;

public class WeakSet<T extends DataItem> {

    private final TreeSet<WeakReference<T>> SET;
    private final Comparator<T> COMP;

    public WeakSet(Comparator<T> comp) {
        SET = new TreeSet<>(new Comparator<WeakReference<T>>() {
            @Override
            public int compare(WeakReference<T> o1, WeakReference<T> o2) {
                return comp.compare(o1.get(), o2.get());
            }
        });
        COMP = comp;
    }

    public void add(T item) {
        SET.add(new WeakReference<T>(item));
    }

    public void remove(T item) {
        SET.remove(new WeakReference<T>(item));
    }

    public TreeSet<T> get() {
        TreeSet<T> hardSet = new TreeSet<>(COMP);
        for (WeakReference<T> ref : SET) {
            hardSet.add(ref.get());
        }
        return hardSet;
    }
}
