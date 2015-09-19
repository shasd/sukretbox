package sukretbox;

import java.util.List;

// interface for data storage technologies: memory, dropbox, S3 etc.

public interface DataStore {

    byte[] getData(String userName, String fileName);
    boolean storeData(String userName, String fileName, byte[] data);
    public boolean deleteData(String userName, String fileName);
    public boolean copyFile(String userName, String fileName, String newUserName, String newFileName);
    //public List<File> listFiles(String userName);
}
