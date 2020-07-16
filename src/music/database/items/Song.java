package music.database.items;

import java.lang.ref.WeakReference;

public class Song {
    private final int m_ID;
    private String m_name;
    private WeakReference<Album> m_album;
    private int m_trackNo;

    public Song(int id, String name, Album album, int trackNo) {
        m_ID = id;
        m_name = name;
        m_album = new WeakReference<>(album);
        m_trackNo = trackNo;
    }

    public int getID() {
        return m_ID;
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
}
