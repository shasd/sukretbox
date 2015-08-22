package sukretbox;

/**
 * Created by sukret on 8/12/15.
 */
public class File {

    private String userName;
    private String fileName;
    private long size;

    public File() {
        fileName = "";
        size = 0;
    }

    public File(String userName, String fileName, long size) {
        this.userName = userName;
        this.fileName = fileName;
        this.size = size;
    }

    public String getUserName() {
        return userName;
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
}
