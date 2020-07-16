package music.database;

public class WrongAlbumException extends Exception {
    public WrongAlbumException() {

    }

    public WrongAlbumException(String message) {
        super(message);
    }
}
