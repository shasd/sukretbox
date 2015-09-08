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
    private byte stored; // 1 if this file is actually stored under this users name

    public File(String userName, String fileName, long size, long hash, byte stored) {

        this.userName = userName;
        this.fileName = fileName;
        this.size = size;
        this.hash = hash;
        this.stored = stored;
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

    public byte getStored() {
        return stored;
    }

    public void setStored(byte stored) {
        this.stored = stored;
    }


}
