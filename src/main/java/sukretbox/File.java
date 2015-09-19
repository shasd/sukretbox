package sukretbox;

/**
 * Created by sukret on 8/12/15.
 * File metadata
 */
public class File {

    private String userName;
    private String fileName;
    private long size;
    private long hash;
    private Storage storage;

    public File(String userName, String fileName, long size, long hash, Storage storage) {

        this.userName = userName;
        this.fileName = fileName;
        this.size = size;
        this.hash = hash;
        this.storage = storage;
    }

    public File() {
        fileName = "";
        size = 0;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }


    public enum Storage {
        NONE,
        SUKRETBOX,
        DROPBOX;

        @Override
        public String toString() {
            if(this == NONE)
                return "NONE";
            if(this == SUKRETBOX)
                return "SUKRETBOX";
            if(this == DROPBOX)
                return "DROPBOX";
            return "NONE";
        }

        public static Storage map(String val) {
            if(val.equals("SUKRETBOX"))
                return Storage.SUKRETBOX;
            if(val.equals("DROPBOX"))
                return Storage.DROPBOX;
            return Storage.NONE;
        }
    }

}
