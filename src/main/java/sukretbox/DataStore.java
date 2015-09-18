package sukretbox;

import java.util.List;

public interface DataStore {

    byte[] getData(String userName, String fileName);
    boolean storeData(String userName, String fileName, byte[] data);
    public List<File> listFiles(String userName);
    public boolean deleteData(String userName, String fileName);
    public boolean copyFile(String userName, String fileName, String newUserName, String newFileName);
}
