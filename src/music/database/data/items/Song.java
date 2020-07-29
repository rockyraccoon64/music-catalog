package music.database.data.items;

import java.lang.ref.WeakReference;

public class Song extends DataItem implements Comparable<Song> {
    private String m_name;
    private WeakReference<Album> m_album;
    private int m_trackNo;

    public Song(int id, String name, Album album, int trackNo) {
        super(id, SQLItem.SONGS);
        m_name = name;
        m_album = new WeakReference<>(album);
        m_trackNo = trackNo;
    }

    public String getName() {
        return m_name;
    }

    public int getTrackNo() {
        return m_trackNo;
    }

    public Album getAlbum() {
        return m_album.get();
    }

    public void setName(String name) {
        m_name = name;
    }

    public void setAlbum(Album album) {
        m_album = new WeakReference<>(album);
    }

    public void setTrackNo(int trackNo) {
        m_trackNo = trackNo;
    }

    @Override
    public int compareTo(Song o) {
        return (this.getAlbum().getID() == o.getAlbum().getID()) ? this.getTrackNo() - o.getTrackNo() : 0;
    }
}
